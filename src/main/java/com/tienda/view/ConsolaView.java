package com.tienda.view;

import com.tienda.model.domain.Compra;
import com.tienda.model.domain.Pedido;
import com.tienda.model.utils.PrecioUtil;
import java.util.List;

// Capa Vista: unica responsable de imprimir en consola.
// Si el sistema pasara a una API, alcanza con reemplazar esta clase.
public class ConsolaView {
    private static final String LINEA =
            "=================================================================";

    public void mostrarTitulo(String titulo) {
        System.out.println("\n" + LINEA);
        System.out.println(titulo);
        System.out.println(LINEA);
    }

    public void mostrarCalculoCompra(Compra compra) {
        System.out.println("Precio inicial: "
                + PrecioUtil.formatearPrecio(compra.getPrecioInicial()));
        for (Compra.PasoHistorial paso : compra.getHistorial()) {
            System.out.printf("  -> %-40s %s%n",
                    paso.descripcion,
                    PrecioUtil.formatearPrecio(paso.precio));
        }
        System.out.println("Precio final: "
                + PrecioUtil.formatearPrecio(compra.getPrecioFinal()));
    }

    public void mostrarInicioPedido(Pedido pedido) {
        System.out.println("\n--- Procesando " + pedido.getIdPedido() + " ---");
    }

    public void mostrarResultadoPedido(Pedido pedido) {
        for (Pedido.PasoValidacion paso : pedido.getPasos()) {
            String estado = paso.ok
                    ? "OK"
                    : "ERROR - " + paso.motivo;
            System.out.println("  " + paso.nombre + ": " + estado);
        }
        if (pedido.getAprobado() != null && pedido.getAprobado()) {
            System.out.println("Resultado: APROBADO. Precio final: "
                    + PrecioUtil.formatearPrecio(pedido.getPrecioFinal()));
        } else {
            System.out.println("Resultado: RECHAZADO en '"
                    + pedido.getHandlerRechazo() + "'. Motivo: "
                    + pedido.getMotivoRechazo());
        }
    }

    public void mostrarPedidosPersistidos(List<Pedido> pedidos) {
        System.out.println("\nPedidos guardados en el DAO:");
        for (Pedido pedido : pedidos) {
            boolean aprobado = pedido.getAprobado() != null
                    && pedido.getAprobado();
            String estado = aprobado ? "APROBADO" : "RECHAZADO";
            System.out.println("  " + pedido.getIdPedido() + ": " + estado);
        }
    }
}