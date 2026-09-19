package com.tienda.model;

public class Compra {
    private final double precioBase;
    private final TipoCliente tipoCliente;
    private final int cantidadProductos;
    private final TipoEnvio tipoEnvio;

    public Compra(double precioBase, TipoCliente tipoCliente, int cantidadProductos, TipoEnvio tipoEnvio) {
        this.precioBase = precioBase;
        this.tipoCliente = tipoCliente;
        this.cantidadProductos = cantidadProductos;
        this.tipoEnvio = tipoEnvio;
    }

    public double getPrecioBase() { return precioBase; }
    public TipoCliente getTipoCliente() { return tipoCliente; }
    public int getCantidadProductos() { return cantidadProductos; }
    public TipoEnvio getTipoEnvio() { return tipoEnvio; }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US, "Compra{precioBase=%.2f, cliente=%s, cantidad=%d, envio=%s}",
                precioBase, tipoCliente, cantidadProductos, tipoEnvio);
    }
}
