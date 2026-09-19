package com.tienda.future.chain;

import com.tienda.chain.Handler;
import com.tienda.chain.ResultadoValidacion;
import com.tienda.model.Pedido;
import java.util.Set;
import java.util.HashSet;

public class ValidadorCupon extends Handler {
    private final Set<String> validos = new HashSet<String>();
    private final String cupon;
    public ValidadorCupon(String cupon, String... validos) {
        this.cupon = cupon;
        for (String s : validos) this.validos.add(s);
    }
    @Override protected ResultadoValidacion validar(Pedido p) {
        if (cupon == null || cupon.isEmpty()) return ResultadoValidacion.ok("Sin cupon");
        if (!validos.contains(cupon)) return ResultadoValidacion.error("Cupon invalido: " + cupon);
        return ResultadoValidacion.ok("Cupon valido: " + cupon);
    }
    @Override protected String getNombre() { return "Validar Cupon"; }
}
