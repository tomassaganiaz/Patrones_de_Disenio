package com.tienda.future.strategy;

import com.tienda.model.Compra;
import com.tienda.strategy.EstrategiaPrecio;

// Estrategia que suma (no descuenta), va al final despues de envio
public class ImpuestoIVA implements EstrategiaPrecio {
    private final double tasa;
    public ImpuestoIVA(double tasa) { this.tasa = tasa; }
    public ImpuestoIVA() { this(0.21); }
    @Override public double aplicar(double precio, Compra c) { return precio * (1 + tasa); }
    @Override public String getDescripcion() { return "IVA (+" + (int)(tasa*100) + "%)"; }
}
