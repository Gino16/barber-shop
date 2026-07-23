package org.barbershop.item.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PagedResponse Tests")
class PagedResponseTest {

  @Test
  @DisplayName("ReturnCorrectMetadata_WhenPagedResponseCreated")
  void shouldReturnCorrectMetadataWhenPagedResponseCreated() {
    // Arrange
    List<String> data = List.of("Item 1", "Item 2", "Item 3");
    int page = 1;
    int pageSize = 10;
    long total = 25;

    // Act
    PagedResponse<String> response = new PagedResponse<>(data, page, pageSize, total);

    // Assert
    assertEquals(data, response.data());
    assertEquals(1, response.page());
    assertEquals(10, response.pageSize());
    assertEquals(25L, response.total());
    assertEquals(3, response.totalPages());
    assertTrue(response.hasNextPage());
  }

  @Test
  @DisplayName("ReturnNoNextPage_WhenOnLastPage")
  void shouldReturnNoNextPageWhenOnLastPage() {
    // Arrange
    List<String> data = List.of("Item 1", "Item 2");
    int page = 3;
    int pageSize = 10;
    long total = 25;

    // Act
    PagedResponse<String> response = new PagedResponse<>(data, page, pageSize, total);

    // Assert
    assertFalse(response.hasNextPage());
  }

  @Test
  @DisplayName("ReturnOnePage_WhenTotalLessThanPageSize")
  void shouldReturnOnePageWhenTotalLessThanPageSize() {
    // Arrange
    List<String> data = List.of("Item 1", "Item 2");
    int page = 1;
    int pageSize = 10;
    long total = 2;

    // Act
    PagedResponse<String> response = new PagedResponse<>(data, page, pageSize, total);

    // Assert
    assertEquals(1, response.totalPages());
    assertFalse(response.hasNextPage());
  }

  @Test
  @DisplayName("ReturnCorrectTotalPages_WhenExactMultiple")
  void shouldReturnCorrectTotalPagesWhenExactMultiple() {
    // Arrange
    List<String> data = List.of();
    int page = 1;
    int pageSize = 10;
    long total = 50;

    // Act
    PagedResponse<String> response = new PagedResponse<>(data, page, pageSize, total);

    // Assert
    assertEquals(5, response.totalPages());
  }

  @Test
  @DisplayName("ReturnHasNextPage_WhenNotOnLastPage")
  void shouldReturnHasNextPageWhenNotOnLastPage() {
    // Arrange
    List<String> data = List.of();
    int page = 2;
    int pageSize = 10;
    long total = 50;

    // Act
    PagedResponse<String> response = new PagedResponse<>(data, page, pageSize, total);

    // Assert
    assertTrue(response.hasNextPage());
  }
}
