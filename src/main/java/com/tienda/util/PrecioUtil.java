package com.tienda.util;

import java.util.Locale;

// E02: formato con Locale.US evita coma en es-AR
public final class PrecioUtil {
    private PrecioUtil() {}
    public static String fmt(double v) { return String.format(Locale.US, "%.2f", v); }
    public static String fmtMoney(double v) { return "$" + fmt(v); }
}
