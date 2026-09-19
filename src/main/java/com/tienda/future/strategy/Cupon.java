package com.tienda.future.strategy;

import com.tienda.model.Compra;
import com.tienda.strategy.EstrategiaPrecio;

public class Cupon implements EstrategiaPrecio {
    private final String codigo;
    private final double pct;
    public Cupon(String codigo, double pct) { this.codigo = codigo; this.pct = pct; }
    @Override public double aplicar(double precio, Compra c) { return precio * (1 - pct); }
    @Override public String getDescripcion() { return "Cupon " + codigo + " (-" + (int)(pct*100) + "%)"; }
}
