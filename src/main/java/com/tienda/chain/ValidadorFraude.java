package com.tienda.chain;

import com.tienda.model.Pedido;

public class ValidadorFraude extends Handler {
    @Override protected ResultadoValidacion validar(Pedido p) {
        if (p.getPrecioFinal() > 500000 && !p.isClienteValido()) return ResultadoValidacion.error("Posible fraude detectado");
        return ResultadoValidacion.ok("Sin fraude detectado");
    }
    @Override protected String getNombre() { return "Validar Fraude"; }
}
