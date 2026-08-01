package org.barbershop.audit.adapter.in.rest;

import jakarta.ws.rs.core.Response;
import org.barbershop.audit.application.AuditFilterQuery;
import org.barbershop.common.pagination.PagedResponse;
import org.barbershop.audit.application.port.in.AuditUseCase;
import org.barbershop.audit.domain.AuditAction;
import org.barbershop.audit.domain.AuditLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditRestAdapter Tests")
class AuditRestAdapterTest {

  @Mock
  private AuditUseCase useCase;

  @InjectMocks
  private AuditRestAdapter adapter;

  private AuditLog sampleLog() {
    return new AuditLog(1L, "ITEM", 1L, AuditAction.CREATE, null,
        null, "system", OffsetDateTime.now(ZoneOffset.UTC));
  }

  private PagedResponse<AuditLog> pagedResponse(List<AuditLog> data) {
    return new PagedResponse<>(data, 1, 10, data.size());
  }

  @Test
  @DisplayName("Return200WithLogs_WhenListAuditLogsCalled")
  void shouldReturn200WithLogsWhenListAuditLogsCalled() {
    // Arrange
    when(useCase.list(any(AuditFilterQuery.class))).thenReturn(pagedResponse(List.of(sampleLog())));

    // Act
    Response response = adapter.listAuditLogs(1, 10, null, null);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    verify(useCase).list(any(AuditFilterQuery.class));
  }

  @Test
  @DisplayName("Return200WithEmptyList_WhenNoLogsExist")
  void shouldReturn200WithEmptyListWhenNoLogsExist() {
    // Arrange
    when(useCase.list(any(AuditFilterQuery.class))).thenReturn(pagedResponse(List.of()));

    // Act
    Response response = adapter.listAuditLogs(1, 10, null, null);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("FilterByEntityType_WhenEntityTypeProvided")
  void shouldFilterByEntityTypeWhenEntityTypeProvided() {
    // Arrange
    when(useCase.list(any(AuditFilterQuery.class))).thenReturn(pagedResponse(List.of(sampleLog())));

    // Act
    Response response = adapter.listAuditLogs(1, 10, "ITEM", null);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    verify(useCase).list(any(AuditFilterQuery.class));
  }

  @Test
  @DisplayName("FilterByAction_WhenActionProvided")
  void shouldFilterByActionWhenActionProvided() {
    // Arrange
    when(useCase.list(any(AuditFilterQuery.class))).thenReturn(pagedResponse(List.of(sampleLog())));

    // Act
    Response response = adapter.listAuditLogs(1, 10, null, "CREATE");

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    verify(useCase).list(any(AuditFilterQuery.class));
  }

  @Test
  @DisplayName("UseDefaultPagination_WhenNullPageParamsProvided")
  void shouldUseDefaultPaginationWhenNullPageParamsProvided() {
    // Arrange
    when(useCase.list(any(AuditFilterQuery.class))).thenReturn(pagedResponse(List.of()));

    // Act
    Response response = adapter.listAuditLogs(null, null, null, null);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }
}
