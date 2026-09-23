package com.tienda.model.services;

import com.tienda.model.domain.Handler;
import com.tienda.model.domain.Pedido;
import com.tienda.model.domain.ResultadoValidacion;

// Valida que el cliente este habilitado para operar.
public class ValidarCliente extends Handler {
    @Override
    protected ResultadoValidacion validar(Pedido pedido) {
        if (pedido.isClienteHabilitado()) {
            return ResultadoValidacion.ok();
        }
        return ResultadoValidacion.error(
                "El cliente no esta habilitado para operar");
    }

    @Override
    protected String nombre() {
        return "Validar Cliente";
    }
}