package com.tienda.controller;

import com.tienda.model.dao.PedidoDAO;
import com.tienda.model.domain.Compra;
import com.tienda.model.domain.Pedido;
import com.tienda.model.services.CalculoPrecioService;
import com.tienda.model.services.ValidacionPedidoService;
import com.tienda.view.ConsolaView;

// Capa Controlador: conecta la Vista con el Model.
// No tiene reglas de negocio: solo pide procesar y mostrar resultados.
public class TiendaController {
    private final ConsolaView view;
    private final PedidoDAO pedidoDao;
    private final CalculoPrecioService calculoPrecioService =
            new CalculoPrecioService();

    public TiendaController(ConsolaView view, PedidoDAO pedidoDao) {
        this.view = view;
        this.pedidoDao = pedidoDao;
    }

    // Calcula el precio final de la compra y lo muestra.
    public double calcularPrecioCompra(Compra compra) {
        double precioFinal = calculoPrecioService.calcular(compra);
        view.mostrarCalculoCompra(compra);
        return precioFinal;
    }

    // Procesa el pedido, lo guarda en el DAO y muestra el resultado.
    public boolean procesarPedido(Pedido pedido,
            ValidacionPedidoService validacionService) {
        view.mostrarInicioPedido(pedido);
        boolean aprobado = validacionService.procesar(pedido);
        pedidoDao.guardar(pedido);
        view.mostrarResultadoPedido(pedido);
        return aprobado;
    }

    public void mostrarPedidosPersistidos() {
        view.mostrarPedidosPersistidos(pedidoDao.listar());
    }

    public void mostrarTitulo(String titulo) {
        view.mostrarTitulo(titulo);
    }
}