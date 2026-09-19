package com.tienda.chain;

import com.tienda.model.Pedido;
import java.io.PrintStream;

// Chain: corta en ERROR, delega si OK. handle() final evita override accidental.
// Log inyectable via setOutput() (MVC limpio, testeable sin System.out)
public abstract class Handler {
    protected Handler siguiente;
    private PrintStream out = System.out;

    public Handler setSiguiente(Handler siguiente) {
        this.siguiente = siguiente;
        siguiente.out = this.out;
        return siguiente;
    }
    public Handler getSiguiente() { return siguiente; }
    public Handler setOutput(PrintStream out) {
        this.out = out;
        if (siguiente != null) siguiente.setOutput(out);
        return this;
    }

    public final ResultadoValidacion handle(Pedido pedido) {
        if (pedido == null) return ResultadoValidacion.error("Pedido nulo");
        ResultadoValidacion r = validar(pedido);
        out.println("  [" + getNombre() + "] " + r);
        if (!r.isAprobado()) return r;
        if (siguiente != null) return siguiente.handle(pedido);
        return ResultadoValidacion.ok("Pedido aprobado - todas las validaciones pasaron");
    }

    protected abstract ResultadoValidacion validar(Pedido pedido);
    protected abstract String getNombre();
}