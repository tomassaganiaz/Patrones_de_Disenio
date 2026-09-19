package com.tienda.chain;

import com.tienda.model.Pedido;

public class ValidadorStock extends Handler {
    @Override protected ResultadoValidacion validar(Pedido p) {
        if (p.getCompra() == null) return ResultadoValidacion.error("Compra nula");
        if (!p.isStockDisponible()) return ResultadoValidacion.error("Stock insuficiente para cantidad solicitada (" + p.getCompra().getCantidadProductos() + " unidades)");
        return ResultadoValidacion.ok("Stock disponible");
    }
    @Override protected String getNombre() { return "Validar Stock"; }
}
