package com.tienda.model;

public class Pedido {
    private final Compra compra;
    private double precioFinal;
    private boolean clienteValido;
    private boolean stockDisponible;
    private boolean pagoValido;

    public Pedido(Compra compra, double precioFinal) {
        this.compra = compra;
        this.precioFinal = precioFinal;
        this.clienteValido = true;
        this.stockDisponible = true;
        this.pagoValido = true;
    }

    public Pedido(Compra compra, double precioFinal, boolean clienteValido, boolean stockDisponible, boolean pagoValido) {
        this.compra = compra;
        this.precioFinal = precioFinal;
        this.clienteValido = clienteValido;
        this.stockDisponible = stockDisponible;
        this.pagoValido = pagoValido;
    }

    public Compra getCompra() { return compra; }
    public double getPrecioFinal() { return precioFinal; }
    public void setPrecioFinal(double precioFinal) { this.precioFinal = precioFinal; }
    public boolean isClienteValido() { return clienteValido; }
    public void setClienteValido(boolean clienteValido) { this.clienteValido = clienteValido; }
    public boolean isStockDisponible() { return stockDisponible; }
    public void setStockDisponible(boolean stockDisponible) { this.stockDisponible = stockDisponible; }
    public boolean isPagoValido() { return pagoValido; }
    public void setPagoValido(boolean pagoValido) { this.pagoValido = pagoValido; }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US, "Pedido{compra=%s, precioFinal=%.2f, clienteValido=%s, stock=%s, pago=%s}",
                compra, precioFinal, clienteValido, stockDisponible, pagoValido);
    }
}
