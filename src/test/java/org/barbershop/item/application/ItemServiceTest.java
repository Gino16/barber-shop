package org.barbershop.item.application;

import org.barbershop.item.application.port.out.ItemRepositoryPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemService Tests")
class ItemServiceTest {

  @Mock
  private ItemRepositoryPort repositoryPort;

  @InjectMocks
  private ItemService itemService;

  @Test
  @DisplayName("ReturnPaginatedItems_WhenListCalled")
  void shouldReturnPaginatedItemsWhenListCalled() {
    // Arrange
    Item item1 = new Item(1L, "Corte", "Corte de cabello", Item.Category.SERVICE, true, OffsetDateTime.now(ZoneOffset.UTC));
    Item item2 = new Item(2L, "Afeitado", "Afeitado completo", Item.Category.SERVICE, true, OffsetDateTime.now(ZoneOffset.UTC));
    
    ItemFilterQuery query = new ItemFilterQuery(null, null, null, 1, 10, "id", "asc");
    
    when(repositoryPort.find(any(ItemFilterQuery.class))).thenReturn(List.of(item1, item2));
    when(repositoryPort.count(any(ItemFilterQuery.class))).thenReturn(2L);

    // Act
    PagedResponse<Item> result = itemService.list(query);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.data().size());
    assertEquals(1, result.page());
    assertEquals(10, result.pageSize());
    assertEquals(2L, result.total());
    assertEquals(1, result.totalPages());
    assertFalse(result.hasNextPage());
    verify(repositoryPort).find(any(ItemFilterQuery.class));
    verify(repositoryPort).count(any(ItemFilterQuery.class));
  }

  @Test
  @DisplayName("ReturnItem_WhenFindByIdCalled")
  void shouldReturnItemWhenFindByIdCalled() {
    // Arrange
    Long itemId = 1L;
    Item expectedItem = new Item(itemId, "Corte", "Corte de cabello", Item.Category.SERVICE, true, OffsetDateTime.now(ZoneOffset.UTC));
    when(repositoryPort.findById(itemId)).thenReturn(Optional.of(expectedItem));

    // Act
    Optional<Item> result = itemService.findById(itemId);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(expectedItem, result.get());
    verify(repositoryPort).findById(itemId);
  }

  @Test
  @DisplayName("ReturnEmptyOptional_WhenFindByIdCalledWithNonExistentId")
  void shouldReturnEmptyOptionalWhenFindByIdCalledWithNonExistentId() {
    // Arrange
    Long itemId = 999L;
    when(repositoryPort.findById(itemId)).thenReturn(Optional.empty());

    // Act
    Optional<Item> result = itemService.findById(itemId);

    // Assert
    assertTrue(result.isEmpty());
    verify(repositoryPort).findById(itemId);
  }

  @Test
  @DisplayName("ReturnCreatedItem_WhenCreateCalled")
  void shouldReturnCreatedItemWhenCreateCalled() {
    // Arrange
    ItemCommand command = new ItemCommand("Corte", "Corte moderno", Item.Category.SERVICE, true);
    Item savedItem = new Item(1L, "Corte", "Corte moderno", Item.Category.SERVICE, true, OffsetDateTime.now(ZoneOffset.UTC));
    when(repositoryPort.save(any(Item.class))).thenReturn(savedItem);

    // Act
    Item result = itemService.create(command);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals("Corte", result.name());
    assertEquals(Item.Category.SERVICE, result.category());
    assertTrue(result.active());
    verify(repositoryPort).save(any(Item.class));
  }

  @Test
  @DisplayName("ReturnUpdatedItem_WhenUpdateCalledWithExistentId")
  void shouldReturnUpdatedItemWhenUpdateCalledWithExistentId() {
    // Arrange
    Long itemId = 1L;
    ItemCommand command = new ItemCommand("Corte Premium", "Corte con diseño", Item.Category.SERVICE, true);
    Item existingItem = new Item(itemId, "Corte", "Corte de cabello", Item.Category.SERVICE, true, OffsetDateTime.now(ZoneOffset.UTC));
    Item updatedItem = new Item(itemId, "Corte Premium", "Corte con diseño", Item.Category.SERVICE, true, existingItem.createdAt());

    when(repositoryPort.findById(itemId)).thenReturn(Optional.of(existingItem));
    when(repositoryPort.save(any(Item.class))).thenReturn(updatedItem);

    // Act
    Optional<Item> result = itemService.update(itemId, command);

    // Assert
    assertTrue(result.isPresent());
    assertEquals("Corte Premium", result.get().name());
    verify(repositoryPort).findById(itemId);
    verify(repositoryPort).save(any(Item.class));
  }

  @Test
  @DisplayName("ReturnEmptyOptional_WhenUpdateCalledWithNonExistentId")
  void shouldReturnEmptyOptionalWhenUpdateCalledWithNonExistentId() {
    // Arrange
    Long itemId = 999L;
    ItemCommand command = new ItemCommand("Corte", "Corte de cabello", Item.Category.SERVICE, true);
    when(repositoryPort.findById(itemId)).thenReturn(Optional.empty());

    // Act
    Optional<Item> result = itemService.update(itemId, command);

    // Assert
    assertTrue(result.isEmpty());
    verify(repositoryPort).findById(itemId);
    verify(repositoryPort, never()).save(any(Item.class));
  }

  @Test
  @DisplayName("ReturnActiveItem_WhenCreateCalledWithoutActiveParam")
  void shouldReturnActiveItemWhenCreateCalledWithoutActiveParam() {
    // Arrange
    ItemCommand command = new ItemCommand("Corte", "Corte moderno", Item.Category.SERVICE, null);
    Item savedItem = new Item(1L, "Corte", "Corte moderno", Item.Category.SERVICE, true, OffsetDateTime.now(ZoneOffset.UTC));
    when(repositoryPort.save(any(Item.class))).thenReturn(savedItem);

    // Act
    Item result = itemService.create(command);

    // Assert
    assertTrue(result.active());
    verify(repositoryPort).save(any(Item.class));
  }
}
