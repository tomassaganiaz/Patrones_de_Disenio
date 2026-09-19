# Corrección de Errores — Guía sin romper la entrega base

> **Objetivo:** diagnosticar y corregir errores comunes preservando los 5 casos de `src/main/java/com/tienda/Main.java:18` y el contrato de `src/main/java/com/tienda/strategy/EstrategiaPrecio.java:1` y `src/main/java/com/tienda/chain/Handler.java:11`.
> **Regla de oro:** *agregar* código en `challenge/`/`future/`/`fix/` o con wrappers; **no editar** `strategy/*`, `chain/*`, `model/*` ya entregados. Si hay que tocar un archivo base, hacerlo vía *branch* + *tests de regresión*.

---

## 1. Contrato a proteger (tests de regresión)

Antes de tocar nada, deja estos 5 casos como barrera:

| # | Compra | Estrategias | validadores | Esperado |
|---|--------|-------------|-------------|----------|
| 1 | `Compra(100000,VIP,12,EXPRESS)` + VIP/Cantidad/Promo5%/Envio (`Main.java:27`) | — | `clienteOk,stockOk,pagoOk` | `$82675.00` + `Pedido aprobado` (`Main.java:89`) |
| 2 | `Compra(50000,PREMIUM,8,NORMAL)` | — | `stockOk=false` | `$47750.00` + corta en `Validar Stock` |
| 3 | `Compra(75000,COMUN,3,RETIRO)` + Promo10% | — | `pagoOk=false` | `$67500.00` + corta en `Validar Pago` |
| 4 | Desafío BlackFriday + límite 200k (`Main.java:70`) | — | todo ok | `$64506.25` + aprobado |
| 5 | Desafío BlackFriday + límite 150k (`Main.java:75`) | — | todo ok | `$223750.00` + `ERROR Limite excedido` |

Si agregás JUnit (futuro), portá estos casos a `src/test/java/com/tienda/RegresionTest.java`. Sin JUnit, verificá con `run.bat` / `java -cp out com.tienda.Main`.

---

## 2. Errores frecuentes y corrección no destructiva

### E01 — Orden de estrategias incorrecto (envío antes de descuentos)
**Síntoma:** precio final distinto al enunciado (ej. $100k → Express +10k → luego -15% da $93500 en vez de $82675).
**Causa:** `calc.agregarEstrategia(new CostoEnvio())` antes de descuentos.
**Diagnóstico:** ver log `[STRATEGY]` de `CalculadoraPrecio.java:33` — el delta de envío debe ser el último.
**Corrección sin romper:** no reordenar `Main` existente; para nuevos casos usar `EstrategiaFactory` o documentar regla: *descuentos primero, `CostoEnvio` último, impuestos al final*.
```java
// futuro: factory garantiza orden
calc.agregarEstrategia(new DescuentoClienteVIP());
calc.agregarEstrategia(new DescuentoPorCantidad());
calc.agregarEstrategia(new PromocionEspecial(...));
calc.agregarEstrategia(new DescuentoBlackFriday()); // desafíos antes de envío
calc.agregarEstrategia(new CostoEnvio()); // siempre último
```

### E02 — `double` con redondeo / `String.format` con locale
**Síntoma:** `$82675,00` vs `$82675.00` según `Locale`, o `0.10000000000000009`.
**Causa:** `double` + `String.format("%.2f")` sin `Locale.US`.
**Corrección no destructiva:** wrapper sin tocar `CalculadoraPrecio.java:33`:
```java
public class PrecioUtil { public static String fmt(double v){ return String.format(java.util.Locale.US, "%.2f", v); } }
```
Para cálculo exacto futuro: migrar a `BigDecimal` en nuevas estrategias (ej. `EstrategiaBigDecimal`), mantener `double` en las base para no romper.

### E03 — `NullPointerException` en `Compra` / `Pedido`
**Síntoma:** `Handler.java:26` o `DescuentoClienteVIP.java` con `compra == null`.
**Causa:** `new Pedido(null, ...)` o `compra.getTipoCliente()` nulo.
**Diagnóstico:** `ValidadorCliente.java` ya chequea `pedido.getCompra()==null` — debe ser el **primer** handler (`Main.java:122`).
**Corrección:** no tocar validadores; agregar guard en fábrica:
```java
if(compra==null) throw new IllegalArgumentException("Compra requerida");
if(compra.getTipoCliente()==null) throw new IllegalArgumentException("TipoCliente requerido");
```
Y en `CalculadoraPrecio.calcular()` agregar precondición sin cambiar firma (sobrecarga).

### E04 — Cadena mal encadenada (`setSiguiente` no enlaza)
**Síntoma:** solo corre `Validar Cliente`, nunca llega a Stock/Pago.
**Causa:** `cliente.setSiguiente(stock); pago.setSiguiente(...)` en vez de `cliente.setSiguiente(stock).setSiguiente(pago)` (`Handler.java:14` retorna `siguiente` para fluent).
**Diagnóstico:** `chain/Handler.java:17` y `Main.java:126`.
**Corrección:** usar `CadenaBuilder` (ver `FUTURAS_IMPLEMENTACIONES.md#4.2`) — no modifica `Handler`.

### E05 — `Handler.handle()` override accidental
**Síntoma:** cadena no corta en ERROR.
**Causa:** subclase hace `@Override handle()` en vez de `validar()`.
**Prevención:** `Handler.java:25` es `final` — ya está protegido. No quitar `final`. Si alguien lo quitó, revertir.

### E06 — Descuento aplicado dos veces / estrategia duplicada
**Síntoma:** `-30%` en vez de `-15%`.
**Causa:** `calc.agregarEstrategia(new DescuentoClienteVIP())` dos veces o `DescuentoPorCantidad` + estrategia separada `DescuentoCantidad10`.
**Corrección:** en `CalculadoraPrecio` agregar `Set` check opcional en paquete `future` (decorator `UnicaEstrategiaDecorator`), no tocar `strategy/*` base. Test de regresión lo detecta (caso 1 debe ser 82675).

### E07 — `Pedido` con flags invertidos
**Síntoma:** caso 2 (sin stock) aparece aprobado.
**Causa:** `new Pedido(compra, precio, true, true, true)` con boolean en orden equivocado (`Pedido.java` constructor `(clienteValido, stockDisponible, pagoValido)`).
**Diagnóstico:** revisar `src/main/java/com/tienda/model/Pedido.java:1` — documentar orden o usar builder futuro:
```java
Pedido p = new PedidoBuilder(compra, precio).clienteValido(true).stock(false).pago(true).build();
```

### E08 — Encoding / compilación con `javac` en Windows (ñ, tildes)
**Síntoma:** `error: unmappable character for encoding Cp1252`.
**Causa:** `compile.bat` sin `-encoding UTF-8`.
**Corrección:** ya está `javac -encoding UTF-8` en `compile.bat:1` y `pom.xml:1` (`UTF-8`). No cambiar.

### E09 — Java version mismatch (se exige 1.8)
**Síntoma:** `unsupported major.minor` o lambdas no compilables.
**Diagnóstico:** `java -version` (debe ser 1.8, ver `pom.xml:1` `maven.compiler.source=1.8`).
**Corrección:** no subir a 17 en `pom.xml` sin rama separada; nuevas features compatibles con 1.8.

### E10 — Lógica de `DescuentoPorCantidad` mal entendida
**Síntoma:** 6 productos da -10% en vez de -5%.
**Causa:** confundir `>5` y `>10` como acumulativos.
**Implementación correcta:** `DescuentoPorCantidad.java:1` — `if >10 → -10% else if >5 → -5%` (no acumulativo). Si se requieren dos clases separadas, crear `DescuentoMayor5` y `DescuentoMayor10` en `future/` y no modificar la base.

---

## 3. Procedimiento de corrección seguro (paso a paso)

1. **Reproducir:** `run.bat` → capturar salida. Crear `out/expected.txt` con los 5 casos buenos.
2. **Aislar:** crear branch `fix/E0X-descripcion`.
3. **No tocar base:** implementar fix en `src/main/java/com/tienda/future/fix/` o como wrapper/decorator.
   - Ej. bug de orden → `OrdenadoCalculadora` que delega a `CalculadoraPrecio` reordenando.
   - Ej. bug de NPE → `SafePedidoFactory`.
4. **Test de regresión:** `java -cp out com.tienda.Main > out/actual.txt && fc out\expected.txt out\actual.txt` (Windows) o `diff` (Unix). Debe dar 0 diff en los 5 casos.
5. **Si hay que tocar base:** hacer cambio mínimo, documentar en commit y actualizar `docs/diagrama.puml` y `README.md` solo si cambia contrato.
6. **Merge:** solo si `compile.bat` + `run.bat` pasan y `git diff master -- src/main/java/com/tienda/strategy src/main/java/com/tienda/chain src/main/java/com/tienda/model` es vacío o intencional.

---

## 4. Checklist de no-regresión

- [ ] `compile.bat` sin warnings con `-Xlint` (agregar temporalmente para revisión)
- [ ] `run.bat` imprime los 5 casos con mismos montos y mensajes (`ResultadoValidacion.java:1` `OK`/`ERROR`)
- [ ] `Handler.handle()` sigue `final` y corta en ERROR (`Handler.java:25`)
- [ ] `CalculadoraPrecio` sigue dependiendo de `EstrategiaPrecio` (DIP) y no de concretos
- [ ] `DescuentoBlackFriday` y `ValidadorLimiteCompra` siguen en `challenge/` sin copiarse a `strategy/`/`chain/`
- [ ] `out/` no commiteado (ver `.gitignore:1`)
- [ ] `pom.xml` sigue en `1.8` y `UTF-8`

---

## 5. Plantilla de issue/PR

```markdown
**Bug:** E0X — título
**Repro:** pasos + salida actual vs esperada (caso #)
**Causa raíz:** ...
**Fix sin romper:** paquete `future/fix/` + clase `XxxFix` (no toca `strategy/`/`chain/`)
**Verificación:** `run.bat` diff 0, caso 1=$82675.00, caso 2 corta en Stock, caso 3 corta en Pago
**Riesgo OCP:** bajo/medio — ¿requiere tocar base? [ ] no [ ] sí (justificar)
```

---

## 6. Cuándo sí tocar la base (excepción)

Solo si el bug está **dentro** de un archivo base (ej. `CostoEnvio` suma 5000 en vez de 5000.00). Entonces:
- Fix mínimo en ese archivo, un commit, y tag `v1.0.1`.
- Actualizar `README.md` tabla de estrategias y `docs/diagrama.puml` si aplica.
- Avisar que la entrega original cambió — preferible dejar base intacta y documentar workaround en `future/`.

Toda corrección debe dejar `Main` original ejecutable sin cambios para el corrector del TP.
