package com.tienda.model.domain;

import java.util.ArrayList;
import java.util.List;

// Entidad Compra: junta los datos y las estrategias a aplicar.
// No tiene reglas de descuento: eso lo hace CalculoPrecioService.
public class Compra {
    private final double precioInicial;
    private final int cantidadProductos;
    private final List<EstrategiaDescuento> estrategias = new ArrayList<EstrategiaDescuento>();
    private final List<PasoHistorial> historial = new ArrayList<PasoHistorial>();
    private Double precioFinal;

    public Compra(double precioInicial, int cantidadProductos) {
        this.precioInicial = precioInicial;
        this.cantidadProductos = cantidadProductos;
    }

    // Agrega una estrategia mas a aplicar (fluent para encadenar).
    public Compra agregarEstrategia(EstrategiaDescuento estrategia) {
        estrategias.add(estrategia);
        return this;
    }

    public void registrarPasoHistorial(String descripcion, double precio) {
        historial.add(new PasoHistorial(descripcion, precio));
    }

    public double getPrecioInicial() { return precioInicial; }
    public int getCantidadProductos() { return cantidadProductos; }
    public List<EstrategiaDescuento> getEstrategias() {
        return new ArrayList<EstrategiaDescuento>(estrategias);
    }
    public List<PasoHistorial> getHistorial() {
        return new ArrayList<PasoHistorial>(historial);
    }
    public Double getPrecioFinal() { return precioFinal; }
    public void setPrecioFinal(double precioFinal) { this.precioFinal = precioFinal; }

    // Un paso del historial: nombre de la regla y precio tras aplicarla.
    public static class PasoHistorial {
        public final String descripcion;
        public final double precio;

        public PasoHistorial(String descripcion, double precio) {
            this.descripcion = descripcion;
            this.precio = precio;
        }
    }
}