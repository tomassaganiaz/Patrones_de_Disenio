package com.tienda.factory;

import com.tienda.model.Compra;
import com.tienda.strategy.*;
import java.util.ArrayList;
import java.util.List;

// E01: orden correcto descuentos -> CostoEnvio ultimo
public final class EstrategiaFactory {
    private EstrategiaFactory() {}
    public static List<EstrategiaPrecio> paraCompra(Compra c) {
        List<EstrategiaPrecio> l = new ArrayList<EstrategiaPrecio>();
        if (c.getTipoCliente() == com.tienda.model.TipoCliente.VIP) l.add(new DescuentoClienteVIP());
        else if (c.getTipoCliente() == com.tienda.model.TipoCliente.PREMIUM) l.add(new DescuentoClientePremium());
        else l.add(new DescuentoClienteComun());
        if (c.getCantidadProductos() > 5) l.add(new DescuentoMayor5());
        if (c.getCantidadProductos() > 10) l.add(new DescuentoMayor10());
        l.add(new CostoEnvio()); // siempre ultimo
        return l;
    }
    public static List<EstrategiaPrecio> conPromo(Compra c, double pct, String nombre) {
        List<EstrategiaPrecio> l = paraCompra(c);
        l.add(l.size()-1, new PromocionEspecial(pct, nombre)); // antes de envio
        return l;
    }
}
