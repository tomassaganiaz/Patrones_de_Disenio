package com.tienda.strategy;

import com.tienda.model.Compra;

// Regla independiente: >5 y <=10 productos => -5%
public class DescuentoMayor5 implements EstrategiaPrecio {
    @Override public double aplicar(double precio, Compra c) {
        int n = c.getCantidadProductos();
        return (n > 5 && n <= 10) ? precio * 0.95 : precio;
    }
    @Override public String getDescripcion() { return "Descuento >5 productos (-5%)"; }
}