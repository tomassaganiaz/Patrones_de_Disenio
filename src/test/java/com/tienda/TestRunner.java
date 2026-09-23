package com.tienda;

import com.tienda.controller.TiendaController;
import com.tienda.model.dao.PedidoDAOMemoria;
import com.tienda.model.domain.Compra;
import com.tienda.model.domain.Pedido;
import com.tienda.model.services.CalculoPrecioService;
import com.tienda.model.services.ClientePremium;
import com.tienda.model.services.ClienteVip;
import com.tienda.model.services.DescuentoBlackFriday;
import com.tienda.model.services.DescuentoPorCantidad;
import com.tienda.model.services.EnvioExpress;
import com.tienda.model.services.EnvioNormal;
import com.tienda.model.services.PromocionPorcentual;
import com.tienda.model.services.ValidacionPedidoService;
import com.tienda.view.ConsolaView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Runner sin JUnit: corre la demo (igual que main.py) y las verificaciones.
public class TestRunner {
    private static int pass = 0;
    private static int fail = 0;

    public static void main(String[] args) {
        System.out.println("========== TIENDA ONLINE - DEMO Y TESTS ==========\n");
        demoCasos();
        testCalculoPrecioService();
        testValidacionPedidoService();
        testPedidoDao();
        System.out.println("\n=== RESULTADO: " + pass + " PASS, " + fail + " FAIL ===");
        if (fail > 0) {
            System.exit(1);
        }
    }

    // Demo identica a main.py del TP: PARTE 1, PARTE 2, DESAFIO y DAO.
    private static void demoCasos() {
        TiendaController controller = new TiendaController(
                new ConsolaView(), new PedidoDAOMemoria());

        controller.mostrarTitulo(
                "PARTE 1 - STRATEGY: calculo del precio final de una compra");
        Compra compra = new Compra(100_000, 12);
        compra.agregarEstrategia(new ClienteVip());
        compra.agregarEstrategia(new DescuentoPorCantidad());
        compra.agregarEstrategia(new PromocionPorcentual(5, "Promocion especial"));
        compra.agregarEstrategia(new EnvioExpress());
        double precio = controller.calcularPrecioCompra(compra);

        controller.mostrarTitulo(
                "PARTE 2 - CHAIN OF RESPONSIBILITY: validacion de pedidos");
        Pedido pedidoAprobado = new Pedido("PED-001", true, true, true, precio);
        Pedido pedidoSinStock = new Pedido("PED-002", true, false, true, 45_000);
        Pedido pedidoPagoRechazado = new Pedido("PED-003", true, true, false, 60_000);
        ValidacionPedidoService validacion = new ValidacionPedidoService();
        controller.procesarPedido(pedidoAprobado, validacion);
        controller.procesarPedido(pedidoSinStock, validacion);
        controller.procesarPedido(pedidoPagoRechazado, validacion);

        controller.mostrarTitulo(
                "DESAFIO: nueva estrategia y nuevo validador, sin tocar los existentes");
        Compra compraDesafio = new Compra(100_000, 3);
        compraDesafio.agregarEstrategia(new ClientePremium());
        compraDesafio.agregarEstrategia(new DescuentoBlackFriday());
        compraDesafio.agregarEstrategia(new EnvioNormal());
        double precioDesafio = controller.calcularPrecioCompra(compraDesafio);

        ValidacionPedidoService validacionConLimite =
                new ValidacionPedidoService(true);
        Pedido pedidoOk = new Pedido("PED-004", true, true, true, precioDesafio);
        controller.procesarPedido(pedidoOk, validacionConLimite);

        Pedido pedidoExcedeLimite = new Pedido("PED-005", true, true, true, 800_000);
        controller.procesarPedido(pedidoExcedeLimite, validacionConLimite);

        controller.mostrarTitulo("PEDIDOS PERSISTIDOS (capa DAO)");
        controller.mostrarPedidosPersistidos();
    }

    // Verificaciones de CalculoPrecioService (patron Strategy).
    private static void testCalculoPrecioService() {
        CalculoPrecioService service = new CalculoPrecioService();

        Compra sinEstrategias = new Compra(100_000, 1);
        check("strategy: sin estrategias no cambia el precio",
                eq(service.calcular(sinEstrategias), 100_000));

        Compra enunciado = new Compra(100_000, 12);
        enunciado.agregarEstrategia(new ClienteVip());
        enunciado.agregarEstrategia(new DescuentoPorCantidad());
        enunciado.agregarEstrategia(new PromocionPorcentual(5));
        enunciado.agregarEstrategia(new EnvioExpress());
        double esperado = 100_000 * 0.85 * 0.90 * 0.95 + 10_000;
        check("strategy: ejemplo del enunciado (82675)",
                eq(service.calcular(enunciado), esperado));

        Compra conHistorial = new Compra(100_000, 1);
        conHistorial.agregarEstrategia(new ClienteVip());
        conHistorial.agregarEstrategia(new EnvioExpress());
        service.calcular(conHistorial);
        boolean historialOk = conHistorial.getHistorial().size() == 2
                && conHistorial.getHistorial().get(0).descripcion
                        .equals("Cliente VIP (-15%)");
        check("strategy: registra un paso de historial por estrategia",
                historialOk);

        Compra blackFriday = new Compra(100_000, 1);
        blackFriday.agregarEstrategia(new DescuentoBlackFriday());
        check("strategy: desafio black friday -25%",
                eq(service.calcular(blackFriday), 75_000));
    }

    // Verificaciones de ValidacionPedidoService (Chain of Responsibility).
    private static void testValidacionPedidoService() {
        ValidacionPedidoService service = new ValidacionPedidoService();

        Pedido valido = pedido(10_000, true, true, true);
        check("chain: pedido valido queda aprobado", service.procesar(valido));
        check("chain: pasos del valido son Cliente/Stock/Pago",
                nombresPasos(valido).equals(Arrays.asList(
                        "Validar Cliente", "Validar Stock", "Validar Pago")));

        Pedido sinStock = pedido(10_000, true, false, true);
        check("chain: sin stock se detiene antes de pago",
                !service.procesar(sinStock)
                        && "Validar Stock".equals(sinStock.getHandlerRechazo()));
        check("chain: pasos del sin stock son Cliente/Stock",
                nombresPasos(sinStock).equals(Arrays.asList(
                        "Validar Cliente", "Validar Stock")));

        Pedido pagoRechazado = pedido(10_000, true, true, false);
        check("chain: pago rechazado detiene en pago",
                !service.procesar(pagoRechazado)
                        && "Validar Pago".equals(pagoRechazado.getHandlerRechazo()));

        Pedido caroEstandar = pedido(800_000, true, true, true);
        check("chain: desafio limite no afecta la cadena estandar",
                service.procesar(caroEstandar));

        ValidacionPedidoService serviceConLimite =
                new ValidacionPedidoService(true);
        Pedido caroConLimite = pedido(800_000, true, true, true);
        check("chain: desafio limite rechaza montos excesivos",
                !serviceConLimite.procesar(caroConLimite)
                        && "Validar Limite de Compra"
                                .equals(caroConLimite.getHandlerRechazo()));
    }

    // Verificaciones de PedidoDAOMemoria (capa DAO).
    private static void testPedidoDao() {
        PedidoDAOMemoria dao = new PedidoDAOMemoria();

        Pedido p1 = new Pedido("PED-1", true, true, true, 1000);
        dao.guardar(p1);
        check("dao: guarda y busca por id", dao.buscarPorId("PED-1") == p1);

        check("dao: buscar id inexistente devuelve null",
                dao.buscarPorId("NO-EXISTE") == null);

        Pedido p2 = new Pedido("PED-2", true, true, true, 2000);
        dao.guardar(p2);
        check("dao: listar devuelve todos los guardados",
                dao.listar().size() == 2);

        PedidoDAOMemoria daoConActualizacion = new PedidoDAOMemoria();
        Pedido original = new Pedido("PED-1", true, true, true, 1000);
        Pedido actualizado = new Pedido("PED-1", true, true, true, 5000);
        daoConActualizacion.guardar(original);
        daoConActualizacion.guardar(actualizado);
        check("dao: guardar el mismo id sobrescribe",
                daoConActualizacion.listar().size() == 1
                        && daoConActualizacion.buscarPorId("PED-1")
                                .getPrecioFinal() == 5000);
    }

    // Crea un pedido con los flags y el precio indicados.
    private static Pedido pedido(double precio, boolean clienteOk,
            boolean stockOk, boolean pagoOk) {
        return new Pedido("PED-TEST", clienteOk, stockOk, pagoOk, precio);
    }

    // Devuelve solo los nombres de los pasos registrados en el pedido.
    private static List<String> nombresPasos(Pedido pedido) {
        List<String> nombres = new ArrayList<String>();
        for (Pedido.PasoValidacion paso : pedido.getPasos()) {
            nombres.add(paso.nombre);
        }
        return nombres;
    }

    // Compara dos precios con una pequena tolerancia.
    private static boolean eq(double a, double b) {
        return Math.abs(a - b) < 0.01;
    }

    // Cuenta un resultado de verificacion.
    private static void check(String nombre, boolean ok) {
        if (ok) {
            pass++;
            System.out.println("  PASS: " + nombre);
        } else {
            fail++;
            System.out.println("  FAIL: " + nombre);
        }
    }
}