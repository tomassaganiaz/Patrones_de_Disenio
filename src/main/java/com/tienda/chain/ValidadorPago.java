package com.tienda.chain;

import com.tienda.model.Pedido;

public class ValidadorPago extends Handler {
    @Override protected ResultadoValidacion validar(Pedido p) {
        if (p.getCompra() == null) return ResultadoValidacion.error("Compra nula");
        if (!p.isPagoValido()) return ResultadoValidacion.error("Pago rechazado - tarjeta invalida / fondos insuficientes");
        if (p.getPrecioFinal() <= 0) return ResultadoValidacion.error("Precio final invalido");
        return ResultadoValidacion.ok("Pago validado correctamente");
    }
    @Override protected String getNombre() { return "Validar Pago"; }
}
