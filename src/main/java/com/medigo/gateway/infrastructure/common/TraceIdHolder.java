package com.medigo.gateway.infrastructure.common;

/**
 * ThreadLocal para propagar el X-Trace-ID a lo largo de la petición.
 */
public final class TraceIdHolder {

    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    private TraceIdHolder() {}

    public static void set(String traceId) { HOLDER.set(traceId); }
    public static String get()             { return HOLDER.get(); }
    public static void clear()             { HOLDER.remove(); }
}
