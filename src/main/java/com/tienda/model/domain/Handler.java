package com.tienda.model.domain;

// Contrato del Chain: eslabon que valida un Pedido y delega al siguiente.
public abstract class Handler {
    protected Handler siguiente;

    // Enlaza el siguiente eslabon y lo devuelve para encadenar.
    public Handler setSiguiente(Handler siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    // Recorre la cadena: registra cada paso y corta si uno rechaza.
    public boolean manejar(Pedido pedido) {
        ResultadoValidacion resultado = validar(pedido);
        pedido.registrarPaso(nombre(), resultado.isOk(), resultado.getMotivo());
        if (!resultado.isOk()) {
            pedido.rechazar(nombre(), resultado.getMotivo());
            return false;
        }
        if (siguiente != null) {
            return siguiente.manejar(pedido);
        }
        return true;
    }

    // Valida el pedido y devuelve si pasa o el motivo del rechazo.
    protected abstract ResultadoValidacion validar(Pedido pedido);

    // Nombre del eslabon, para mostrar en el historial del Pedido.
    protected abstract String nombre();
}