package org.barbershop.audit.application;

import org.barbershop.audit.application.port.out.AuditRepositoryPort;
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
@DisplayName("AuditService Tests")
class AuditServiceTest {

  @Mock
  private AuditRepositoryPort repository;

  @InjectMocks
  private AuditService auditService;

  private AuditLog sampleLog(Long id) {
    return new AuditLog(id, "ITEM", 1L, AuditAction.CREATE, null,
        null, "system", OffsetDateTime.now(ZoneOffset.UTC));
  }

  @Test
  @DisplayName("ReturnPagedLogs_WhenListCalled")
  void shouldReturnPagedLogsWhenListCalled() {
    // Arrange
    AuditFilterQuery query = new AuditFilterQuery(null, null, 1, 10);
    List<AuditLog> logs = List.of(sampleLog(1L), sampleLog(2L));
    when(repository.find(any(AuditFilterQuery.class))).thenReturn(logs);
    when(repository.count(any(AuditFilterQuery.class))).thenReturn(2L);

    // Act
    PagedResponse<AuditLog> result = auditService.list(query);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.data().size());
    assertEquals(1, result.page());
    assertEquals(10, result.pageSize());
    assertEquals(2L, result.total());
    assertEquals(1, result.totalPages());
    assertFalse(result.hasNextPage());
    verify(repository).find(any(AuditFilterQuery.class));
    verify(repository).count(any(AuditFilterQuery.class));
  }

  @Test
  @DisplayName("ReturnEmptyPage_WhenNoLogsExist")
  void shouldReturnEmptyPageWhenNoLogsExist() {
    // Arrange
    AuditFilterQuery query = new AuditFilterQuery(null, null, 1, 10);
    when(repository.find(any(AuditFilterQuery.class))).thenReturn(List.of());
    when(repository.count(any(AuditFilterQuery.class))).thenReturn(0L);

    // Act
    PagedResponse<AuditLog> result = auditService.list(query);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.data().size());
    assertEquals(0L, result.total());
  }

  @Test
  @DisplayName("ApplyDefaults_WhenListCalledWithZeroPage")
  void shouldApplyDefaultsWhenListCalledWithZeroPage() {
    // Arrange
    AuditFilterQuery query = new AuditFilterQuery(null, null, 0, 0);
    when(repository.find(any(AuditFilterQuery.class))).thenReturn(List.of());
    when(repository.count(any(AuditFilterQuery.class))).thenReturn(0L);

    // Act
    PagedResponse<AuditLog> result = auditService.list(query);

    // Assert
    assertEquals(1, result.page());
    assertEquals(10, result.pageSize());
  }

  @Test
  @DisplayName("ReturnMultiplePages_WhenTotalExceedsPageSize")
  void shouldReturnMultiplePagesWhenTotalExceedsPageSize() {
    // Arrange
    AuditFilterQuery query = new AuditFilterQuery(null, null, 1, 5);
    when(repository.find(any(AuditFilterQuery.class))).thenReturn(List.of(sampleLog(1L)));
    when(repository.count(any(AuditFilterQuery.class))).thenReturn(12L);

    // Act
    PagedResponse<AuditLog> result = auditService.list(query);

    // Assert
    assertEquals(3, result.totalPages());
    assertTrue(result.hasNextPage());
  }
}
