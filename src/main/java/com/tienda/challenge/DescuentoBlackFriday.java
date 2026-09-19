package com.tienda.challenge;

import com.tienda.model.Compra;
import com.tienda.strategy.EstrategiaPrecio;

public class DescuentoBlackFriday implements EstrategiaPrecio {
    private final double pct;
    public DescuentoBlackFriday(double pct) { this.pct = pct; }
    public DescuentoBlackFriday() { this(0.25); }
    @Override public double aplicar(double precio, Compra c) { return precio * (1 - pct); }
    @Override public String getDescripcion() { return "Descuento Black Friday (-" + (int)(pct*100) + "%) [DESAFIO]"; }
}
