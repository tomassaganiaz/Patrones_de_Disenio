package com.tienda.strategy;

import com.tienda.model.Compra;

public class PromocionEspecial implements EstrategiaPrecio {
    private final double porcentaje;
    private final String nombre;
    public PromocionEspecial(double porcentaje, String nombre) { this.porcentaje = porcentaje; this.nombre = nombre; }
    public PromocionEspecial() { this(0.10, "Promocion Especial"); }
    @Override public double aplicar(double precio, Compra compra) { return precio * (1 - porcentaje); }
    @Override public String getDescripcion() { return nombre + " (-" + (int)(porcentaje*100) + "%)"; }
}
