package com.tienda.model.services;

import com.tienda.model.domain.Compra;
import com.tienda.model.domain.EstrategiaDescuento;

// Regla: el cliente premium descuenta 10%.
public class ClientePremium implements EstrategiaDescuento {
    @Override
    public double aplicar(Compra compra, double precioActual) {
        return precioActual * 0.90;
    }

    @Override
    public String describir() {
        return "Cliente Premium (-10%)";
    }
}