package com.tienda.controller;

import com.tienda.challenge.DescuentoBlackFriday;
import com.tienda.challenge.ValidadorLimiteCompra;
import com.tienda.model.Compra;
import com.tienda.model.TipoCliente;
import com.tienda.model.TipoEnvio;
import com.tienda.strategy.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TiendaControllerTest {

    private final TiendaController ctrl = new TiendaController();
    private static final double D = 0.01;

    @Test
    void caso1AprobadoEnunciado() {
        Compra c = new Compra(100000, TipoCliente.VIP, 12, TipoEnvio.ENVIO_EXPRESS);
        ResultadoOperacion op = ctrl.procesarPedido(c, ctrl.crearCadenaBase(), true, true, true,
                new DescuentoClienteVIP(), new DescuentoPorCantidad(),
                new PromocionEspecial(0.05, "Promocion Especial"), new CostoEnvio());
        assertEquals(82675, op.getPrecioFinal(), D);
        assertTrue(op.isAprobado());
    }

    @Test
    void caso2StockRechazado() {
        Compra c = new Compra(50000, TipoCliente.PREMIUM, 8, TipoEnvio.ENVIO_NORMAL);
        ResultadoOperacion op = ctrl.procesarPedido(c, ctrl.crearCadenaBase(), true, false, true,
                new DescuentoClientePremium(), new DescuentoPorCantidad(), new CostoEnvio());
        assertEquals(47750, op.getPrecioFinal(), D);
        assertFalse(op.isAprobado());
        assertTrue(op.getValidacion().getMensaje().toLowerCase().contains("stock"));
    }

    @Test
    void caso3PagoRechazado() {
        Compra c = new Compra(75000, TipoCliente.COMUN, 3, TipoEnvio.RETIRO_SUCURSAL);
        ResultadoOperacion op = ctrl.procesarPedido(c, ctrl.crearCadenaBase(), true, true, false,
                new DescuentoClienteComun(), new DescuentoPorCantidad(), new PromocionEspecial(), new CostoEnvio());
        assertEquals(67500, op.getPrecioFinal(), D);
        assertFalse(op.isAprobado());
    }

    @Test
    void desafioBlackFridayConLimiteAprobado() {
        Compra c = new Compra(100000, TipoCliente.VIP, 12, TipoEnvio.ENVIO_EXPRESS);
        ResultadoOperacion op = ctrl.procesarPedido(c,
                ctrl.crearCadenaCon(new ValidadorLimiteCompra(200000)),
                true, true, true,
                new DescuentoClienteVIP(), new DescuentoPorCantidad(),
                new PromocionEspecial(0.05, "Promo"), new DescuentoBlackFriday(), new CostoEnvio());
        assertEquals(64506.25, op.getPrecioFinal(), D);
        assertTrue(op.isAprobado());
    }

    @Test
    void desafioLimiteRechazado() {
        Compra c = new Compra(300000, TipoCliente.COMUN, 1, TipoEnvio.ENVIO_EXPRESS);
        ResultadoOperacion op = ctrl.procesarPedido(c,
                ctrl.crearCadenaCon(new ValidadorLimiteCompra(150000)),
                true, true, true,
                new DescuentoClienteVIP(), new DescuentoPorCantidad(),
                new PromocionEspecial(0.05, "Promo"), new DescuentoBlackFriday(), new CostoEnvio());
        assertTrue(op.getPrecioFinal() > 150000);
        assertFalse(op.isAprobado());
        assertTrue(op.getValidacion().getMensaje().toLowerCase().contains("limite"));
    }

    @Test
    void calcularPrecioNullLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> ctrl.calcularPrecio(null));
    }

    @Test
    void estrategiasSonComponiblesYTesteablesSinMain() {
        Compra c = new Compra(20000, TipoCliente.COMUN, 1, TipoEnvio.ENVIO_NORMAL);
        double conEnvio = ctrl.calcularPrecio(c, new CostoEnvio());
        double sinEnvio = ctrl.calcularPrecio(c);
        assertEquals(5000, conEnvio - sinEnvio, D);
    }
}
