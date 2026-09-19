package com.tienda.strategy;

import com.tienda.model.Compra;
import com.tienda.model.TipoCliente;
import com.tienda.model.TipoEnvio;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculadoraPrecioTest {

    private static final double DELTA = 0.01;

    @Test
    void precioBaseSinEstrategias() {
        Compra c = new Compra(100, TipoCliente.COMUN, 1, TipoEnvio.RETIRO_SUCURSAL);
        CalculadoraPrecio calc = new CalculadoraPrecio();
        assertEquals(100, calc.calcularConDetalle(c).precioFinal, DELTA);
    }

    @Test
    void clienteVip15Porciento() {
        Compra c = new Compra(100000, TipoCliente.VIP, 1, TipoEnvio.RETIRO_SUCURSAL);
        CalculadoraPrecio calc = new CalculadoraPrecio();
        calc.agregarEstrategia(new DescuentoClienteVIP());
        assertEquals(85000, calc.calcularConDetalle(c).precioFinal, DELTA);
    }

    @Test
    void clientePremium10() {
        Compra c = new Compra(100000, TipoCliente.PREMIUM, 1, TipoEnvio.RETIRO_SUCURSAL);
        CalculadoraPrecio calc = new CalculadoraPrecio();
        calc.agregarEstrategia(new DescuentoClientePremium());
        assertEquals(90000, calc.calcularConDetalle(c).precioFinal, DELTA);
    }

    @Test
    void cantidadMasDe5Descuento5() {
        Compra c = new Compra(10000, TipoCliente.COMUN, 6, TipoEnvio.RETIRO_SUCURSAL);
        CalculadoraPrecio calc = new CalculadoraPrecio();
        calc.agregarEstrategia(new DescuentoPorCantidad());
        assertEquals(9500, calc.calcularConDetalle(c).precioFinal, DELTA);
    }

    @Test
    void cantidadMasDe10Descuento10() {
        Compra c = new Compra(10000, TipoCliente.COMUN, 12, TipoEnvio.RETIRO_SUCURSAL);
        CalculadoraPrecio calc = new CalculadoraPrecio();
        calc.agregarEstrategia(new DescuentoPorCantidad());
        assertEquals(9000, calc.calcularConDetalle(c).precioFinal, DELTA);
    }

    @Test
    void mayor5SoloEnRango6A10() {
        Compra c6 = new Compra(10000, TipoCliente.COMUN, 6, TipoEnvio.RETIRO_SUCURSAL);
        Compra c12 = new Compra(10000, TipoCliente.COMUN, 12, TipoEnvio.RETIRO_SUCURSAL);
        assertEquals(9500, new DescuentoMayor5().aplicar(c6.getPrecioBase(), c6), DELTA);
        assertEquals(10000, new DescuentoMayor5().aplicar(c12.getPrecioBase(), c12), DELTA);
    }

    @Test
    void mayor10SoloMayorA10() {
        Compra c12 = new Compra(10000, TipoCliente.COMUN, 12, TipoEnvio.RETIRO_SUCURSAL);
        Compra c6 = new Compra(10000, TipoCliente.COMUN, 6, TipoEnvio.RETIRO_SUCURSAL);
        assertEquals(9000, new DescuentoMayor10().aplicar(c12.getPrecioBase(), c12), DELTA);
        assertEquals(10000, new DescuentoMayor10().aplicar(c6.getPrecioBase(), c6), DELTA);
    }

    @Test
    void mayor5MasMayor10EquivaleACompuesto() {
        Compra c = new Compra(10000, TipoCliente.COMUN, 8, TipoEnvio.RETIRO_SUCURSAL);
        CalculadoraPrecio compuesto = new CalculadoraPrecio();
        compuesto.agregarEstrategia(new DescuentoPorCantidad());
        CalculadoraPrecio separado = new CalculadoraPrecio();
        separado.agregarEstrategia(new DescuentoMayor5());
        separado.agregarEstrategia(new DescuentoMayor10());
        assertEquals(compuesto.calcularConDetalle(c).precioFinal,
                separado.calcularConDetalle(c).precioFinal, DELTA);
    }

    @Test
    void envioNormal5000Express10000() {
        Compra r = new Compra(10000, TipoCliente.COMUN, 1, TipoEnvio.RETIRO_SUCURSAL);
        Compra n = new Compra(10000, TipoCliente.COMUN, 1, TipoEnvio.ENVIO_NORMAL);
        Compra e = new Compra(10000, TipoCliente.COMUN, 1, TipoEnvio.ENVIO_EXPRESS);
        CalculadoraPrecio calc = new CalculadoraPrecio();
        calc.agregarEstrategia(new CostoEnvio());
        assertEquals(10000, calc.calcularConDetalle(r).precioFinal, DELTA);
        assertEquals(15000, calc.calcularConDetalle(n).precioFinal, DELTA);
        assertEquals(20000, calc.calcularConDetalle(e).precioFinal, DELTA);
    }

    @Test
    void combinacionEnunciado() {
        // Compra inicial: $100.000, VIP -15%, cantidad 12 -10%, promo -5%, Express +10000 = 82675
        Compra c = new Compra(100000, TipoCliente.VIP, 12, TipoEnvio.ENVIO_EXPRESS);
        CalculadoraPrecio calc = new CalculadoraPrecio();
        calc.agregarEstrategia(new DescuentoClienteVIP());
        calc.agregarEstrategia(new DescuentoPorCantidad());
        calc.agregarEstrategia(new PromocionEspecial(0.05, "Promocion Especial"));
        calc.agregarEstrategia(new CostoEnvio());
        assertEquals(82675, calc.calcularConDetalle(c).precioFinal, DELTA);
        // mismo pero sin detalle (testeando calcular silencioso)
        assertEquals(82675, new com.tienda.controller.TiendaController()
                .calcularPrecio(c, new DescuentoClienteVIP(), new DescuentoPorCantidad(),
                        new PromocionEspecial(0.05, "Promo"), new CostoEnvio()), DELTA);
    }

    @Test
    void promocionParametrizable() {
        Compra c = new Compra(10000, TipoCliente.COMUN, 1, TipoEnvio.RETIRO_SUCURSAL);
        CalculadoraPrecio calc = new CalculadoraPrecio();
        calc.agregarEstrategia(new PromocionEspecial(0.10, "Promo 10%"));
        assertEquals(9000, calc.calcularConDetalle(c).precioFinal, DELTA);
    }
}
