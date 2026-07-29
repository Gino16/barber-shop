package org.barbershop.employee.application;

import org.barbershop.employee.domain.EmployeeRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmployeeFilterQuery Tests")
class EmployeeFilterQueryTest {

  @Test
  @DisplayName("ReturnDefaultValues_WhenWithDefaultsCalledWithZeroPage")
  void shouldReturnDefaultValuesWhenWithDefaultsCalledWithZeroPage() {
    // Arrange
    EmployeeFilterQuery query = new EmployeeFilterQuery(null, null, null, 0, 0);

    // Act
    EmployeeFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(1, result.page());
    assertEquals(10, result.pageSize());
    assertNull(result.search());
    assertNull(result.role());
    assertNull(result.active());
  }

  @Test
  @DisplayName("ReturnProvidedValues_WhenWithDefaultsCalledWithValidData")
  void shouldReturnProvidedValuesWhenWithDefaultsCalledWithValidData() {
    // Arrange
    EmployeeFilterQuery query = new EmployeeFilterQuery("Pedro", EmployeeRole.BARBER, true, 2, 20);

    // Act
    EmployeeFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(2, result.page());
    assertEquals(20, result.pageSize());
    assertEquals("Pedro", result.search());
    assertEquals(EmployeeRole.BARBER, result.role());
    assertTrue(result.active());
  }

  @Test
  @DisplayName("ReturnDefaultPageSize_WhenPageSizeExceedsMax")
  void shouldReturnDefaultPageSizeWhenPageSizeExceedsMax() {
    // Arrange
    EmployeeFilterQuery query = new EmployeeFilterQuery(null, null, null, 1, 200);

    // Act
    EmployeeFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(10, result.pageSize());
  }

  @Test
  @DisplayName("TrimSearch_WhenSearchHasWhitespace")
  void shouldTrimSearchWhenSearchHasWhitespace() {
    // Arrange
    EmployeeFilterQuery query = new EmployeeFilterQuery("  Pedro  ", null, null, 1, 10);

    // Act
    EmployeeFilterQuery result = query.withDefaults();

    // Assert
    assertEquals("Pedro", result.search());
  }

  @Test
  @DisplayName("ReturnCorrectOffset_WhenOffsetCalled")
  void shouldReturnCorrectOffsetWhenOffsetCalled() {
    // Arrange
    EmployeeFilterQuery query = new EmployeeFilterQuery(null, null, null, 3, 10);

    // Act
    int offset = query.offset();

    // Assert
    assertEquals(20, offset);
  }

  @Test
  @DisplayName("ReturnZeroOffset_WhenFirstPage")
  void shouldReturnZeroOffsetWhenFirstPage() {
    // Arrange
    EmployeeFilterQuery query = new EmployeeFilterQuery(null, null, null, 1, 10);

    // Act
    int offset = query.offset();

    // Assert
    assertEquals(0, offset);
  }
}
