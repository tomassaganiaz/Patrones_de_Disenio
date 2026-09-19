package com.tienda.chain;

import com.tienda.model.Compra;
import com.tienda.model.Pedido;
import com.tienda.model.TipoCliente;
import com.tienda.model.TipoEnvio;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChainTest {

    private Pedido pedido(boolean clienteOk, boolean stockOk, boolean pagoOk) {
        Compra c = new Compra(10000, TipoCliente.COMUN, 1, TipoEnvio.RETIRO_SUCURSAL);
        return new Pedido(c, 10000, clienteOk, stockOk, pagoOk);
    }

    private Handler cadenaBase() {
        Handler cl = new ValidadorCliente();
        Handler st = new ValidadorStock();
        Handler pa = new ValidadorPago();
        cl.setSiguiente(st).setSiguiente(pa);
        return cl;
    }

    @Test
    void pedidoAprobadoPasaTodaLaCadena() {
        ResultadoValidacion r = cadenaBase().handle(pedido(true, true, true));
        assertTrue(r.isAprobado());
        assertTrue(r.getMensaje().contains("aprobado") || r.getMensaje().contains("validaciones"));
    }

    @Test
    void clienteInvalidoCortaEnPrimerEslabon() {
        ResultadoValidacion r = cadenaBase().handle(pedido(false, true, true));
        assertFalse(r.isAprobado());
        assertTrue(r.getMensaje().toLowerCase().contains("cliente"));
    }

    @Test
    void stockInsuficienteCortaEnSegundo() {
        // Cliente OK, stock falla -> no debe llegar a pago
        Pedido p = pedido(true, false, false); // pago también fallaría pero debe cortar antes
        ResultadoValidacion r = cadenaBase().handle(p);
        assertFalse(r.isAprobado());
        assertTrue(r.getMensaje().toLowerCase().contains("stock"));
    }

    @Test
    void pagoRechazadoCortaEnTercero() {
        ResultadoValidacion r = cadenaBase().handle(pedido(true, true, false));
        assertFalse(r.isAprobado());
        assertTrue(r.getMensaje().toLowerCase().contains("pago"));
    }

    @Test
    void siguienteNuloAprobado() {
        Handler soloCliente = new ValidadorCliente();
        ResultadoValidacion r = soloCliente.handle(pedido(true, true, true));
        assertTrue(r.isAprobado());
    }

    @Test
    void outputInyectableCapturaPasos() {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream ps = new java.io.PrintStream(bos);
        ResultadoValidacion r = cadenaBase().setOutput(ps).handle(pedido(true, true, true));
        assertTrue(r.isAprobado());
        String log = bos.toString();
        assertTrue(log.contains("Validar Cliente"));
        assertTrue(log.contains("Validar Stock"));
        assertTrue(log.contains("Validar Pago"));
    }

    @Test
    void cadenaConValidadorExtraOCP() {
        Handler base = cadenaBase();
        // busca tail y agrega límite
        Handler tail = base;
        while (tail.getSiguiente() != null) tail = tail.getSiguiente();
        tail.setSiguiente(new com.tienda.challenge.ValidadorLimiteCompra(5000));

        Compra c = new Compra(10000, TipoCliente.COMUN, 1, TipoEnvio.RETIRO_SUCURSAL);
        Pedido caro = new Pedido(c, 10000, true, true, true);
        assertFalse(base.handle(caro).isAprobado());

        Pedido barato = new Pedido(c, 4000, true, true, true);
        assertTrue(new ValidadorCliente(){{setSiguiente(new ValidadorStock(){{setSiguiente(new ValidadorPago(){{setSiguiente(new com.tienda.challenge.ValidadorLimiteCompra(5000));}});}});}}.handle(barato).isAprobado());
    }
}
