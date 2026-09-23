package com.tienda.model.services;

import com.tienda.model.domain.Compra;
import com.tienda.model.domain.EstrategiaDescuento;

// Regla: el envio normal suma $5.000 al precio.
public class EnvioNormal implements EstrategiaDescuento {
    @Override
    public double aplicar(Compra compra, double precioActual) {
        return precioActual + 5000;
    }

    @Override
    public String describir() {
        return "Envio: Normal (+$5.000)";
    }
}