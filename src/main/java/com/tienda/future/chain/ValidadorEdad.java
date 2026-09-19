package com.tienda.future.chain;

import com.tienda.chain.Handler;
import com.tienda.chain.ResultadoValidacion;
import com.tienda.model.Pedido;

public class ValidadorEdad extends Handler {
    private final int edad;
    private final int minimo;
    public ValidadorEdad(int edad, int minimo) { this.edad = edad; this.minimo = minimo; }
    @Override protected ResultadoValidacion validar(Pedido p) {
        if (edad < minimo) return ResultadoValidacion.error("Edad minima " + minimo + " no cumplida");
        return ResultadoValidacion.ok("Edad OK (" + edad + ")");
    }
    @Override protected String getNombre() { return "Validar Edad"; }
}
