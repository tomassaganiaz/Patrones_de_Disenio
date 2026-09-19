package com.tienda.builder;

import com.tienda.model.Compra;
import com.tienda.model.Pedido;

// E07: evita flags invertidos (cliente, stock, pago)
public class PedidoBuilder {
    private Compra compra;
    private double precio;
    private boolean clienteValido = true;
    private boolean stockDisponible = true;
    private boolean pagoValido = true;

    public PedidoBuilder compra(Compra c) { this.compra = c; return this; }
    public PedidoBuilder precio(double p) { this.precio = p; return this; }
    public PedidoBuilder clienteValido(boolean v) { this.clienteValido = v; return this; }
    public PedidoBuilder stock(boolean v) { this.stockDisponible = v; return this; }
    public PedidoBuilder pago(boolean v) { this.pagoValido = v; return this; }

    public Pedido build() {
        if (compra == null) throw new IllegalArgumentException("Compra requerida");
        return new Pedido(compra, precio, clienteValido, stockDisponible, pagoValido);
    }
}
