package org.barbershop.audit.adapter.in.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.barbershop.api.AuditLogsApi;
import org.barbershop.api.model.AuditLogResponse;
import org.barbershop.api.model.AuditLogResponse.ActionEnum;
import org.barbershop.api.model.PaginatedAuditLogsResponse;
import org.barbershop.api.model.PaginationResponse;
import org.barbershop.audit.application.AuditFilterQuery;
import org.barbershop.audit.application.port.in.AuditUseCase;
import org.barbershop.audit.domain.AuditAction;
import org.barbershop.audit.domain.AuditLog;
import org.barbershop.common.pagination.PagedResponse;

@ApplicationScoped
public class AuditRestAdapter implements AuditLogsApi {

  private final AuditUseCase useCase;

  @Inject
  public AuditRestAdapter(AuditUseCase useCase) {
    this.useCase = useCase;
  }

  @Override
  public Response listAuditLogs(
      Integer page,
      Integer pageSize,
      String entityType,
      String action) {

    AuditFilterQuery query = new AuditFilterQuery(
        entityType,
        action != null ? AuditAction.valueOf(action) : null,
        page != null ? page : 1,
        pageSize != null ? pageSize : 10
    );

    PagedResponse<AuditLog> pagedResult = useCase.list(query);

    PaginatedAuditLogsResponse response = PaginatedAuditLogsResponse.builder()
        .data(pagedResult.data().stream().map(this::toDTO).toList())
        .pagination(buildPaginationResponse(pagedResult))
        .build();

    return Response.ok(response).build();
  }

  private AuditLogResponse toDTO(AuditLog log) {
    return AuditLogResponse.builder()
        .id(log.id())
        .entityType(log.entityType())
        .entityId(log.entityId())
        .action(ActionEnum.valueOf(log.action().name()))
        .oldValues(log.oldValues())
        .newValues(log.newValues())
        .userName(log.userName())
        .timestamp(log.timestamp())
        .build();
  }

  private PaginationResponse buildPaginationResponse(PagedResponse<AuditLog> pagedResult) {
    return new PaginationResponse(pagedResult.page(), pagedResult.pageSize(), pagedResult.total(),
        pagedResult.totalPages(), pagedResult.hasNextPage());
  }
}
