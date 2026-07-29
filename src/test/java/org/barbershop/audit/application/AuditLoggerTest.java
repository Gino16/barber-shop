package org.barbershop.audit.application;

import org.barbershop.audit.application.port.out.AuditRepositoryPort;
import org.barbershop.audit.domain.AuditAction;
import org.barbershop.audit.domain.AuditLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditLogger Tests")
class AuditLoggerTest {

  @Mock
  private AuditRepositoryPort repository;

  @InjectMocks
  private AuditLogger auditLogger;

  @Test
  @DisplayName("CallRepositorySave_WhenRecordCalled")
  void shouldCallRepositorySaveWhenRecordCalled() {
    // Arrange
    Map<String, Object> newValues = Map.of("name", "Corte");
    when(repository.save(any(AuditLog.class))).thenReturn(
        new AuditLog(1L, "ITEM", 1L, AuditAction.CREATE, null, newValues, "system", OffsetDateTime.now(ZoneOffset.UTC)));

    // Act
    auditLogger.record("ITEM", 1L, AuditAction.CREATE, null, newValues);

    // Assert
    verify(repository).save(any(AuditLog.class));
  }

  @Test
  @DisplayName("SaveAuditLogWithCorrectFields_WhenRecordCalled")
  void shouldSaveAuditLogWithCorrectFieldsWhenRecordCalled() {
    // Arrange
    Map<String, Object> oldValues = Map.of("name", "Corte Básico");
    Map<String, Object> newValues = Map.of("name", "Corte Premium");
    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    when(repository.save(any(AuditLog.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    auditLogger.record("ITEM", 42L, AuditAction.UPDATE, oldValues, newValues);

    // Assert
    verify(repository).save(captor.capture());
    AuditLog saved = captor.getValue();
    assertNull(saved.id());
    assertEquals("ITEM", saved.entityType());
    assertEquals(42L, saved.entityId());
    assertEquals(AuditAction.UPDATE, saved.action());
    assertEquals(oldValues, saved.oldValues());
    assertEquals(newValues, saved.newValues());
    assertEquals("system", saved.userName());
    assertNotNull(saved.timestamp());
  }

  @Test
  @DisplayName("SaveAuditLogWithNullOldValues_WhenCreateAction")
  void shouldSaveAuditLogWithNullOldValuesWhenCreateAction() {
    // Arrange
    Map<String, Object> newValues = Map.of("id", 1L);
    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    when(repository.save(any(AuditLog.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    auditLogger.record("CUSTOMER", 1L, AuditAction.CREATE, null, newValues);

    // Assert
    verify(repository).save(captor.capture());
    assertNull(captor.getValue().oldValues());
    assertNotNull(captor.getValue().newValues());
  }

  @Test
  @DisplayName("SaveAuditLogWithNullNewValues_WhenDeleteAction")
  void shouldSaveAuditLogWithNullNewValuesWhenDeleteAction() {
    // Arrange
    Map<String, Object> oldValues = Map.of("id", 1L);
    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    when(repository.save(any(AuditLog.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    auditLogger.record("CUSTOMER", 1L, AuditAction.DELETE, oldValues, null);

    // Assert
    verify(repository).save(captor.capture());
    assertNotNull(captor.getValue().oldValues());
    assertNull(captor.getValue().newValues());
  }
}
