package org.barbershop.item.application;

import org.barbershop.item.domain.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemFilterQuery Tests")
class ItemFilterQueryTest {

  @Test
  @DisplayName("ReturnDefaultValues_WhenWithDefaultsCalled")
  void shouldReturnDefaultValuesWhenWithDefaultsCalled() {
    // Arrange
    ItemFilterQuery query = new ItemFilterQuery(null, null, null, 0, 0, null, null);

    // Act
    ItemFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(1, result.page());
    assertEquals(10, result.pageSize());
    assertEquals("id", result.sortBy());
    assertEquals("asc", result.sortDirection());
    assertNull(result.search());
    assertNull(result.category());
    assertNull(result.active());
  }

  @Test
  @DisplayName("ReturnValidValues_WhenWithDefaultsCalledWithValidData")
  void shouldReturnValidValuesWhenWithDefaultsCalledWithValidData() {
    // Arrange
    ItemFilterQuery query = new ItemFilterQuery("corte", Item.Category.SERVICE, true, 2, 20, "name", "desc");

    // Act
    ItemFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(2, result.page());
    assertEquals(20, result.pageSize());
    assertEquals("name", result.sortBy());
    assertEquals("desc", result.sortDirection());
    assertEquals("corte", result.search());
    assertEquals(Item.Category.SERVICE, result.category());
    assertTrue(result.active());
  }

  @Test
  @DisplayName("ReturnMaxPageSize_WhenPageSizeExceedsMax")
  void shouldReturnMaxPageSizeWhenPageSizeExceedsMax() {
    // Arrange
    ItemFilterQuery query = new ItemFilterQuery(null, null, null, 1, 200, "id", "asc");

    // Act
    ItemFilterQuery result = query.withDefaults();

    // Assert
    assertEquals(10, result.pageSize());
  }

  @Test
  @DisplayName("ReturnCorrectOffset_WhenOffsetCalled")
  void shouldReturnCorrectOffsetWhenOffsetCalled() {
    // Arrange
    ItemFilterQuery query = new ItemFilterQuery(null, null, null, 3, 10, "id", "asc");

    // Act
    int offset = query.offset();

    // Assert
    assertEquals(20, offset);
  }

  @Test
  @DisplayName("ReturnAscAsDefault_WhenSortDirectionIsInvalid")
  void shouldReturnAscAsDefaultWhenSortDirectionIsInvalid() {
    // Arrange
    ItemFilterQuery query = new ItemFilterQuery(null, null, null, 1, 10, "id", "invalid");

    // Act
    ItemFilterQuery result = query.withDefaults();

    // Assert
    assertEquals("asc", result.sortDirection());
  }

  @Test
  @DisplayName("TrimSearchString_WhenSearchHasWhitespace")
  void shouldTrimSearchStringWhenSearchHasWhitespace() {
    // Arrange
    ItemFilterQuery query = new ItemFilterQuery("  corte  ", null, null, 1, 10, "id", "asc");

    // Act
    ItemFilterQuery result = query.withDefaults();

    // Assert
    assertEquals("corte", result.search());
  }
}
