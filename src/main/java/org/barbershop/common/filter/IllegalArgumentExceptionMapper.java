package org.barbershop.common.filter;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

  @Override
  public Response toResponse(IllegalArgumentException exception) {
    return Response.status(Response.Status.BAD_REQUEST)
        .type(MediaType.APPLICATION_JSON)
        .entity(Map.of(
            "status", Response.Status.BAD_REQUEST.getStatusCode(),
            "error", "Bad Request",
            "message", exception.getMessage() != null
                ? exception.getMessage() : "Solicitud inválida"))
        .build();
  }
}
