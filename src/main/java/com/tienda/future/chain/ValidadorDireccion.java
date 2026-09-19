package com.tienda.future.chain;

import com.tienda.chain.Handler;
import com.tienda.chain.ResultadoValidacion;
import com.tienda.model.Pedido;

public class ValidadorDireccion extends Handler {
    @Override protected ResultadoValidacion validar(Pedido p) {
        if (p.getCompra().getTipoEnvio() != com.tienda.model.TipoEnvio.RETIRO_SUCURSAL && p.getCompra().getPrecioBase() <= 0)
            return ResultadoValidacion.error("Direccion invalida para envio");
        return ResultadoValidacion.ok("Direccion valida");
    }
    @Override protected String getNombre() { return "Validar Direccion"; }
}
