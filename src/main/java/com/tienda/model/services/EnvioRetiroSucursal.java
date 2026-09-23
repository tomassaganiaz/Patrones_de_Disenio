package com.tienda.model.services;

import com.tienda.model.domain.Compra;
import com.tienda.model.domain.EstrategiaDescuento;

// Regla: el envio con retiro en sucursal no suma costo.
public class EnvioRetiroSucursal implements EstrategiaDescuento {
    @Override
    public double aplicar(Compra compra, double precioActual) {
        return precioActual;
    }

    @Override
    public String describir() {
        return "Envio: Retiro en sucursal (+$0)";
    }
}