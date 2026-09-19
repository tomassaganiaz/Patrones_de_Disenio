# Futuras Implementaciones — Roadmap sin romper la entrega base

> **Restricción del TP:** toda extensión debe respetar **OCP** (abierto a extensión, cerrado a modificación). Ninguna de las ideas de abajo requiere tocar `strategy/*`, `chain/*` ni `model/*` existentes. Se implementan en paquetes nuevos (`future/`, `challenge/`) inyectándose vía `CalculadoraPrecio` y `Handler#setSiguiente()`.

Relacionado: `src/main/java/com/tienda/strategy/EstrategiaPrecio.java:1`, `src/main/java/com/tienda/strategy/CalculadoraPrecio.java:13`, `src/main/java/com/tienda/chain/Handler.java:11`, `src/main/java/com/tienda/Main.java:16`, `docs/diagrama.puml:28`.

---

## 1. Principio de extensión

```
existente (no tocar)  →  src/main/java/com/tienda/strategy/*.java
                         src/main/java/com/tienda/chain/*.java
                         src/main/java/com/tienda/model/*.java
nuevo (agregar)       →  src/main/java/com/tienda/future/strategy/*.java
                         src/main/java/com/tienda/future/chain/*.java
                         src/main/java/com/tienda/future/factory/*.java
inyección             →  Main / Factory / Builder crea y compone sin modificar clases base
```

Ver desafío ya resuelto como ejemplo: `src/main/java/com/tienda/challenge/DescuentoBlackFriday.java:1` y `src/main/java/com/tienda/challenge/ValidadorLimiteCompra.java:1`.

---

## 2. Nuevas Strategies (sin modificar `CalculadoraPrecio`)

### 2.1 Descuento por medio de pago
```java
package com.tienda.future.strategy;
import com.tienda.model.Compra;
import com.tienda.strategy.EstrategiaPrecio;

public class DescuentoTransferencia implements EstrategiaPrecio {
    @Override public double aplicar(double precio, Compra c) {
        // ej. 5% si se agrega campo medioPago en Compra
        return precio * 0.95;
    }
    @Override public String getDescripcion() { return "Pago Transferencia (-5%)"; }
}
```
Uso: `calc.agregarEstrategia(new DescuentoTransferencia());` — orden importa (antes de `CostoEnvio`).

### 2.2 Cupón / Código promocional (parametrizable)
```java
public class Cupon implements EstrategiaPrecio {
    private final String codigo; private final double pct;
    public Cupon(String codigo, double pct){ this.codigo=codigo; this.pct=pct; }
    @Override public double aplicar(double p, Compra c){ return p * (1-pct); }
    @Override public String getDescripcion(){ return "Cupón "+codigo+" (-"+(int)(pct*100)+"%)"; }
}
```

### 2.3 Impuesto / IVA (estrategia que *suma*)
No todas las estrategias descuentan. `CostoEnvio` ya demuestra que `aplicar()` puede sumar. Futuro `ImpuestoIVA(0.21)` reutiliza el mismo contrato.

### 2.4 Otras candidatas (backlog)
| Idea | Tipo | Nota |
|------|------|------|
| `DescuentoEstacional` | % | Navidad, CyberMonday — se puede componer con `DescuentoBlackFriday` |
| `DescuentoPrimeraCompra` | % | Requiere flag en `Pedido`/`Compra` |
| `Cashback` | % | No afecta precio, genera crédito — considerar `EstrategiaPostPrecio` |
| `EnvioGratisPorMonto` | condicional | Si `precio > 80000` → ignora `CostoEnvio` (decorator) |
| `EstrategiaCompuesta` | composite | Agrupa varias en una (ej. pack VIP+Cantidad) |

**Regla de oro:** siempre agregar al final de la cadena de descuentos y **antes** de `CostoEnvio`, salvo impuestos que van al final. Documentar orden en `CalculadoraPrecio`.

---

## 3. Nuevos Handlers (sin modificar `Handler` ni validadores existentes)

### 3.1 Validador de dirección / zona de envío
```java
package com.tienda.future.chain;
import com.tienda.chain.Handler;
import com.tienda.chain.ResultadoValidacion;
import com.tienda.model.Pedido;
public class ValidadorDireccion extends Handler {
    @Override protected ResultadoValidacion validar(Pedido p){
        // if (!zona.cubre(p.getDireccion())) return error(...)
        return ResultadoValidacion.ok("Dirección válida");
    }
    @Override protected String getNombre(){ return "Validar Dirección"; }
}
```
Encadenado: `pago.setSiguiente(new ValidadorDireccion()).setSiguiente(new ValidadorLimiteCompra(200000));`

### 3.2 Validador de edad / fraude avanzado / cupón / stock por variante
Misma plantilla. Cada handler **SRP**: una sola causa de rechazo y mensaje claro (ver `src/main/java/com/tienda/chain/ValidadorStock.java:1` y `ValidadorPago.java:1`).

### 3.3 Handler terminal: `Notificador` / `Persistidor`
No valida, siempre `ok()` pero con efecto lateral (enviar mail, guardar en BD). Mantiene `Handler` para no romper la cadena; alternativa futura: separar con `Observer`.

---

## 4. Cross-cutting: Factories y Builders (sin romper `Main` actual)

### 4.1 Factory de estrategias
```java
public class EstrategiaFactory {
    public static List<EstrategiaPrecio> paraCompra(Compra c){
        List<EstrategiaPrecio> l = new ArrayList<>();
        // switch por TipoCliente → DescuentoClienteVIP/Premium/Comun
        // if (c.getCantidadProductos()>5) l.add(new DescuentoPorCantidad());
        // l.add(new CostoEnvio());
        return l;
    }
}
```
`Main` actual sigue funcionando; el factory es **opcional** para nuevos clientes (API, tests).

### 4.2 Builder de cadena
```java
public class CadenaBuilder {
    private Handler head, tail;
    public CadenaBuilder add(Handler h){
        if(head==null) head=tail=h; else { tail.setSiguiente(h); tail=h; }
        return this;
    }
    public Handler build(){ return head; }
}
```
Evita errores de `setSiguiente()` mal encadenado (ver `docs/CORRECCION_ERRORES.md`).

### 4.3 Configuración externa
`estrategias.properties` / `validaciones.properties` + `ServiceLoader` para activar estrategias sin recompilar. Mantiene OCP a nivel despliegue.

---

## 5. Nuevos patrones sin reescribir lo entregado

| Patrón | Dónde encaja | Cómo no rompe |
|--------|-------------|---------------|
| **Decorator** | Envolver `EstrategiaPrecio` (ej. `LogEstrategia`, `TopeDescuento`) | Decora cualquier `EstrategiaPrecio` existente |
| **Observer** | Notificar confirmación/rechazo a mail, stock, analytics | `Pedido` como subject, handlers notifican |
| **Template Method** | Ya está en `Handler.handle()` (`Handler.java:25`) | Nuevos handlers solo implementan `validar()` |
| **Composite** | `PackEstrategias implements EstrategiaPrecio` | Trata N estrategias como una |
| **Strategy + Factory** | Crear pack según perfil | Factory decide, `CalculadoraPrecio` no cambia |

---

## 6. Modelo y persistencia

- **Extender `Compra`/`Pedido` sin modificar:** herencia (`CompraConCupon extends Compra`) o composición (`CompraWrapper` con `Compra` + `cupon`). Preferir composición para no romper LSP.
- **Nuevos campos:** `medioPago`, `direccion`, `codigoCupon`, `fecha`, `usuarioId` — agregar vía nuevo constructor + delegar al viejo (sobrecarga), nunca eliminar el constructor usado en `Main.java:27`.
- **Persistencia:** `PedidoRepository` (interface DIP) con `InMemoryPedidoRepository` para tests; JPA después.

---

## 7. Testing y calidad

- **Tests que protegen la entrega base:** crear `src/test/java/com/tienda/StrategyTest.java` y `ChainTest.java` que repliquen los 5 casos de `Main.java:18` (aprobado, sin stock, pago fallido, BlackFriday aprobado/rechazado). Toda futura PR debe pasarlos.
- **Tests parametrizados:** `CalculadoraPrecioTest` con tabla `precioBase, cliente, cantidad, envio → precioEsperado`.
- **Contrato `EstrategiaPrecio`:** test que toda implementación cumpla `precio >=0` y `getDescripcion() != null`.
- **Mutation / orden:** test que verifique que `CostoEnvio` siempre es el último descuento aplicado.

---

## 8. API / UI (evolución)

- **REST:** `POST /calcular` recibe `CompraDTO` → `EstrategiaFactory` → `CalculadoraPrecio` → `Pedido` → cadena → `ResultadoValidacion`. `Main` queda como demo CLI, no se borra.
- **CLI mejorado:** args `java -cp out com.tienda.Main --cliente VIP --cantidad 12 --envio EXPRESS`.
- **Logging:** reemplazar `System.out.println` de `CalculadoraPrecio.java:33` y `Handler.java:28` por `java.util.logging` o SLF4J con adapter, sin cambiar lógica.

---

## 9. Checklist antes de implementar

- [ ] ¿Nueva clase en `future/` o `challenge/` (no en `strategy/`/`chain/`)?
- [ ] ¿Implementa `EstrategiaPrecio` o extiende `Handler` sin copiar/pegar validación existente?
- [ ] ¿Caso de test que replique los 5 casos base y falle si los rompo?
- [ ] ¿Diagrama `docs/diagrama.puml` actualizado en sección `future`?
- [ ] ¿`compile.bat` / `mvn compile` y `run.bat` siguen pasando?
- [ ] ¿README con nueva fila en tabla de estrategias/handlers?

---

## 10. Orden sugerido (MVP → Avanzado)

1. `Cupon` + `ValidadorCupon` (valor inmediato)
2. `EstrategiaFactory` + `CadenaBuilder` (reduce error humano)
3. Tests JUnit de los 5 casos (protege la entrega)
4. `ImpuestoIVA` + `EnvioGratisPorMonto` (casos de negocio reales)
5. REST `POST /pedidos` + `PedidoRepository`
6. Observer `NotificadorMail` + Decorator `LogEstrategia`

Toda esta hoja de ruta **no modifica** archivos de la entrega base; solo los **usa**.
