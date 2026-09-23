package com.tienda.model.services;

import com.tienda.model.domain.Handler;
import com.tienda.model.domain.Pedido;
import com.tienda.model.domain.ResultadoValidacion;

// Valida que el pago haya sido aprobado por la entidad de pago.
public class ValidarPago extends Handler {
    @Override
    protected ResultadoValidacion validar(Pedido pedido) {
        if (pedido.isPagoAprobado()) {
            return ResultadoValidacion.ok();
        }
        return ResultadoValidacion.error(
                "El pago fue rechazado por la entidad de pago");
    }

    @Override
    protected String nombre() {
        return "Validar Pago";
    }
}