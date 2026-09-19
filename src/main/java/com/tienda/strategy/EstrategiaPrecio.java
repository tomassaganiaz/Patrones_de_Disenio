package com.tienda.strategy;

import com.tienda.model.Compra;

public interface EstrategiaPrecio {
    double aplicar(double precio, Compra compra);
    String getDescripcion();
}
