package com.tienda.chain;

import com.tienda.model.Pedido;

public class ValidadorCliente extends Handler {
    @Override protected ResultadoValidacion validar(Pedido p) {
        if (!p.isClienteValido()) return ResultadoValidacion.error("Cliente no valido / no registrado");
        if (p.getCompra() == null) return ResultadoValidacion.error("Compra nula");
        return ResultadoValidacion.ok("Cliente validado correctamente");
    }
    @Override protected String getNombre() { return "Validar Cliente"; }
}
