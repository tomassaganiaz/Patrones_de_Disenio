package com.tienda.chain;

public class ResultadoValidacion {
    private final boolean aprobado;
    private final String mensaje;
    public ResultadoValidacion(boolean aprobado, String mensaje) { this.aprobado = aprobado; this.mensaje = mensaje; }
    public static ResultadoValidacion ok(String m) { return new ResultadoValidacion(true, m); }
    public static ResultadoValidacion error(String m) { return new ResultadoValidacion(false, m); }
    public boolean isAprobado() { return aprobado; }
    public String getMensaje() { return mensaje; }
    @Override public String toString() { return (aprobado ? "OK" : "ERROR") + ": " + mensaje; }
}
