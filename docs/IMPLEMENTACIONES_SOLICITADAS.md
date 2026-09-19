# Implementaciones Solicitadas — Verificación TP Original (No Rompe)

> **Conclusión: NO ROMPE.** Todas las consignas de la primera solicitud siguen intactas en `model/`+`strategy/`+`chain/`+`challenge/`+`Main`. MVC (`controller/`+`view/`), correcciones (`util/`+`builder/`+`factory/`) y futuras (`future/`) son **aditivas** en paquetes nuevos y no modifican el contrato base. `Main` sigue mostrando 5 casos con `82675/47750/67500/64506.25/223750.00`.

## 1. Checklist Consignas Originales

| # | Consigna Original | Estado | Implementación `file:línea` | Evidencia |
|---|---|---|---|---|
| S1 | Regla Cliente común 0% | ✅ | `src/main/java/com/tienda/strategy/DescuentoClienteComun.java:1` | `aplicar(){return precio;}` |
| S2 | Cliente Premium 10% | ✅ | `src/main/java/com/tienda/strategy/DescuentoClientePremium.java:1` | `*0.90` |
| S3 | Cliente VIP 15% | ✅ | `src/main/java/com/tienda/strategy/DescuentoClienteVIP.java:1` | `*0.85` |
| S4 | Más de 5 productos 5% | ✅ | `src/main/java/com/tienda/strategy/DescuentoMayor5.java:1` | `n>5 && n<=10 → *0.95` |
| S5 | Más de 10 productos 10% | ✅ | `src/main/java/com/tienda/strategy/DescuentoMayor10.java:1` | `n>10 → *0.90` |
| S6 | Retiro $0 | ✅ | `src/main/java/com/tienda/strategy/CostoEnvio.java:5` | `RETIRO 0` |
| S7 | Envío normal $5000 | ✅ | `src/main/java/com/tienda/strategy/CostoEnvio.java:6` | `+5000` |
| S8 | Envío Express $10000 | ✅ | `src/main/java/com/tienda/strategy/CostoEnvio.java:7` | `+10000` |
| S9 | Promoción especial 10% parametrizable | ✅ | `src/main/java/com/tienda/strategy/PromocionEspecial.java:1` | `default 0.10` |
| S10 | Varias estrategias simultáneas | ✅ | `src/main/java/com/tienda/strategy/CalculadoraPrecio.java:6` + `src/main/java/com/tienda/controller/TiendaController.java:19` | `List<EstrategiaPrecio>` + `calcularPrecio(compra, List)` |
| S11 | Ejemplo $100.000 VIP -15% Cantidad -10% Promo -5% Express +10000 = $82675 | ✅ | `src/main/java/com/tienda/Main.java:22` + `src/test/java/com/tienda/strategy/CalculadoraPrecioTest.java:64` | `calc → 82675.00` verificado en Main y tests |
| S12 | Cada regla independiente (SRP/OCP) | ✅ | `src/main/java/com/tienda/strategy/EstrategiaPrecio.java:1` (interface ISP) | 6 clases `implements EstrategiaPrecio` |
| S13 | Agregar nuevas estrategias sin modificar existentes | ✅ | `src/main/java/com/tienda/challenge/DescuentoBlackFriday.java:1` | `implements EstrategiaPrecio` en paquete separado |
| C1 | Chain: Validar Cliente → Stock → Pago (mín 3) | ✅ | `src/main/java/com/tienda/chain/ValidadorCliente.java:1`, `ValidadorStock.java:1`, `ValidadorPago.java:1` + `ValidadorFraude.java:1` (4º opcional) | |
| C2 | `Handler` abstracto decide continuar/detener | ✅ | `src/main/java/com/tienda/chain/Handler.java:6` | `final handle()` corta en `!isAprobado()` |
| C3 | Cada Handler una responsabilidad | ✅ | `chain/Handler.java:6` | `validar()` + `getNombre()` |
| C4 | Pedido aprobado | ✅ | `src/main/java/com/tienda/Main.java:22` | Caso1 `VIP 12u Express` `cliente/stock/pago true` |
| C5 | Pedido rechazado por stock | ✅ | `src/main/java/com/tienda/Main.java:27` | Caso2 `stock false` corta en `Validar Stock` |
| C6 | Pedido rechazado por pago | ✅ | `src/main/java/com/tienda/Main.java:32` | Caso3 `pago false` corta en `Validar Pago` |
| C7 | Mostrar por consola estrategias, precio y pasos Chain | ✅ | `src/main/java/com/tienda/view/TiendaView.java:12` + `src/main/java/com/tienda/strategy/CalculadoraPrecio.java:13` (detalle) + `chain/Handler.java:9` (log `[Validar X]`) | Main imprime `[STRATEGY]`, `PRECIO FINAL`, `[CHAIN]` |
| C8 | Diagrama clases y relación patrones | ✅ | `docs/diagrama.puml:1` | PlantUML con `model/strategy/chain/challenge/controller/view` |
| C9 | Explicar 4 preguntas | ✅ | `src/main/java/com/tienda/view/TiendaView.java:28` + `src/main/java/com/tienda/Main.java:55` + `README.md:48` | Strategy/Chain, múltiples estrategias, rechazo |
| D1 | DescuentoBlackFriday sin modificar base | ✅ | `src/main/java/com/tienda/challenge/DescuentoBlackFriday.java:1` | `0.25` default, `challenge/` separado |
| D2 | ValidarLimiteCompra sin modificar base | ✅ | `src/main/java/com/tienda/challenge/ValidadorLimiteCompra.java:1` | `extends Handler` en `challenge/` |

**SOLID original intacto:** SRP `model/Compra.java:1`, OCP `challenge/` , LSP `EstrategiaPrecio.java:1`/`Handler.java:6`, ISP interface mínima, DIP `TiendaController.java:16` depende de abstracciones.

---

## 2. Detalle PARTE 1 — Strategy `src/main/java/com/tienda/strategy/`

**Interface:** `EstrategiaPrecio.java:1`
```java
double aplicar(double precio, Compra compra);
String getDescripcion();
```

| Archivo | Función | Clave |
|---|---|---|
| `DescuentoClienteComun.java:1` | `aplicar(){return precio;}` | 0% |
| `DescuentoClientePremium.java:1` | `*0.90` si PREMIUM | -10% |
| `DescuentoClienteVIP.java:1` | `*0.85` si VIP | -15% |
| `DescuentoPorCantidad.java:1` | compone `DescuentoMayor5`+`DescuentoMayor10` (compatibilidad) | volumen |
| `CostoEnvio.java:1` | `switch RETIRO 0 / NORMAL 5000 / EXPRESS 10000` | costo fijo |
| `PromocionEspecial.java:1` | `* (1-pct)` ctor `0.10` | promo parametrizable |
| `CalculadoraPrecio.java:1` | `agregarEstrategia()`, `calcular()`, `calcularConDetalle()` | Context, `List<EstrategiaPrecio>` ordenada |

**Combinación múltiple:** `TiendaController.java:19` `calcularPrecio(Compra, List<EstrategiaPrecio>)` itera en orden. Ejemplo verificado `Main.java:22` `82675.00`.

## 3. Detalle PARTE 2 — Chain `src/main/java/com/tienda/chain/`

**Handler.java:1**
```java
final ResultadoValidacion handle(Pedido p){ validar(); log; if(!ok) return; if(siguiente!=null) siguiente.handle(); }
```

| Archivo | Función | Mensaje |
|---|---|---|
| `ValidadorCliente.java:1` | `!isClienteValido` / `compra==null` | `Cliente no valido` |
| `ValidadorStock.java:1` | `!isStockDisponible` (+ `compra==null` guard) | `Stock insuficiente (N unidades)` |
| `ValidadorPago.java:1` | `!isPagoValido` / `precioFinal<=0` | `Pago rechazado` |
| `ValidadorFraude.java:1` | `>500k && !clienteValido` | `Posible fraude` (4º opcional) |
| `ResultadoValidacion.java:1` | `ok()/error()`, `isAprobado()`, `toString() OK: / ERROR:` | Value object |

**Cadena base:** `TiendaController.java:56` `crearCadenaBase()` → `Cliente.setSiguiente(Stock).setSiguiente(Pago)` (fluent). `crearCadenaCon(extra)` agrega sin tocar base (OCP).

## 4. Casos Solicitados `src/main/java/com/tienda/Main.java:1` (MVC, sin lógica)

```java
ejecutar(CASO 1 APROBADO, Compra(100000,VIP,12,EXPRESS), cadenaBase, true,true,true, VIP,Cantidad,Promo5%,Envio) // → 82675 CONFIRMADO
ejecutar(CASO 2 STOCK, Compra(50000,PREMIUM,8,NORMAL), cadenaBase, true,false,true, Premium,Cantidad,Envio) // → 47750 corta Stock
ejecutar(CASO 3 PAGO, Compra(75000,COMUN,3,RETIRO), cadenaBase, true,true,false, Comun,Cantidad,Promo10%,Envio) // → 67500 corta Pago
ejecutar(CASO 4 BLACKFRIDAY OK, Compra(100000,VIP,12,EXPRESS), cadenaCon(Limite200k), true,true,true, VIP,Cantidad,Promo5%,BlackFriday,Envio) // → 64506.25 CONFIRMADO
ejecutar(CASO 5 LIMITE FAIL, Compra(300000,COMUN,1,EXPRESS), cadenaCon(Limite150k), true,true,true, VIP,Cantidad,Promo5%,BlackFriday,Envio) // → 223750 ERROR Limite
```

**Consola:** `TiendaView.java:12` `mostrarEncabezado()`, `mostrarCompra()`, `mostrarCalculoDetalle()` → `[STRATEGY] ... PRECIO FINAL`, `mostrarChainHeader()` → `[CHAIN]`, `Handler.java:9` → `[Validar Cliente] OK: ...`, `mostrarResultadoFinal()` → `Resultado final: OK:/ERROR:` + `>>> PEDIDO CONFIRMADO/RECHAZADO <<<`.

## 5. Diagrama `docs/diagrama.puml:1`

Incluye `model (Compra,Pedido,TipoCliente,TipoEnvio)`, `strategy (EstrategiaPrecio 6 impl + CalculadoraPrecio)`, `chain (Handler 4 impl)`, `challenge (BlackFriday, LimiteCompra)`, `controller (TiendaController, ResultadoOperacion)`, `view (TiendaView)`, `Main` wiring. PlantUML render: https://www.plantuml.com/plantuml/ .

## 6. Explicaciones (4 Preguntas)

Ubicadas en `view/TiendaView.java:28` `mostrarExplicacion()` y `README.md:48`:

- **¿Qué problema resuelve Strategy?** Encapsula algoritmos intercambiables, evita `if` gigantes, OCP/SRP.
- **¿Por qué varias estrategias?** Descuentos/costos acumulativos y ortogonales (cliente+volumen+promo+envío).
- **¿Qué problema resuelve Chain?** Desacopla emisor/receptores, agrega validadores sin tocar cliente.
- **¿Qué pasa al rechazar?** `Handler.handle()` retorna `ERROR` y no invoca `siguiente` (cadena corta).

## 7. Desafío OCP `src/main/java/com/tienda/challenge/`

- `DescuentoBlackFriday.java:1` `implements EstrategiaPrecio` `* (1-0.25)` — agregado vía `Arrays.asList(..., new DescuentoBlackFriday(), new CostoEnvio())` en `Main.java:42`
- `ValidadorLimiteCompra.java:1` `extends Handler` `precioFinal > limite → ERROR` — agregado vía `controller.crearCadenaCon(new ValidadorLimiteCompra(200000))` `Main.java:42` y `150000` `Main.java:47`

No se tocó `strategy/*` ni `chain/*` base.

## 8. Extensiones No Rompen (aditivas)

| Paquete | Función | No toca base |
|---|---|---|
| `controller/TiendaController.java:16` | MVC, `calcularPrecio()`, `validarPedido()`, `procesarPedido()` testeable sin `Main` | Usa `strategy/chain` via DIP |
| `view/TiendaView.java:1` | Solo imprime | Sin lógica |
| `util/PrecioUtil.java:1` | `Locale.US` | Utility |
| `builder/PedidoBuilder.java:1` | Evita flags invertidos | Builder |
| `builder/CadenaBuilder.java:1` | Evita `setSiguiente` mal encadenado | Builder |
| `factory/EstrategiaFactory.java:1` | Orden `descuentos → CostoEnvio` | Factory |
| `future/strategy/*` | `Cupon`, `DescuentoTransferencia`, `ImpuestoIVA`, `EnvioGratisPorMonto`, `DescuentoEstacional` | Nuevas `EstrategiaPrecio` |
| `future/chain/*` | `ValidadorDireccion`, `ValidadorCupon`, `ValidadorEdad` | Nuevos `Handler` |

## 9. Tests (testeable sin Main)

- **Sin Maven:** `src/test/java/com/tienda/TestRunner.java:1` (19 tests) → `java -cp out com.tienda.TestRunner` `19 PASS` (Strategy 6, Chain 4, Controller 3, Desafío 4, Model 2)
- **JUnit5:** `src/test/java/com/tienda/strategy/CalculadoraPrecioTest.java:1`, `chain/ChainTest.java:1`, `controller/TiendaControllerTest.java:1`, `challenge/DesafioTest.java:1`, `model/ModelTest.java:1` → `mvn test` (requiere Maven)
- **Edge:** `PrecioUtil`, `PedidoBuilder`, `CadenaBuilder`, `EstrategiaFactory`, futuras `Cupon/IVA` verificados en `EdgeTest` manual.

## 10. Cómo Ejecutar

```bat
compile.bat          # for /R con comillas para "Patrones de diseño"
run.bat              # Main MVC 5 casos
test.bat             # TestRunner 19 PASS + mvn test si hay Maven
# manual
javac -d out -encoding UTF-8 -sourcepath src\main\java src\main\java\com\tienda\Main.java
java -cp out com.tienda.Main
```

`.gitignore:1` ignora `out/`, `target/`, `*.class`, `out_*.txt`.

## 11. Evidencia Verificación

```
CASO 1 => $82675.00 CONFIRMADO
CASO 2 => $47750.00 ERROR Stock
CASO 3 => $67500.00 ERROR Pago
CASO 4 => $64506.25 CONFIRMADO (BlackFriday)
CASO 5 => $223750.00 ERROR Limite
TestRunner => 19 PASS 0 FAIL
```

Todo lo solicitado está en `src/main/java/com/tienda/` y documentado. MVC y futuras son capas opcionales que no alteran el TP original.
