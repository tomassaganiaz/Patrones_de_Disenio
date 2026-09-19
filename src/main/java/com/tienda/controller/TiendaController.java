package com.tienda.controller;

import com.tienda.chain.Handler;
import com.tienda.chain.ResultadoValidacion;
import com.tienda.chain.ValidadorCliente;
import com.tienda.chain.ValidadorPago;
import com.tienda.chain.ValidadorStock;
import com.tienda.model.Compra;
import com.tienda.model.Pedido;
import com.tienda.strategy.CalculadoraPrecio;
import com.tienda.strategy.EstrategiaPrecio;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Controller MVC: orquesta Strategy+Chain sin tocar View
public class TiendaController {

    public double calcularPrecio(Compra compra, List<EstrategiaPrecio> estrategias) {
        if (compra == null) throw new IllegalArgumentException("Compra requerida");
        if (compra.getTipoCliente() == null) throw new IllegalArgumentException("TipoCliente requerido");
        CalculadoraPrecio calc = new CalculadoraPrecio();
        for (EstrategiaPrecio e : estrategias) calc.agregarEstrategia(e);
        return calc.calcularConDetalle(compra).precioFinal;
    }
    public double calcularPrecio(Compra compra, EstrategiaPrecio... estrategias) {
        return calcularPrecio(compra, Arrays.asList(estrategias));
    }

    public ResultadoOperacion.CalculadoraDetalle calcularConDetalle(Compra compra, List<EstrategiaPrecio> estrategias) {
        if (compra == null) throw new IllegalArgumentException("Compra requerida");
        if (compra.getTipoCliente() == null) throw new IllegalArgumentException("TipoCliente requerido");
        CalculadoraPrecio calc = new CalculadoraPrecio();
        for (EstrategiaPrecio e : estrategias) calc.agregarEstrategia(e);
        CalculadoraPrecio.ResultadoCalculo r = calc.calcularConDetalle(compra);
        return new ResultadoOperacion.CalculadoraDetalle(compra.getPrecioBase(), r.detalle);
    }

    public ResultadoValidacion validarPedido(Pedido pedido, Handler cadena) {
        if (pedido == null) throw new IllegalArgumentException("Pedido requerido");
        if (cadena == null) return ResultadoValidacion.ok("Sin validaciones - aprobado");
        return cadena.handle(pedido);
    }

    public ResultadoOperacion procesarPedido(Compra compra, List<EstrategiaPrecio> estrategias, Handler cadena,
                                             boolean clienteValido, boolean stockDisponible, boolean pagoValido) {
        double precioFinal = calcularPrecio(compra, estrategias);
        Pedido pedido = new Pedido(compra, precioFinal, clienteValido, stockDisponible, pagoValido);
        ResultadoValidacion v = validarPedido(pedido, cadena);
        ResultadoOperacion.CalculadoraDetalle detalle = calcularConDetalle(compra, estrategias);
        return new ResultadoOperacion(compra, precioFinal, pedido, v, detalle);
    }

    public ResultadoOperacion procesarPedido(Compra compra, Handler cadena, boolean clienteValido, boolean stockDisponible, boolean pagoValido, EstrategiaPrecio... estrategias) {
        return procesarPedido(compra, Arrays.asList(estrategias), cadena, clienteValido, stockDisponible, pagoValido);
    }

    public Handler crearCadenaBase() {
        Handler c = new ValidadorCliente();
        Handler s = new ValidadorStock();
        Handler p = new ValidadorPago();
        c.setSiguiente(s).setSiguiente(p);
        return c;
    }

    public Handler crearCadenaCon(Handler extra) {
        Handler base = crearCadenaBase();
        Handler tail = base;
        while (tail.getSiguiente() != null) tail = tail.getSiguiente();
        tail.setSiguiente(extra);
        return base;
    }

    public List<EstrategiaPrecio> estrategias(EstrategiaPrecio... e) { return new ArrayList<EstrategiaPrecio>(Arrays.asList(e)); }
}
