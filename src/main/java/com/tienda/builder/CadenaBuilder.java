package com.tienda.builder;

import com.tienda.chain.Handler;

// E04: evita setSiguiente mal encadenado
public class CadenaBuilder {
    private Handler head;
    private Handler tail;
    public CadenaBuilder add(Handler h) {
        if (head == null) head = tail = h;
        else { tail.setSiguiente(h); tail = h; }
        return this;
    }
    public Handler build() { return head; }
}
