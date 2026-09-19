package com.tienda.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void compraGetters() {
        Compra c = new Compra(123, TipoCliente.PREMIUM, 7, TipoEnvio.ENVIO_NORMAL);
        assertEquals(123, c.getPrecioBase(), 0.01);
        assertEquals(TipoCliente.PREMIUM, c.getTipoCliente());
        assertEquals(7, c.getCantidadProductos());
        assertEquals(TipoEnvio.ENVIO_NORMAL, c.getTipoEnvio());
    }

    @Test
    void pedidoFlags() {
        Compra c = new Compra(100, TipoCliente.COMUN, 1, TipoEnvio.RETIRO_SUCURSAL);
        Pedido p = new Pedido(c, 100, true, false, true);
        assertTrue(p.isClienteValido());
        assertFalse(p.isStockDisponible());
        assertTrue(p.isPagoValido());
        p.setStockDisponible(true);
        assertTrue(p.isStockDisponible());
    }
}
