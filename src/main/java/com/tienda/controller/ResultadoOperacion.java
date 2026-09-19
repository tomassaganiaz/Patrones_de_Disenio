package com.tienda.controller;

import com.tienda.chain.ResultadoValidacion;
import com.tienda.model.Compra;
import com.tienda.model.Pedido;

public class ResultadoOperacion {
    private final Compra compra;
    private final double precioFinal;
    private final Pedido pedido;
    private final ResultadoValidacion validacion;
    private final CalculadoraDetalle detalleCalculo;
    public ResultadoOperacion(Compra compra, double precioFinal, Pedido pedido, ResultadoValidacion validacion, CalculadoraDetalle detalle) {
        this.compra = compra; this.precioFinal = precioFinal; this.pedido = pedido; this.validacion = validacion; this.detalleCalculo = detalle;
    }
    public Compra getCompra() { return compra; }
    public double getPrecioFinal() { return precioFinal; }
    public Pedido getPedido() { return pedido; }
    public ResultadoValidacion getValidacion() { return validacion; }
    public CalculadoraDetalle getDetalleCalculo() { return detalleCalculo; }
    public boolean isAprobado() { return validacion != null && validacion.isAprobado(); }

    public static class CalculadoraDetalle {
        public final double precioBase;
        public final java.util.List<String> pasos;
        public CalculadoraDetalle(double precioBase, java.util.List<String> pasos) { this.precioBase = precioBase; this.pasos = pasos; }
    }
}
