package com.tienda.strategy;

import com.tienda.model.Compra;

public class CostoEnvio implements EstrategiaPrecio {
    @Override public double aplicar(double precio, Compra compra) {
        switch (compra.getTipoEnvio()) {
            case RETIRO_SUCURSAL: return precio;
            case ENVIO_NORMAL: return precio + 5000;
            case ENVIO_EXPRESS: return precio + 10000;
            default: return precio;
        }
    }
    @Override public String getDescripcion() { return "Costo de Envio (Retiro $0 / Normal $5000 / Express $10000)"; }
}
