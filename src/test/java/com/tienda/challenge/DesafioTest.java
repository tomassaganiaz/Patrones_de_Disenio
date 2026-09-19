package com.tienda.challenge;

import com.tienda.controller.TiendaController;
import com.tienda.model.Compra;
import com.tienda.model.TipoCliente;
import com.tienda.model.TipoEnvio;
import com.tienda.strategy.CalculadoraPrecio;
import com.tienda.strategy.DescuentoClienteVIP;
import com.tienda.strategy.DescuentoPorCantidad;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DesafioTest {

    @Test
    void blackFriday25() {
        Compra c = new Compra(10000, TipoCliente.COMUN, 1, TipoEnvio.RETIRO_SUCURSAL);
        CalculadoraPrecio calc = new CalculadoraPrecio();
        calc.agregarEstrategia(new DescuentoBlackFriday());
        assertEquals(7500, calc.calcularConDetalle(c).precioFinal, 0.01);
    }

    @Test
    void blackFridayParametrizable() {
        Compra c = new Compra(10000, TipoCliente.COMUN, 1, TipoEnvio.RETIRO_SUCURSAL);
        CalculadoraPrecio calc = new CalculadoraPrecio();
        calc.agregarEstrategia(new DescuentoBlackFriday(0.50));
        assertEquals(5000, calc.calcularConDetalle(c).precioFinal, 0.01);
    }

    @Test
    void limiteCompraSinModificarValidadoresExistentes() {
        TiendaController ctrl = new TiendaController();
        Compra c = new Compra(50000, TipoCliente.COMUN, 1, TipoEnvio.RETIRO_SUCURSAL);
        // OCP: se agrega como eslabón nuevo
        assertFalse(ctrl.procesarPedido(c, ctrl.crearCadenaCon(new ValidadorLimiteCompra(10000)),
                true,true,true).isAprobado());
        assertTrue(ctrl.procesarPedido(c, ctrl.crearCadenaCon(new ValidadorLimiteCompra(100000)),
                true,true,true).isAprobado());
    }

    @Test
    void desafioNoRompeCasosBase() {
        TiendaController ctrl = new TiendaController();
        Compra base = new Compra(100000, TipoCliente.VIP, 12, TipoEnvio.ENVIO_EXPRESS);
        double precioBase = ctrl.calcularPrecio(base, new DescuentoClienteVIP(), new DescuentoPorCantidad());
        double precioChallenge = ctrl.calcularPrecio(base, new DescuentoClienteVIP(), new DescuentoPorCantidad(), new DescuentoBlackFriday());
        assertTrue(precioChallenge < precioBase); // BlackFriday descuenta más
        assertTrue(ctrl.procesarPedido(base, ctrl.crearCadenaBase(), true,true,true,
                new DescuentoClienteVIP(), new DescuentoPorCantidad()).isAprobado());
    }
}
