package com.tienda.model.services;

import com.tienda.model.domain.Compra;
import com.tienda.model.domain.EstrategiaDescuento;

// Regla: el cliente comun no recibe descuento.
public class ClienteComun implements EstrategiaDescuento {
    @Override
    public double aplicar(Compra compra, double precioActual) {
        return precioActual;
    }

    @Override
    public String describir() {
        return "Cliente Comun (0%)";
    }
}