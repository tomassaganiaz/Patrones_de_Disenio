package com.tienda.model.dao;

import com.tienda.model.domain.Pedido;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Implementacion en memoria de PedidoDAO, lista para reemplazar por
// una base de datos real sin tocar el resto del sistema.
public class PedidoDAOMemoria implements PedidoDAO {
    private final Map<String, Pedido> pedidos =
            new LinkedHashMap<String, Pedido>();

    @Override
    public void guardar(Pedido pedido) {
        pedidos.put(pedido.getIdPedido(), pedido);
    }

    @Override
    public Pedido buscarPorId(String idPedido) {
        return pedidos.get(idPedido);
    }

    @Override
    public List<Pedido> listar() {
        return new ArrayList<Pedido>(pedidos.values());
    }
}