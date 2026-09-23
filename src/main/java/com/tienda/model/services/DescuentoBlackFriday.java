package com.tienda.model.services;

import com.tienda.model.domain.Compra;
import com.tienda.model.domain.EstrategiaDescuento;

// DESAFIO: nueva estrategia sin tocar ninguna de las existentes (OCP).
public class DescuentoBlackFriday implements EstrategiaDescuento {
    @Override
    public double aplicar(Compra compra, double precioActual) {
        return precioActual * 0.75;
    }

    @Override
    public String describir() {
        return "Promocion Black Friday (-25%)";
    }
}