package com.tienda.model.services;

import com.tienda.model.domain.Compra;
import com.tienda.model.domain.EstrategiaDescuento;

// Regla parametrizable: descuenta el porcentaje que se le indique.
// Sirve para cualquier promocion porcentual sin crear una clase nueva.
public class PromocionPorcentual implements EstrategiaDescuento {
    private final double porcentaje;
    private final String nombre;

    public PromocionPorcentual(double porcentaje, String nombre) {
        this.porcentaje = porcentaje;
        this.nombre = nombre;
    }

    // Si no se pasa nombre, usa "Promocion especial".
    public PromocionPorcentual(double porcentaje) {
        this(porcentaje, "Promocion especial");
    }

    @Override
    public double aplicar(Compra compra, double precioActual) {
        return precioActual * (1 - porcentaje / 100);
    }

    @Override
    public String describir() {
        return nombre + " (-" + (int) porcentaje + "%)";
    }
}