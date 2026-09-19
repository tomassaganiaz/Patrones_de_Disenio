package com.tienda.strategy;

import com.tienda.model.Compra;

// Compatibilidad: compone las reglas independientes Mayor5 + Mayor10 (mismo resultado que antes)
public class DescuentoPorCantidad implements EstrategiaPrecio {
    private final EstrategiaPrecio m5 = new DescuentoMayor5();
    private final EstrategiaPrecio m10 = new DescuentoMayor10();
    @Override public double aplicar(double precio, Compra c) {
        return m10.aplicar(m5.aplicar(precio, c), c);
    }
    @Override public String getDescripcion() { return "Descuento por Cantidad (>5: -5%, >10: -10%)"; }
}