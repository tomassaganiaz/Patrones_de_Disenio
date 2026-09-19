package com.tienda.strategy;

import com.tienda.model.Compra;
import java.util.ArrayList;
import java.util.List;

// Context Strategy: aplica lista de estrategias en orden
public class CalculadoraPrecio {
    private final List<EstrategiaPrecio> estrategias = new ArrayList<EstrategiaPrecio>();

    public void agregarEstrategia(EstrategiaPrecio estrategia) { estrategias.add(estrategia); }
    public void limpiarEstrategias() { estrategias.clear(); }
    public List<EstrategiaPrecio> getEstrategias() { return new ArrayList<EstrategiaPrecio>(estrategias); }

    public double calcular(Compra compra) {
        if (compra == null) throw new IllegalArgumentException("Compra requerida");
        double precio = compra.getPrecioBase();
        System.out.println("  Precio base: $" + String.format(java.util.Locale.US, "%.2f", precio));
        for (EstrategiaPrecio e : estrategias) {
            double antes = precio;
            precio = e.aplicar(precio, compra);
            double delta = precio - antes;
            String signo = delta >= 0 ? "+" : "";
            System.out.println("  -> " + e.getDescripcion() + ": $" + String.format(java.util.Locale.US, "%.2f", antes)
                    + " -> $" + String.format(java.util.Locale.US, "%.2f", precio) + " (" + signo + String.format(java.util.Locale.US, "%.2f", delta) + ")");
        }
        return precio;
    }

    // Variante pura para Controller/tests (sin print)
    public ResultadoCalculo calcularConDetalle(Compra compra) {
        if (compra == null) throw new IllegalArgumentException("Compra requerida");
        List<String> detalle = new ArrayList<String>();
        double precio = compra.getPrecioBase();
        detalle.add(String.format(java.util.Locale.US, "Precio base: $%.2f", precio));
        for (EstrategiaPrecio e : estrategias) {
            double antes = precio;
            precio = e.aplicar(precio, compra);
            detalle.add(String.format(java.util.Locale.US, "%s: $%.2f -> $%.2f", e.getDescripcion(), antes, precio));
        }
        return new ResultadoCalculo(precio, detalle);
    }

    public static class ResultadoCalculo {
        public final double precioFinal;
        public final List<String> detalle;
        public ResultadoCalculo(double precioFinal, List<String> detalle) {
            this.precioFinal = precioFinal;
            this.detalle = detalle;
        }
    }
}
