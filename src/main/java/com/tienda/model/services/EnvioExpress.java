package com.tienda.model.services;

import com.tienda.model.domain.Compra;
import com.tienda.model.domain.EstrategiaDescuento;

// Regla: el envio express suma $10.000 al precio.
public class EnvioExpress implements EstrategiaDescuento {
    @Override
    public double aplicar(Compra compra, double precioActual) {
        return precioActual + 10000;
    }

    @Override
    public String describir() {
        return "Envio: Express (+$10.000)";
    }
}