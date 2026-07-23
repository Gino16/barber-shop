package org.barbershop.common.filter;

import io.opentelemetry.api.trace.Span;
import jakarta.ws.rs.container.ContainerResponseContext;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

/**
 * Expone el traceId de OpenTelemetry en el header X-Trace-ID de cada respuesta.
 *
 * Usa @ServerResponseFilter (quarkus-rest reactivo) en lugar de
 * ContainerResponseFilter (JAX-RS clásico) para que el Span activo
 * sea accesible correctamente en el contexto reactivo.
 */
public class TraceIdResponseFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-ID";
    private static final String INVALID_TRACE_ID = "00000000000000000000000000000000";

    @ServerResponseFilter
    public void addTraceIdHeader(ContainerResponseContext responseContext) {
        String traceId = Span.current().getSpanContext().getTraceId();

        if (traceId != null && !traceId.isBlank() && !traceId.equals(INVALID_TRACE_ID)) {
            responseContext.getHeaders().add(TRACE_ID_HEADER, traceId);
        }
    }
}
