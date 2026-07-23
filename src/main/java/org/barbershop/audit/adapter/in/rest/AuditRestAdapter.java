package org.barbershop.audit.adapter.in.rest;

import org.barbershop.audit.application.AuditFilterQuery;
import org.barbershop.audit.application.PagedResponse;
import org.barbershop.audit.application.port.in.AuditUseCase;
import org.barbershop.audit.domain.AuditAction;
import org.barbershop.audit.domain.AuditLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
@Path("/audit-logs")
@Produces("application/json")
public class AuditRestAdapter {

  private final AuditUseCase useCase;

  @Inject
  public AuditRestAdapter(AuditUseCase useCase) {
    this.useCase = useCase;
  }

  @GET
  public Response listAuditLogs(
      @QueryParam("entityType") String entityType,
      @QueryParam("action") String action,
      @QueryParam("page") Integer page,
      @QueryParam("pageSize") Integer pageSize) {
    
    AuditFilterQuery query = new AuditFilterQuery(
        entityType,
        action != null ? AuditAction.valueOf(action) : null,
        page != null ? page : 1,
        pageSize != null ? pageSize : 10
    );
    
    PagedResponse<AuditLog> pagedResult = useCase.list(query);
    
    PaginatedAuditLogResponse response = new PaginatedAuditLogResponse(
        pagedResult.data().stream().map(this::toDTO).toList(),
        new PaginationInfo(pagedResult.page(), pagedResult.pageSize(), pagedResult.total(),
                          pagedResult.totalPages(), pagedResult.hasNextPage())
    );
    
    return Response.ok(response).build();
  }

  private AuditLogDTO toDTO(AuditLog log) {
    return new AuditLogDTO(log.id(), log.entityType(), log.entityId(), log.action().name(),
        log.oldValues(), log.newValues(), log.userName(), log.timestamp());
  }

  public static class PaginatedAuditLogResponse {
    public List<AuditLogDTO> data;
    public PaginationInfo pagination;

    public PaginatedAuditLogResponse(List<AuditLogDTO> data, PaginationInfo pagination) {
      this.data = data;
      this.pagination = pagination;
    }
  }

  public static class PaginationInfo {
    public int page;
    public int pageSize;
    public long total;
    public int totalPages;
    public boolean hasNextPage;

    public PaginationInfo(int page, int pageSize, long total, int totalPages, boolean hasNextPage) {
      this.page = page;
      this.pageSize = pageSize;
      this.total = total;
      this.totalPages = totalPages;
      this.hasNextPage = hasNextPage;
    }
  }
}
