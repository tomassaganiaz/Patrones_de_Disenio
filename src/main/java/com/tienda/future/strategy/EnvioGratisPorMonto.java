package com.tienda.future.strategy;

import com.tienda.model.Compra;
import com.tienda.strategy.EstrategiaPrecio;

// Si precio > umbral, anula costo de envio ya sumado
public class EnvioGratisPorMonto implements EstrategiaPrecio {
    private final double umbral;
    public EnvioGratisPorMonto(double umbral) { this.umbral = umbral; }
    public EnvioGratisPorMonto() { this(80000); }
    @Override public double aplicar(double precio, Compra c) {
        if (precio > umbral) {
            // asume envio normal/express ya sumado, descuenta hasta 10000
            if (c.getTipoEnvio() == com.tienda.model.TipoEnvio.ENVIO_EXPRESS) return precio - 10000;
            if (c.getTipoEnvio() == com.tienda.model.TipoEnvio.ENVIO_NORMAL) return precio - 5000;
        }
        return precio;
    }
    @Override public String getDescripcion() { return "Envio Gratis >$" + (int)umbral; }
}
