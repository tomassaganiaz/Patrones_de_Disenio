package com.tienda.model.services;

import com.tienda.model.domain.Compra;
import com.tienda.model.domain.EstrategiaDescuento;

// Regla: descuento por cantidad (-10% si mas de 10, -5% si mas de 5).
public class DescuentoPorCantidad implements EstrategiaDescuento {
    @Override
    public double aplicar(Compra compra, double precioActual) {
        if (compra.getCantidadProductos() > 10) {
            return precioActual * 0.90;
        }
        if (compra.getCantidadProductos() > 5) {
            return precioActual * 0.95;
        }
        return precioActual;
    }

    @Override
    public String describir() {
        return "Descuento por cantidad";
    }
}