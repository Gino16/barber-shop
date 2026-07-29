package org.barbershop.appointment.application;

import org.barbershop.appointment.domain.AppointmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AppointmentFilterQuery Tests")
class AppointmentFilterQueryTest {

  @Test
  @DisplayName("ReturnDefaultValues_WhenWithDefaultsCalledWithZeroPage")
  void shouldReturnDefaultValuesWhenWithDefaultsCalledWithZeroPage() {
    // Arrange
    AppointmentFilterQuery query = new AppointmentFilterQuery(null, null, null, null, null, 0, 0);

    // Act
    AppointmentFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(1, result.page());
    assertEquals(10, result.pageSize());
    assertNull(result.startDate());
    assertNull(result.endDate());
    assertNull(result.employeeId());
    assertNull(result.customerId());
    assertNull(result.status());
  }

  @Test
  @DisplayName("ReturnProvidedValues_WhenWithDefaultsCalledWithValidData")
  void shouldReturnProvidedValuesWhenWithDefaultsCalledWithValidData() {
    // Arrange
    LocalDate start = LocalDate.of(2024, 1, 1);
    LocalDate end = LocalDate.of(2024, 1, 31);
    AppointmentFilterQuery query = new AppointmentFilterQuery(start, end, 1L, 2L, AppointmentStatus.SCHEDULED, 2, 20);

    // Act
    AppointmentFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(2, result.page());
    assertEquals(20, result.pageSize());
    assertEquals(start, result.startDate());
    assertEquals(end, result.endDate());
    assertEquals(1L, result.employeeId());
    assertEquals(2L, result.customerId());
    assertEquals(AppointmentStatus.SCHEDULED, result.status());
  }

  @Test
  @DisplayName("ReturnDefaultPageSize_WhenPageSizeExceedsMax")
  void shouldReturnDefaultPageSizeWhenPageSizeExceedsMax() {
    // Arrange
    AppointmentFilterQuery query = new AppointmentFilterQuery(null, null, null, null, null, 1, 200);

    // Act
    AppointmentFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(10, result.pageSize());
  }

  @Test
  @DisplayName("ReturnCorrectOffset_WhenOffsetCalled")
  void shouldReturnCorrectOffsetWhenOffsetCalled() {
    // Arrange
    AppointmentFilterQuery query = new AppointmentFilterQuery(null, null, null, null, null, 3, 10);

    // Act
    int offset = query.offset();

    // Assert
    assertEquals(20, offset);
  }

  @Test
  @DisplayName("ReturnZeroOffset_WhenFirstPage")
  void shouldReturnZeroOffsetWhenFirstPage() {
    // Arrange
    AppointmentFilterQuery query = new AppointmentFilterQuery(null, null, null, null, null, 1, 10);

    // Act
    int offset = query.offset();

    // Assert
    assertEquals(0, offset);
  }

  @Test
  @DisplayName("ReturnPage1_WhenPageIsNegative")
  void shouldReturnPage1WhenPageIsNegative() {
    // Arrange
    AppointmentFilterQuery query = new AppointmentFilterQuery(null, null, null, null, null, -5, 10);

    // Act
    AppointmentFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(1, result.page());
  }
}
