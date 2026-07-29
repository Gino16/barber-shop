package org.barbershop.customer.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomerFilterQuery Tests")
class CustomerFilterQueryTest {

  @Test
  @DisplayName("ReturnDefaultValues_WhenWithDefaultsCalledWithZeroPage")
  void shouldReturnDefaultValuesWhenWithDefaultsCalledWithZeroPage() {
    // Arrange
    CustomerFilterQuery query = new CustomerFilterQuery(null, 0, 0);

    // Act
    CustomerFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(1, result.page());
    assertEquals(10, result.pageSize());
    assertNull(result.search());
  }

  @Test
  @DisplayName("ReturnProvidedValues_WhenWithDefaultsCalledWithValidData")
  void shouldReturnProvidedValuesWhenWithDefaultsCalledWithValidData() {
    // Arrange
    CustomerFilterQuery query = new CustomerFilterQuery("Juan", 2, 20);

    // Act
    CustomerFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(2, result.page());
    assertEquals(20, result.pageSize());
    assertEquals("Juan", result.search());
  }

  @Test
  @DisplayName("ReturnDefaultPageSize_WhenPageSizeExceedsMax")
  void shouldReturnDefaultPageSizeWhenPageSizeExceedsMax() {
    // Arrange
    CustomerFilterQuery query = new CustomerFilterQuery(null, 1, 200);

    // Act
    CustomerFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(10, result.pageSize());
  }

  @Test
  @DisplayName("TrimSearch_WhenSearchHasWhitespace")
  void shouldTrimSearchWhenSearchHasWhitespace() {
    // Arrange
    CustomerFilterQuery query = new CustomerFilterQuery("  Juan  ", 1, 10);

    // Act
    CustomerFilterQuery result = query.withDefaults();

    // Assert
    assertEquals("Juan", result.search());
  }

  @Test
  @DisplayName("ReturnCorrectOffset_WhenOffsetCalled")
  void shouldReturnCorrectOffsetWhenOffsetCalled() {
    // Arrange
    CustomerFilterQuery query = new CustomerFilterQuery(null, 3, 10);

    // Act
    int offset = query.offset();

    // Assert
    assertEquals(20, offset);
  }

  @Test
  @DisplayName("ReturnZeroOffset_WhenFirstPage")
  void shouldReturnZeroOffsetWhenFirstPage() {
    // Arrange
    CustomerFilterQuery query = new CustomerFilterQuery(null, 1, 10);

    // Act
    int offset = query.offset();

    // Assert
    assertEquals(0, offset);
  }
}
