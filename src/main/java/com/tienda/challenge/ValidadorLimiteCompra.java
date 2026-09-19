package com.tienda.challenge;

import com.tienda.chain.Handler;
import com.tienda.chain.ResultadoValidacion;
import com.tienda.model.Pedido;

public class ValidadorLimiteCompra extends Handler {
    private final double limite;
    public ValidadorLimiteCompra(double limite) { this.limite = limite; }
    @Override protected ResultadoValidacion validar(Pedido p) {
        if (p.getPrecioFinal() > limite) return ResultadoValidacion.error(
                "Limite excedido: $" + String.format(java.util.Locale.US, "%.2f", p.getPrecioFinal()) + " > $" + String.format(java.util.Locale.US, "%.2f", limite));
        return ResultadoValidacion.ok("Limite OK (< $" + String.format(java.util.Locale.US, "%.2f", limite) + ")");
    }
    @Override protected String getNombre() { return "Validar Limite Compra [DESAFIO]"; }
}
