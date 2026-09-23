package com.tienda.model.dao;

import com.tienda.model.domain.Pedido;
import java.util.List;

// Interfaz de persistencia de Pedidos ya procesados.
// En un sistema real puede implementarse contra una base de datos.
public interface PedidoDAO {

    void guardar(Pedido pedido);

    Pedido buscarPorId(String idPedido);

    List<Pedido> listar();
}