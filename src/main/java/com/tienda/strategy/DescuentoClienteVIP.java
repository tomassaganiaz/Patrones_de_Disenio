package com.tienda.strategy;

import com.tienda.model.Compra;
import com.tienda.model.TipoCliente;

public class DescuentoClienteVIP implements EstrategiaPrecio {
    @Override public double aplicar(double precio, Compra compra) {
        return compra.getTipoCliente() == TipoCliente.VIP ? precio * 0.85 : precio;
    }
    @Override public String getDescripcion() { return "Cliente VIP (-15%)"; }
}
