package org.barbershop.item.adapter.in.rest;

import jakarta.ws.rs.core.Response;
import org.barbershop.api.model.ItemRequest;
import org.barbershop.item.application.ItemFilterQuery;
import org.barbershop.item.application.PagedResponse;
import org.barbershop.item.application.port.in.ItemUseCase;
import org.barbershop.item.domain.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemRestAdapter Tests")
class ItemRestAdapterTest {

  @Mock
  private ItemUseCase useCase;

  @InjectMocks
  private ItemRestAdapter adapter;

  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private Item sampleItem() {
    return new Item(1L, "Corte", "Corte de cabello", Item.Category.SERVICE, true, NOW);
  }

  private ItemRequest sampleRequest() {
    ItemRequest req = new ItemRequest();
    req.setName("Corte");
    req.setDescription("Corte de cabello");
    req.setCategory(ItemRequest.CategoryEnum.SERVICE);
    req.setActive(true);
    return req;
  }

  @Test
  @DisplayName("Return200WithItems_WhenListCalled")
  void shouldReturn200WithItemsWhenListCalled() {
    // Arrange
    PagedResponse<Item> paged = new PagedResponse<>(List.of(sampleItem()), 1, 10, 1L);
    when(useCase.list(any(ItemFilterQuery.class))).thenReturn(paged);

    // Act
    Response response = adapter.listItems(1, 10, null, null, null, null, null);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    verify(useCase).list(any(ItemFilterQuery.class));
  }

  @Test
  @DisplayName("Return200WithFilteredItems_WhenCategoryFilterApplied")
  void shouldReturn200WithFilteredItemsWhenCategoryFilterApplied() {
    // Arrange
    PagedResponse<Item> paged = new PagedResponse<>(List.of(sampleItem()), 1, 10, 1L);
    when(useCase.list(any(ItemFilterQuery.class))).thenReturn(paged);

    // Act
    Response response = adapter.listItems(1, 10, null, "SERVICE", null, null, null);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return200WithItem_WhenGetItemCalled")
  void shouldReturn200WithItemWhenGetItemCalled() {
    // Arrange
    when(useCase.findById(1L)).thenReturn(Optional.of(sampleItem()));

    // Act
    Response response = adapter.getItem(1L);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
  }

  @Test
  @DisplayName("Return404_WhenGetItemCalledWithNonExistentId")
  void shouldReturn404WhenGetItemCalledWithNonExistentId() {
    // Arrange
    when(useCase.findById(999L)).thenReturn(Optional.empty());

    // Act
    Response response = adapter.getItem(999L);

    // Assert
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return201WithItem_WhenCreateCalled")
  void shouldReturn201WithItemWhenCreateCalled() {
    // Arrange
    when(useCase.create(any())).thenReturn(sampleItem());

    // Act
    Response response = adapter.createItem(sampleRequest());

    // Assert
    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    verify(useCase).create(any());
  }

  @Test
  @DisplayName("Return200WithUpdatedItem_WhenUpdateCalledWithExistentId")
  void shouldReturn200WithUpdatedItemWhenUpdateCalledWithExistentId() {
    // Arrange
    when(useCase.update(eq(1L), any())).thenReturn(Optional.of(sampleItem()));

    // Act
    Response response = adapter.updateItem(1L, sampleRequest());

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return404_WhenUpdateCalledWithNonExistentId")
  void shouldReturn404WhenUpdateCalledWithNonExistentId() {
    // Arrange
    when(useCase.update(eq(999L), any())).thenReturn(Optional.empty());

    // Act
    Response response = adapter.updateItem(999L, sampleRequest());

    // Assert
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("UseDefaultPagination_WhenNullPageParamsProvided")
  void shouldUseDefaultPaginationWhenNullPageParamsProvided() {
    // Arrange
    PagedResponse<Item> paged = new PagedResponse<>(List.of(), 1, 10, 0L);
    when(useCase.list(any(ItemFilterQuery.class))).thenReturn(paged);

    // Act
    Response response = adapter.listItems(null, null, null, null, null, null, null);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }
}
