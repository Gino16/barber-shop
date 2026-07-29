package org.barbershop.audit.application;

import org.barbershop.audit.domain.AuditAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuditFilterQuery Tests")
class AuditFilterQueryTest {

  @Test
  @DisplayName("ReturnDefaultValues_WhenWithDefaultsCalledWithZeroPage")
  void shouldReturnDefaultValuesWhenWithDefaultsCalledWithZeroPage() {
    // Arrange
    AuditFilterQuery query = new AuditFilterQuery(null, null, 0, 0);

    // Act
    AuditFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(1, result.page());
    assertEquals(10, result.pageSize());
    assertNull(result.entityType());
    assertNull(result.action());
  }

  @Test
  @DisplayName("ReturnProvidedValues_WhenWithDefaultsCalledWithValidData")
  void shouldReturnProvidedValuesWhenWithDefaultsCalledWithValidData() {
    // Arrange
    AuditFilterQuery query = new AuditFilterQuery("ITEM", AuditAction.CREATE, 2, 20);

    // Act
    AuditFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(2, result.page());
    assertEquals(20, result.pageSize());
    assertEquals("ITEM", result.entityType());
    assertEquals(AuditAction.CREATE, result.action());
  }

  @Test
  @DisplayName("ReturnDefaultPageSize_WhenPageSizeExceedsMax")
  void shouldReturnDefaultPageSizeWhenPageSizeExceedsMax() {
    // Arrange
    AuditFilterQuery query = new AuditFilterQuery(null, null, 1, 200);

    // Act
    AuditFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(10, result.pageSize());
  }

  @Test
  @DisplayName("ReturnCorrectOffset_WhenOffsetCalled")
  void shouldReturnCorrectOffsetWhenOffsetCalled() {
    // Arrange
    AuditFilterQuery query = new AuditFilterQuery(null, null, 3, 10);

    // Act
    int offset = query.offset();

    // Assert
    assertEquals(20, offset);
  }

  @Test
  @DisplayName("ReturnZeroOffset_WhenFirstPage")
  void shouldReturnZeroOffsetWhenFirstPage() {
    // Arrange
    AuditFilterQuery query = new AuditFilterQuery(null, null, 1, 10);

    // Act
    int offset = query.offset();

    // Assert
    assertEquals(0, offset);
  }
}
