package com.tienda.model.domain;

import java.util.ArrayList;
import java.util.List;

// Entidad Pedido: guarda el estado mientras recorre la cadena de validacion.
public class Pedido {
    private final String idPedido;
    private final boolean clienteHabilitado;
    private final boolean stockDisponible;
    private final boolean pagoAprobado;
    private final double precioFinal;
    private Boolean aprobado;
    private String handlerRechazo;
    private String motivoRechazo;
    private final List<PasoValidacion> pasos = new ArrayList<PasoValidacion>();

    public Pedido(String idPedido, boolean clienteHabilitado,
            boolean stockDisponible, boolean pagoAprobado,
            double precioFinal) {
        this.idPedido = idPedido;
        this.clienteHabilitado = clienteHabilitado;
        this.stockDisponible = stockDisponible;
        this.pagoAprobado = pagoAprobado;
        this.precioFinal = precioFinal;
    }

    // Registra un paso de la cadena en el historial del pedido.
    public void registrarPaso(String nombre, boolean ok, String motivo) {
        pasos.add(new PasoValidacion(nombre, ok, motivo));
    }

    public void aprobar() {
        this.aprobado = Boolean.TRUE;
    }

    // Marca el pedido como rechazado y guarda donde y por que.
    public void rechazar(String handler, String motivo) {
        this.aprobado = Boolean.FALSE;
        this.handlerRechazo = handler;
        this.motivoRechazo = motivo;
    }

    public String getIdPedido() { return idPedido; }
    public boolean isClienteHabilitado() { return clienteHabilitado; }
    public boolean isStockDisponible() { return stockDisponible; }
    public boolean isPagoAprobado() { return pagoAprobado; }
    public double getPrecioFinal() { return precioFinal; }
    public Boolean getAprobado() { return aprobado; }
    public String getHandlerRechazo() { return handlerRechazo; }
    public String getMotivoRechazo() { return motivoRechazo; }
    public List<PasoValidacion> getPasos() {
        return new ArrayList<PasoValidacion>(pasos);
    }

    // Un paso de la cadena: eslabon, si paso y el motivo.
    public static class PasoValidacion {
        public final String nombre;
        public final boolean ok;
        public final String motivo;

        public PasoValidacion(String nombre, boolean ok, String motivo) {
            this.nombre = nombre;
            this.ok = ok;
            this.motivo = motivo;
        }
    }
}