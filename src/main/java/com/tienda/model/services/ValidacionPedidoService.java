package com.tienda.model.services;

import com.tienda.model.domain.Handler;
import com.tienda.model.domain.Pedido;

// Servicio que arma la cadena de validacion y la ejecuta sobre un Pedido.
// Con el flag incluirLimiteCompra enchufa el desafio al final (OCP).
public class ValidacionPedidoService {
    private final Handler cadena;

    public ValidacionPedidoService() {
        this(false);
    }

    public ValidacionPedidoService(boolean incluirLimiteCompra) {
        this.cadena = construirCadena(incluirLimiteCompra);
    }

    // Cadena base: Cliente -> Stock -> Pago, y Limite si se pide.
    private Handler construirCadena(boolean incluirLimiteCompra) {
        ValidarCliente cliente = new ValidarCliente();
        ValidarStock stock = new ValidarStock();
        ValidarPago pago = new ValidarPago();
        cliente.setSiguiente(stock).setSiguiente(pago);
        if (incluirLimiteCompra) {
            pago.setSiguiente(new ValidarLimiteCompra());
        }
        return cliente;
    }

    // Corre la cadena y deja el pedido aprobado si paso todos los controles.
    public boolean procesar(Pedido pedido) {
        boolean aprobado = cadena.manejar(pedido);
        if (aprobado) {
            pedido.aprobar();
        }
        return aprobado;
    }
}