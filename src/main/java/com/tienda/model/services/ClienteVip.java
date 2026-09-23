package com.tienda.model.services;

import com.tienda.model.domain.Compra;
import com.tienda.model.domain.EstrategiaDescuento;

// Regla: el cliente VIP descuenta 15%.
public class ClienteVip implements EstrategiaDescuento {
    @Override
    public double aplicar(Compra compra, double precioActual) {
        return precioActual * 0.85;
    }

    @Override
    public String describir() {
        return "Cliente VIP (-15%)";
    }
}