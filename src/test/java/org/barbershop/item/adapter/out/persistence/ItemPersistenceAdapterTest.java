package org.barbershop.item.adapter.out.persistence;

import org.barbershop.item.domain.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemPersistenceAdapter Tests")
class ItemPersistenceAdapterTest {

  @Mock
  private ItemPanacheRepository panache;

  @InjectMocks
  private ItemPersistenceAdapter adapter;

  @Test
  @DisplayName("ReturnAllItems_WhenFindAllCalled")
  void shouldReturnAllItemsWhenFindAllCalled() {
    // Arrange
    ItemJpaEntity entity = new ItemJpaEntity();
    entity.id = 1L;
    entity.name = "Corte";
    entity.category = Item.Category.SERVICE;
    entity.price = BigDecimal.TEN;
    entity.active = true;
    entity.createdAt = LocalDateTime.now();

    when(panache.listAll()).thenReturn(List.of(entity));

    // Act
    List<Item> result = adapter.findAll();

    // Assert
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Corte", result.get(0).name());
    verify(panache).listAll();
  }

  @Test
  @DisplayName("ReturnEmptyList_WhenFindAllCalledOnEmptyDB")
  void shouldReturnEmptyListWhenFindAllCalledOnEmptyDB() {
    // Arrange
    when(panache.listAll()).thenReturn(List.of());

    // Act
    List<Item> result = adapter.findAll();

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
    verify(panache).listAll();
  }

  @Test
  @DisplayName("ConvertJpaEntityToDomain_WhenToDomainCalled")
  void shouldConvertJpaEntityToDomain() {
    // Arrange
    ItemJpaEntity entity = new ItemJpaEntity();
    entity.id = 1L;
    entity.name = "Corte";
    entity.description = "Corte moderno";
    entity.category = Item.Category.SERVICE;
    entity.price = BigDecimal.TEN;
    entity.active = true;
    entity.createdAt = LocalDateTime.now();

    // Act
    Item domain = entity.toDomain();

    // Assert
    assertNotNull(domain);
    assertEquals(1L, domain.id());
    assertEquals("Corte", domain.name());
    assertEquals("Corte moderno", domain.description());
    assertEquals(Item.Category.SERVICE, domain.category());
    assertTrue(domain.active());
  }

  @Test
  @DisplayName("ConvertDomainToJpaEntity_WhenFromDomainCalled")
  void shouldConvertDomainToJpaEntity() {
    // Arrange
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    Item domain = new Item(1L, "Corte", "Corte moderno", Item.Category.SERVICE, BigDecimal.TEN, true, now);

    // Act
    ItemJpaEntity entity = ItemJpaEntity.fromDomain(domain);

    // Assert
    assertNotNull(entity);
    assertEquals(1L, entity.id);
    assertEquals("Corte", entity.name);
    assertEquals("Corte moderno", entity.description);
    assertEquals(Item.Category.SERVICE, entity.category);
    assertTrue(entity.active);
  }
}
