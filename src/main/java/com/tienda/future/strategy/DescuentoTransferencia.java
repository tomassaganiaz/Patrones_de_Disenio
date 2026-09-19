package com.tienda.future.strategy;

import com.tienda.model.Compra;
import com.tienda.strategy.EstrategiaPrecio;

// Futuro: descuento por medio de pago (no requiere modificar base)
public class DescuentoTransferencia implements EstrategiaPrecio {
    private final double pct;
    public DescuentoTransferencia(double pct) { this.pct = pct; }
    public DescuentoTransferencia() { this(0.05); }
    @Override public double aplicar(double precio, Compra c) { return precio * (1 - pct); }
    @Override public String getDescripcion() { return "Transferencia (-" + (int)(pct*100) + "%)"; }
}
