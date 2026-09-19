package com.tienda.view;

import com.tienda.chain.ResultadoValidacion;
import com.tienda.controller.ResultadoOperacion;
import com.tienda.model.Compra;

public class TiendaView {

    public void mostrarEncabezado(String t) {
        System.out.println("------------------------------------------------");
        System.out.println(t);
        System.out.println("------------------------------------------------");
    }
    public void mostrarCompra(Compra c) { System.out.println("Compra: " + c); }

    public void mostrarCalculo(ResultadoOperacion op) {
        System.out.println("\n[STRATEGY] Calculando precio final:");
        for (String s : op.getDetalleCalculo().pasos) System.out.println("  -> " + s);
        System.out.println("  => PRECIO FINAL: $" + String.format(java.util.Locale.US, "%.2f", op.getPrecioFinal()));
    }
    public void mostrarCalculoDetalle(ResultadoOperacion.CalculadoraDetalle d, double precio) {
        System.out.println("\n[STRATEGY] Calculando precio final:");
        for (String s : d.pasos) System.out.println("  -> " + s);
        System.out.println("  => PRECIO FINAL: $" + String.format(java.util.Locale.US, "%.2f", precio));
    }
    public void mostrarChainHeader() { System.out.println("\n[CHAIN] Validando pedido:"); }
    public void mostrarResultadoFinal(ResultadoValidacion r) {
        System.out.println("\n  Resultado final: " + r);
        System.out.println(r.isAprobado() ? "  >>> PEDIDO CONFIRMADO <<<" : "  >>> PEDIDO RECHAZADO <<<");
        System.out.println();
    }
    public void mostrarCaso(String n, ResultadoOperacion op) {
        mostrarEncabezado(n);
        mostrarCompra(op.getCompra());
        mostrarCalculo(op);
        System.out.println("\n  Resultado final: " + op.getValidacion());
        System.out.println(op.isAprobado() ? "  >>> PEDIDO CONFIRMADO <<<" : "  >>> PEDIDO RECHAZADO <<<");
        System.out.println();
    }
    public void mostrarExplicacion() {
        System.out.println("\n================================================");
        System.out.println("  EXPLICACION - PATRONES Y SOLID");
        System.out.println("================================================\n");
        System.out.println("STRATEGY: encapsula reglas de precio intercambiables (OCP/SRP).");
        System.out.println("Varias estrategias porque descuentos/costos son acumulativos.\n");
        System.out.println("CHAIN: desacopla validaciones, cada handler decide si sigue.");
        System.out.println("En ERROR corta la cadena y retorna motivo.\n");
        System.out.println("SOLID: SRP/OCP/LSP/ISP/DIP. MVC: Model/View/Controller testeable.");
    }
}
