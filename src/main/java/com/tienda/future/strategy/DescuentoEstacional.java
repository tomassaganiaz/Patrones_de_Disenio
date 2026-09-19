package com.tienda.future.strategy;

import com.tienda.model.Compra;
import com.tienda.strategy.EstrategiaPrecio;

public class DescuentoEstacional implements EstrategiaPrecio {
    private final String temporada;
    private final double pct;
    public DescuentoEstacional(String temporada, double pct) { this.temporada = temporada; this.pct = pct; }
    @Override public double aplicar(double precio, Compra c) { return precio * (1 - pct); }
    @Override public String getDescripcion() { return "Estacional " + temporada + " (-" + (int)(pct*100) + "%)"; }
}
