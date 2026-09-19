package com.tienda.strategy;

import com.tienda.model.Compra;

public class DescuentoClienteComun implements EstrategiaPrecio {
    @Override public double aplicar(double precio, Compra compra) { return precio; }
    @Override public String getDescripcion() { return "Cliente Comun (0% dto)"; }
}
