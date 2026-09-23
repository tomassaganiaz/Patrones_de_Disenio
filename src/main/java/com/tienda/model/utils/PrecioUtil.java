package com.tienda.model.utils;

import java.util.Locale;

// Utilidad de formato compartida entre services y view.
public final class PrecioUtil {
    private PrecioUtil() {}

    // Formatea un monto como "$100.000" (con separador de miles).
    public static String formatearPrecio(double valor) {
        String conComa = String.format(Locale.US, "%,.0f", valor);
        return "$" + conComa.replace(',', '.');
    }
}