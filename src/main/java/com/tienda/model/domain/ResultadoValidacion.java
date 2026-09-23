package com.tienda.model.domain;

// Resultado de una validacion: si pasa y, si no, el motivo del rechazo.
public class ResultadoValidacion {
    private final boolean ok;
    private final String motivo;

    public ResultadoValidacion(boolean ok, String motivo) {
        this.ok = ok;
        this.motivo = motivo;
    }

    public static ResultadoValidacion ok() {
        return new ResultadoValidacion(true, "");
    }

    public static ResultadoValidacion error(String motivo) {
        return new ResultadoValidacion(false, motivo);
    }

    public boolean isOk() { return ok; }
    public String getMotivo() { return motivo; }
}