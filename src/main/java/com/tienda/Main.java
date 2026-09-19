package com.tienda;

import com.tienda.chain.Handler;
import com.tienda.chain.ResultadoValidacion;
import com.tienda.challenge.DescuentoBlackFriday;
import com.tienda.challenge.ValidadorLimiteCompra;
import com.tienda.controller.TiendaController;
import com.tienda.model.*;
import com.tienda.strategy.*;
import com.tienda.view.TiendaView;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TiendaController c = new TiendaController();
        TiendaView v = new TiendaView();
        System.out.println("================================================");
        System.out.println("  TIENDA ONLINE - Strategy + Chain + SOLID (MVC)");
        System.out.println("================================================\n");

        ejecutar(v, c, "CASO 1: PEDIDO APROBADO",
                new Compra(100000, TipoCliente.VIP, 12, TipoEnvio.ENVIO_EXPRESS),
                c.crearCadenaBase(), true, true, true,
                Arrays.asList(new DescuentoClienteVIP(), new DescuentoMayor5(), new DescuentoMayor10(), new PromocionEspecial(0.05, "Promocion Especial"), new CostoEnvio()));

        ejecutar(v, c, "CASO 2: RECHAZADO POR FALTA DE STOCK",
                new Compra(50000, TipoCliente.PREMIUM, 8, TipoEnvio.ENVIO_NORMAL),
                c.crearCadenaBase(), true, false, true,
                Arrays.asList(new DescuentoClientePremium(), new DescuentoMayor5(), new DescuentoMayor10(), new CostoEnvio()));

        ejecutar(v, c, "CASO 3: RECHAZADO POR PAGO",
                new Compra(75000, TipoCliente.COMUN, 3, TipoEnvio.RETIRO_SUCURSAL),
                c.crearCadenaBase(), true, true, false,
                Arrays.asList(new DescuentoClienteComun(), new DescuentoMayor5(), new DescuentoMayor10(), new PromocionEspecial(), new CostoEnvio()));

        System.out.println("================================================");
        System.out.println("  DESAFIO - Extension sin modificar codigo existente");
        System.out.println("================================================\n");

        ejecutar(v, c, "CASO 4: BLACK FRIDAY + LIMITE (APROBADO)",
                new Compra(100000, TipoCliente.VIP, 12, TipoEnvio.ENVIO_EXPRESS),
                c.crearCadenaCon(new ValidadorLimiteCompra(200000)), true, true, true,
                Arrays.asList(new DescuentoClienteVIP(), new DescuentoMayor5(), new DescuentoMayor10(), new PromocionEspecial(0.05, "Promocion Especial"), new DescuentoBlackFriday(), new CostoEnvio()));

        ejecutar(v, c, "CASO 5: BLACK FRIDAY + LIMITE (RECHAZADO)",
                new Compra(300000, TipoCliente.COMUN, 1, TipoEnvio.ENVIO_EXPRESS),
                c.crearCadenaCon(new ValidadorLimiteCompra(150000)), true, true, true,
                Arrays.asList(new DescuentoClienteVIP(), new DescuentoMayor5(), new DescuentoMayor10(), new PromocionEspecial(0.05, "Promocion Especial"), new DescuentoBlackFriday(), new CostoEnvio()));

        v.mostrarExplicacion();
    }

    // Orden MVC: header -> calculo -> chain header -> validacion
    private static void ejecutar(TiendaView v, TiendaController c, String titulo, Compra compra, Handler cadena,
                                 boolean cli, boolean stock, boolean pago, List<EstrategiaPrecio> est) {
        v.mostrarEncabezado(titulo);
        v.mostrarCompra(compra);
        double precio = c.calcularPrecio(compra, est);
        v.mostrarCalculoDetalle(c.calcularConDetalle(compra, est), precio);
        v.mostrarChainHeader();
        ResultadoValidacion r = c.validarPedido(new com.tienda.model.Pedido(compra, precio, cli, stock, pago), cadena);
        v.mostrarResultadoFinal(r);
    }
}
