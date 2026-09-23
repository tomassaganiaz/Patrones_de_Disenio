package com.tienda.model.services;

import com.tienda.model.domain.Handler;
import com.tienda.model.domain.Pedido;
import com.tienda.model.domain.ResultadoValidacion;

// Valida que haya stock suficiente para completar el pedido.
public class ValidarStock extends Handler {
    @Override
    protected ResultadoValidacion validar(Pedido pedido) {
        if (pedido.isStockDisponible()) {
            return ResultadoValidacion.ok();
        }
        return ResultadoValidacion.error(
                "No hay stock suficiente para completar el pedido");
    }

    @Override
    protected String nombre() {
        return "Validar Stock";
    }
}