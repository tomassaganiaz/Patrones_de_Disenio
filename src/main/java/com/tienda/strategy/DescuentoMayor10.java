package com.tienda.strategy;

import com.tienda.model.Compra;

// Regla independiente: >10 productos => -10%
public class DescuentoMayor10 implements EstrategiaPrecio {
    @Override public double aplicar(double precio, Compra c) {
        return c.getCantidadProductos() > 10 ? precio * 0.90 : precio;
    }
    @Override public String getDescripcion() { return "Descuento >10 productos (-10%)"; }
}