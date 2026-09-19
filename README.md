# Tienda Online — Strategy + Chain of Responsibility + SOLID + MVC

Trabajo Práctico — Patrones de Diseño en Java 8

## Estructura MVC

```
src/main/java/com/tienda/
  model/       → Compra, Pedido, TipoCliente, TipoEnvio, ResultadoValidacion (Model SRP)
  strategy/    → EstrategiaPrecio (interface) + 6 estrategias + CalculadoraPrecio (Strategy Context)
  chain/       → Handler (abstract) + 4 validadores (Chain)
  challenge/   → DescuentoBlackFriday + ValidadorLimiteCompra (OCP - sin tocar código base)
  controller/  → TiendaController + ResultadoOperacion (Controller - orquesta todo, TESTEABLE)
  view/        → TiendaView (View - solo imprime, sin lógica)
  Main.java    → Wiring MVC (sin lógica, solo 5 casos demo)
src/test/java/com/tienda/
  strategy/CalculadoraPrecioTest.java
  chain/ChainTest.java
  controller/TiendaControllerTest.java
  challenge/DesafioTest.java
  model/ModelTest.java
  TestRunner.java → runner sin JUnit (19 tests, sin Main)
docs/
  diagrama.puml, FUTURAS_IMPLEMENTACIONES.md, CORRECCION_ERRORES.md
```

> **REGLA cumplida:** nada se maneja full por Main. `TiendaController` expone `calcularPrecio()`, `validarPedido()`, `procesarPedido()` puros (sin `System.out`) y es testeado sin Main. `Main` solo hace wiring.

## Compilar y ejecutar

```bat
compile.bat      # compila Main + TestRunner sin dependencias
run.bat          # compila + java com.tienda.Main (MVC, 5 casos)
test.bat         # compila + java com.tienda.TestRunner (19 PASS) + mvn test si hay Maven
```

Manual sin scripts:
```bat
javac -d out -encoding UTF-8 -sourcepath src\main\java src\main\java\com\tienda\model\*.java src\main\java\com\tienda\strategy\*.java src\main\java\com\tienda\chain\*.java src\main\java\com\tienda\challenge\*.java src\main\java\com\tienda\controller\*.java src\main\java\com\tienda\view\*.java src\main\java\com\tienda\Main.java
java -cp out com.tienda.Main

javac -d out -encoding UTF-8 -cp out -sourcepath src\test\java src\test\java\com\tienda\TestRunner.java
java -cp out com.tienda.TestRunner
```

Con Maven:
```bat
mvn test              # JUnit5 (5 clases, ~20 tests)
mvn compile exec:java # Main MVC
```

## Tests — sin Main

**JUnit5** (`pom.xml: JUnit 5.9.3`): `mvn test` corre `CalculadoraPrecioTest`, `ChainTest`, `TiendaControllerTest`, `DesafioTest`, `ModelTest`.

**Sin Maven** (CI / corrección rápida): `TestRunner.java` replica los mismos casos sin importar `org.junit` — `java -cp out com.tienda.TestRunner` (26 tests, usa solo `TiendaController`).

Todos los tests llaman a `TiendaController`/`CalculadoraPrecio`/`Handler` directamente, nunca a `Main`.

## Parte 1 — Strategy

**Interface:** `EstrategiaPrecio` (`strategy/EstrategiaPrecio.java:12`) — `aplicar(precio, compra)` + `getDescripcion()` (ISP, DIP)

| Clase | Regla |
|-------|-------|
| `DescuentoClienteComun` | 0% |
| `DescuentoClientePremium` | -10% si PREMIUM |
| `DescuentoClienteVIP` | -15% si VIP |
| `DescuentoMayor5` | >5 y ≤10 → -5% |
| `DescuentoMayor10` | >10 → -10% |
| `DescuentoPorCantidad` | compone `DescuentoMayor5` + `DescuentoMayor10` (compatibilidad) |
| `CostoEnvio` | Retiro $0 / Normal $5000 / Express $10000 |
| `PromocionEspecial` | parametrizable, default -10% |

**Context:** `CalculadoraPrecio` (`strategy/CalculadoraPrecio.java:13`) — `calcularConDetalle()` puro para Controller/View.

**Ejemplo enunciado:** $100.000 → VIP -15% → Cantidad(12) -10% → Promo -5% → Express +$10.000 = $82.675 (Caso 1).

## Parte 2 — Chain of Responsibility

**Abstract Handler:** `Handler` (`chain/Handler.java:11`) — `setSiguiente()` fluent + `handle()` final que corta en ERROR. Log inyectable con `setOutput(PrintStream)` (MVC limpio).

**Validadores (≥3):** `ValidadorCliente` → `ValidadorStock` → `ValidadorPago` → `ValidadorFraude` (opcional). Cada handler SRP.

Si falla, **detiene la cadena** y retorna `ResultadoValidacion.error(motivo)`.

## MVC

- **Model:** `Compra`, `Pedido`, `TipoCliente`, `TipoEnvio`, `ResultadoValidacion` — solo datos.
- **View:** `TiendaView` (`view/TiendaView.java:12`) — `mostrarEncabezado()`, `mostrarCompra()`, `mostrarCalculoDetalle()`, `mostrarChainHeader()`, `mostrarResultadoFinal()`. Sin lógica.
- **Controller:** `TiendaController` (`controller/TiendaController.java:13`) — `calcularPrecio()`, `validarPedido()`, `procesarPedido()`, `crearCadenaBase()`, `crearCadenaCon()`. Testeable puro. `Main.java` solo hace wiring.

## Casos (via Controller, no Main)

1. **Aprobado** — VIP 12u Express → $82675
2. **Rechazado stock** — stock=false → corta en Validar Stock, $47750
3. **Rechazado pago** — pago=false → corta en Pago, $67500
4. **Desafío aprobado** — BlackFriday -25% + límite 200k → $64506.25
5. **Desafío rechazado** — límite 150k → ERROR

## Desafío OCP

Sin modificar `strategy/*` ni `chain/*`:
- `challenge/DescuentoBlackFriday.java` implements `EstrategiaPrecio`
- `challenge/ValidadorLimiteCompra.java` extends `Handler`
Se inyectan vía `controller.crearCadenaCon(new ValidadorLimiteCompra(...))` y `estrategias.add(new DescuentoBlackFriday())`.

## SOLID

- **SRP:** cada clase una responsabilidad
- **OCP:** abierto a extensión (challenge/future) cerrado a modificación
- **LSP:** cualquier `EstrategiaPrecio`/`Handler` sustituye a otra
- **ISP:** interfaces mínimas
- **DIP:** Controller/View dependen de abstracciones

## Diagrama

`docs/diagrama.puml` (PlantUML, incluye MVC) — https://www.plantuml.com/plantuml/

## Requisitos

- Java 8+ (probado Temurin 1.8)
- Maven 3.6+ opcional (para JUnit); sin Maven usar `test.bat` / `TestRunner`
