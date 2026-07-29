package org.barbershop.audit.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.barbershop.audit.application.AuditFilterQuery;
import org.barbershop.audit.domain.AuditAction;
import org.barbershop.audit.domain.AuditLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditPersistenceAdapter Tests")
class AuditPersistenceAdapterTest {

  @Mock
  private AuditPanacheRepository repository;

  @InjectMocks
  private AuditPersistenceAdapter adapter;

  private AuditLogJpaEntity sampleEntity() {
    AuditLogJpaEntity entity = new AuditLogJpaEntity();
    entity.id = 1L;
    entity.entityType = "ITEM";
    entity.entityId = 1L;
    entity.action = AuditAction.CREATE;
    entity.userName = "system";
    entity.timestamp = LocalDateTime.now();
    return entity;
  }

  @Test
  @DisplayName("ReturnLogs_WhenFindCalledWithNoFilters")
  @SuppressWarnings("unchecked")
  void shouldReturnLogsWhenFindCalledWithNoFilters() {
    // Arrange
    AuditFilterQuery query = new AuditFilterQuery(null, null, 1, 10);
    AuditLogJpaEntity entity = sampleEntity();
    PanacheQuery<AuditLogJpaEntity> mockQuery = mock(PanacheQuery.class);
    when(repository.findAll()).thenReturn(mockQuery);
    when(mockQuery.range(anyInt(), anyInt())).thenReturn(mockQuery);
    when(mockQuery.stream()).thenReturn(Stream.of(entity));

    // Act
    List<AuditLog> result = adapter.find(query);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("ITEM", result.get(0).entityType());
  }

  @Test
  @DisplayName("ReturnCount_WhenCountCalledWithNoFilters")
  @SuppressWarnings("unchecked")
  void shouldReturnCountWhenCountCalledWithNoFilters() {
    // Arrange
    AuditFilterQuery query = new AuditFilterQuery(null, null, 1, 10);
    PanacheQuery<AuditLogJpaEntity> mockQuery = mock(PanacheQuery.class);
    when(repository.findAll()).thenReturn(mockQuery);
    when(mockQuery.count()).thenReturn(5L);

    // Act
    long count = adapter.count(query);

    // Assert
    assertEquals(5L, count);
  }

  @Test
  @DisplayName("ReturnCount_WhenCountCalledWithEntityTypeFilter")
  @SuppressWarnings("unchecked")
  void shouldReturnCountWhenCountCalledWithEntityTypeFilter() {
    // Arrange
    AuditFilterQuery query = new AuditFilterQuery("ITEM", null, 1, 10);
    PanacheQuery<AuditLogJpaEntity> mockQuery = mock(PanacheQuery.class);
    when(repository.find(anyString(), any(Object[].class))).thenReturn(mockQuery);
    when(mockQuery.count()).thenReturn(3L);

    // Act
    long count = adapter.count(query);

    // Assert
    assertEquals(3L, count);
  }

  @Test
  @DisplayName("SaveNewLog_WhenSaveCalledWithNullId")
  void shouldSaveNewLogWhenSaveCalledWithNullId() {
    // Arrange
    AuditLog log = new AuditLog(null, "ITEM", 1L, AuditAction.CREATE,
        null, null, "system", OffsetDateTime.now(ZoneOffset.UTC));
    // Panache void methods are no-ops by default in mocks — no stubbing needed

    // Act
    AuditLog result = adapter.save(log);

    // Assert
    assertNotNull(result);
    assertEquals("ITEM", result.entityType());
    assertEquals(AuditAction.CREATE, result.action());
  }

  @Test
  @DisplayName("ReturnLogWithTimestamp_WhenSaveCalledWithTimestamp")
  void shouldReturnLogWithTimestampWhenSaveCalledWithTimestamp() {
    // Arrange
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    AuditLog log = new AuditLog(null, "CUSTOMER", 2L, AuditAction.UPDATE,
        null, null, "system", now);
    doNothing().when(repository).persist(any(AuditLogJpaEntity.class));

    // Act
    AuditLog result = adapter.save(log);

    // Assert
    assertNotNull(result.timestamp());
    assertEquals("CUSTOMER", result.entityType());
  }
}
