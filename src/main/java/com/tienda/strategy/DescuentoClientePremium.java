package com.tienda.strategy;

import com.tienda.model.Compra;
import com.tienda.model.TipoCliente;

public class DescuentoClientePremium implements EstrategiaPrecio {
    @Override public double aplicar(double precio, Compra compra) {
        return compra.getTipoCliente() == TipoCliente.PREMIUM ? precio * 0.90 : precio;
    }
    @Override public String getDescripcion() { return "Cliente Premium (-10%)"; }
}
