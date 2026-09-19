package com.tienda;

import com.tienda.challenge.DescuentoBlackFriday;
import com.tienda.challenge.ValidadorLimiteCompra;
import com.tienda.chain.ResultadoValidacion;
import com.tienda.controller.ResultadoOperacion;
import com.tienda.controller.TiendaController;
import com.tienda.model.Compra;
import com.tienda.model.Pedido;
import com.tienda.model.TipoCliente;
import com.tienda.model.TipoEnvio;
import com.tienda.strategy.*;

// Runner sin dependencias - testeable sin Main ni Maven
public class TestRunner {
    private static int pass=0, fail=0;

    public static void main(String[] args) {
        System.out.println("=== TESTS SIN MAIN (MVC testeable) ===");
        testStrategy();
        testChain();
        testController();
        testDesafio();
        testModel();
        testReglasIndependientes();
        testOutputInyectable();
        System.out.println("\n=== RESULTADO: " + pass + " PASS, " + fail + " FAIL ===");
        if (fail>0) System.exit(1);
    }

    static void testStrategy() {
        check("strategy: base sin estrategias", eq(
                new TiendaController().calcularPrecio(new Compra(100, TipoCliente.COMUN,1,TipoEnvio.RETIRO_SUCURSAL)), 100));
        check("strategy: VIP -15%", eq(
                new TiendaController().calcularPrecio(new Compra(100000,TipoCliente.VIP,1,TipoEnvio.RETIRO_SUCURSAL), new DescuentoClienteVIP()), 85000));
        check("strategy: cantidad >5 -5%", eq(
                new TiendaController().calcularPrecio(new Compra(10000,TipoCliente.COMUN,6,TipoEnvio.RETIRO_SUCURSAL), new DescuentoPorCantidad()), 9500));
        check("strategy: cantidad >10 -10%", eq(
                new TiendaController().calcularPrecio(new Compra(10000,TipoCliente.COMUN,12,TipoEnvio.RETIRO_SUCURSAL), new DescuentoPorCantidad()), 9000));
        check("strategy: envio normal +5000", eq(
                new TiendaController().calcularPrecio(new Compra(10000,TipoCliente.COMUN,1,TipoEnvio.ENVIO_NORMAL), new CostoEnvio()), 15000));
        check("strategy: enunciado 82675", eq(
                new TiendaController().calcularPrecio(
                    new Compra(100000,TipoCliente.VIP,12,TipoEnvio.ENVIO_EXPRESS),
                    new DescuentoClienteVIP(), new DescuentoPorCantidad(),
                    new PromocionEspecial(0.05,"Promo"), new CostoEnvio()), 82675));
    }

    static void testChain() {
        TiendaController ctrl = new TiendaController();
        check("chain: aprobado", ctrl.procesarPedido(
                new Compra(10000,TipoCliente.COMUN,1,TipoEnvio.RETIRO_SUCURSAL),
                ctrl.crearCadenaBase(), true,true,true).isAprobado());
        check("chain: stock corta", !ctrl.procesarPedido(
                new Compra(10000,TipoCliente.COMUN,1,TipoEnvio.RETIRO_SUCURSAL),
                ctrl.crearCadenaBase(), true,false,true).isAprobado());
        check("chain: pago corta", !ctrl.procesarPedido(
                new Compra(10000,TipoCliente.COMUN,1,TipoEnvio.RETIRO_SUCURSAL),
                ctrl.crearCadenaBase(), true,true,false).isAprobado());
        check("chain: cliente corta", !ctrl.procesarPedido(
                new Compra(10000,TipoCliente.COMUN,1,TipoEnvio.RETIRO_SUCURSAL),
                ctrl.crearCadenaBase(), false,true,true).isAprobado());
    }

    static void testController() {
        TiendaController ctrl = new TiendaController();
        ResultadoOperacion op1 = ctrl.procesarPedido(
                new Compra(100000,TipoCliente.VIP,12,TipoEnvio.ENVIO_EXPRESS),
                ctrl.crearCadenaBase(), true,true,true,
                new DescuentoClienteVIP(), new DescuentoPorCantidad(),
                new PromocionEspecial(0.05,"Promo"), new CostoEnvio());
        check("controller: caso1 82675 aprobado", eq(op1.getPrecioFinal(),82675) && op1.isAprobado());

        ResultadoOperacion op2 = ctrl.procesarPedido(
                new Compra(50000,TipoCliente.PREMIUM,8,TipoEnvio.ENVIO_NORMAL),
                ctrl.crearCadenaBase(), true,false,true,
                new DescuentoClientePremium(), new DescuentoPorCantidad(), new CostoEnvio());
        check("controller: caso2 47750 rechazado stock", eq(op2.getPrecioFinal(),47750) && !op2.isAprobado());

        ResultadoOperacion op3 = ctrl.procesarPedido(
                new Compra(75000,TipoCliente.COMUN,3,TipoEnvio.RETIRO_SUCURSAL),
                ctrl.crearCadenaBase(), true,true,false,
                new DescuentoClienteComun(), new DescuentoPorCantidad(), new PromocionEspecial(), new CostoEnvio());
        check("controller: caso3 67500 rechazado pago", eq(op3.getPrecioFinal(),67500) && !op3.isAprobado());
    }

    static void testDesafio() {
        TiendaController ctrl = new TiendaController();
        check("desafio: blackFriday -25%", eq(
                new TiendaController().calcularPrecio(new Compra(10000,TipoCliente.COMUN,1,TipoEnvio.RETIRO_SUCURSAL), new DescuentoBlackFriday()), 7500));
        check("desafio: limite aprobado 200k", ctrl.procesarPedido(
                new Compra(100000,TipoCliente.VIP,12,TipoEnvio.ENVIO_EXPRESS),
                ctrl.crearCadenaCon(new ValidadorLimiteCompra(200000)), true,true,true,
                new DescuentoClienteVIP(), new DescuentoPorCantidad(),
                new PromocionEspecial(0.05,"Promo"), new DescuentoBlackFriday(), new CostoEnvio()).isAprobado());
        check("desafio: limite rechazado 150k", !ctrl.procesarPedido(
                new Compra(300000,TipoCliente.COMUN,1,TipoEnvio.ENVIO_EXPRESS),
                ctrl.crearCadenaCon(new ValidadorLimiteCompra(150000)), true,true,true,
                new DescuentoClienteVIP(), new DescuentoPorCantidad(),
                new PromocionEspecial(0.05,"Promo"), new DescuentoBlackFriday(), new CostoEnvio()).isAprobado());
        // OCP: base sigue ok
        check("desafio: no rompe base", ctrl.procesarPedido(
                new Compra(100000,TipoCliente.VIP,12,TipoEnvio.ENVIO_EXPRESS),
                ctrl.crearCadenaBase(), true,true,true,
                new DescuentoClienteVIP(), new DescuentoPorCantidad()).isAprobado());
    }

    static void testModel() {
        Compra c = new Compra(123, TipoCliente.PREMIUM,7,TipoEnvio.ENVIO_NORMAL);
        check("model: compra getters", c.getPrecioBase()==123 && c.getTipoCliente()==TipoCliente.PREMIUM);
        Pedido p = new Pedido(c, 100, true,false,true);
        check("model: pedido flags", p.isClienteValido() && !p.isStockDisponible());
    }

    static boolean eq(double a,double b){ return Math.abs(a-b)<0.01; }
    static void check(String name, boolean ok){
        if(ok){ pass++; System.out.println("  PASS: "+name); }
        else { fail++; System.out.println("  FAIL: "+name); }
    }

    // Reglas independientes: Mayor5 solo en rango 6-10, Mayor10 solo >10
    static void testReglasIndependientes() {
        TiendaController c = new TiendaController();
        check("mayor5: 6 productos -5%", eq(c.calcularPrecio(
                new Compra(10000,TipoCliente.COMUN,6,TipoEnvio.RETIRO_SUCURSAL), new DescuentoMayor5()), 9500));
        check("mayor5: 12 productos no aplica", eq(c.calcularPrecio(
                new Compra(10000,TipoCliente.COMUN,12,TipoEnvio.RETIRO_SUCURSAL), new DescuentoMayor5()), 10000));
        check("mayor10: 12 productos -10%", eq(c.calcularPrecio(
                new Compra(10000,TipoCliente.COMUN,12,TipoEnvio.RETIRO_SUCURSAL), new DescuentoMayor10()), 9000));
        check("mayor10: 6 productos no aplica", eq(c.calcularPrecio(
                new Compra(10000,TipoCliente.COMUN,6,TipoEnvio.RETIRO_SUCURSAL), new DescuentoMayor10()), 10000));
        // combinadas excluyentes dan mismo resultado que DescuentoPorCantidad
        Compra ocho = new Compra(10000,TipoCliente.COMUN,8,TipoEnvio.RETIRO_SUCURSAL);
        check("mayor5+mayor10 8 prod = compuesto", eq(c.calcularPrecio(ocho, new DescuentoMayor5(), new DescuentoMayor10()),
                c.calcularPrecio(ocho, new DescuentoPorCantidad())));
    }

    // Log del Handler inyectable (sin System.out fijo)
    static void testOutputInyectable() {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream ps = new java.io.PrintStream(bos);
        TiendaController ctrl = new TiendaController();
        com.tienda.chain.Handler cadena = ctrl.crearCadenaBase().setOutput(ps);
        ResultadoValidacion r = cadena.handle(new Pedido(
                new Compra(10000,TipoCliente.COMUN,1,TipoEnvio.RETIRO_SUCURSAL), 10000, true,true,true));
        String log = bos.toString();
        check("output inyectable captura pasos", r.isAprobado()
                && log.contains("Validar Cliente") && log.contains("Validar Stock") && log.contains("Validar Pago"));
        // reinyectar System.out no rompe default
        cadena.setOutput(System.out);
        check("output reinyectable a System.out", ctrl.procesarPedido(
                new Compra(10000,TipoCliente.COMUN,1,TipoEnvio.RETIRO_SUCURSAL),
                cadena, true,true,true).isAprobado());
    }
}
