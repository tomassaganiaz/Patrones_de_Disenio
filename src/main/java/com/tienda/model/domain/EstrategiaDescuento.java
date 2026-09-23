package com.tienda.model.domain;

// Contrato del Strategy: toda regla que modifica el precio de una Compra.
public interface EstrategiaDescuento {

    // Aplica la regla sobre el precio acumulado y devuelve el nuevo precio.
    double aplicar(Compra compra, double precioActual);

    // Texto legible para mostrar en el detalle de la compra.
    String describir();
}