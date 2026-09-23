package com.tienda.model.services;

import com.tienda.model.domain.Compra;
import com.tienda.model.domain.EstrategiaDescuento;

// Servicio que ejecuta el Strategy: aplica las estrategias de una Compra
// en orden, guarda el historial y deja el precio final en la Compra.
public class CalculoPrecioService {

    public double calcular(Compra compra) {
        double precio = compra.getPrecioInicial();
        for (EstrategiaDescuento estrategia : compra.getEstrategias()) {
            precio = estrategia.aplicar(compra, precio);
            compra.registrarPasoHistorial(estrategia.describir(), precio);
        }
        compra.setPrecioFinal(precio);
        return precio;
    }
}