package com.tienda.model.services;

import com.tienda.model.domain.Handler;
import com.tienda.model.domain.Pedido;
import com.tienda.model.domain.ResultadoValidacion;
import com.tienda.model.utils.PrecioUtil;

// DESAFIO: nuevo validador que limita el monto de una compra (OCP).
public class ValidarLimiteCompra extends Handler {
    private static final double LIMITE = 500_000;

    @Override
    protected ResultadoValidacion validar(Pedido pedido) {
        if (pedido.getPrecioFinal() <= LIMITE) {
            return ResultadoValidacion.ok();
        }
        return ResultadoValidacion.error(
                "El monto (" + PrecioUtil.formatearPrecio(pedido.getPrecioFinal())
                + ") supera el limite permitido de "
                + PrecioUtil.formatearPrecio(LIMITE));
    }

    @Override
    protected String nombre() {
        return "Validar Limite de Compra";
    }
}