package org.barbershop.sale.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.barbershop.sale.domain.PaymentMethod;
import org.barbershop.sale.domain.Sale;
import org.barbershop.sale.domain.SaleItem;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SalePersistenceAdapter Tests")
class SalePersistenceAdapterTest {

  private static final OffsetDateTime SOLD_AT = OffsetDateTime.of(
      2024, 7, 15, 10, 30, 0, 0, ZoneOffset.UTC);

  @Mock
  private SalePanacheRepository repository;

  @Mock
  private EntityManager entityManager;

  @InjectMocks
  private SalePersistenceAdapter adapter;

  @Test
  @DisplayName("ReturnSaleList_WhenFindCalled")
  @SuppressWarnings("unchecked")
  void shouldReturnSaleListWhenFindCalled() {
    // Arrange
    PanacheQuery<SaleJpaEntity> query = mock(PanacheQuery.class);
    when(repository.find("ORDER BY soldAt DESC")).thenReturn(query);
    when(query.range(0, 9)).thenReturn(query);
    when(query.stream()).thenReturn(List.<SaleJpaEntity>of().stream());

    // Act
    List<Sale> result = adapter.find(0, 10);

    // Assert
    assertTrue(result.isEmpty());
    verify(query).range(0, 9);
  }

  @Test
  @DisplayName("ReturnSale_WhenFindByIdCalledWithExistingId")
  @SuppressWarnings("unchecked")
  void shouldReturnSaleWhenFindByIdCalledWithExistingId() {
    // Arrange
    SaleJpaEntity entity = saleEntity(1L);
    PanacheQuery<SaleJpaEntity> saleQuery = mock(PanacheQuery.class);
    TypedQuery<SaleItem> itemQuery = mock(TypedQuery.class);
    when(repository.find("id", 1L)).thenReturn(saleQuery);
    when(saleQuery.firstResultOptional()).thenReturn(Optional.of(entity));
    when(entityManager.createQuery(anyString(), eq(SaleItem.class))).thenReturn(itemQuery);
    when(itemQuery.setParameter(1, 1L)).thenReturn(itemQuery);
    when(itemQuery.getResultList()).thenReturn(List.of());

    // Act
    Optional<Sale> result = adapter.findById(1L);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(1L, result.get().id());
    assertEquals(250.0, result.get().totalAmount());
  }

  @Test
  @DisplayName("ReturnEmpty_WhenFindByIdCalledWithNonExistingId")
  @SuppressWarnings("unchecked")
  void shouldReturnEmptyWhenFindByIdCalledWithNonExistingId() {
    // Arrange
    PanacheQuery<SaleJpaEntity> query = mock(PanacheQuery.class);
    when(repository.find("id", 99L)).thenReturn(query);
    when(query.firstResultOptional()).thenReturn(Optional.empty());

    // Act
    Optional<Sale> result = adapter.findById(99L);

    // Assert
    assertTrue(result.isEmpty());
    verifyNoInteractions(entityManager);
  }

  @Test
  @DisplayName("PersistSale_WhenSaveCalledWithNewSale")
  void shouldPersistSaleWhenSaveCalledWithNewSale() {
    // Arrange
    Sale sale = sale(null, List.of());

    // Act
    Sale result = adapter.save(sale);

    // Assert
    assertEquals(sale.customerId(), result.customerId());
    assertEquals(sale.totalAmount(), result.totalAmount());
    verify(repository).persist(any(SaleJpaEntity.class));
  }

  @Test
  @DisplayName("ReplaceSaleItems_WhenSaveCalledWithExistingSale")
  @SuppressWarnings("unchecked")
  void shouldReplaceSaleItemsWhenSaveCalledWithExistingSale() {
    // Arrange
    SaleJpaEntity entity = saleEntity(1L);
    PanacheQuery<SaleJpaEntity> saleQuery = mock(PanacheQuery.class);
    Query deleteQuery = mock(Query.class);
    SaleItem item = new SaleItem(null, 1L, 7L, 2, 25.0, 50.0);
    when(repository.find("id", 1L)).thenReturn(saleQuery);
    when(saleQuery.firstResult()).thenReturn(entity);
    when(entityManager.createQuery(startsWith("DELETE FROM SaleItemJpaEntity"))).thenReturn(deleteQuery);
    when(deleteQuery.setParameter(1, 1L)).thenReturn(deleteQuery);

    // Act
    Sale result = adapter.save(sale(1L, List.of(item)));

    // Assert
    assertEquals(1L, result.id());
    verify(deleteQuery).executeUpdate();
    verify(entityManager).persist(argThat(savedItem ->
        savedItem instanceof SaleItemJpaEntity saleItem
            && saleItem.itemId.equals(7L)
            && saleItem.quantity.equals(2)
            && saleItem.subtotalAmount.equals(50.0)));
  }

  private SaleJpaEntity saleEntity(Long id) {
    SaleJpaEntity entity = new SaleJpaEntity();
    entity.id = id;
    entity.customerId = 10L;
    entity.employeeId = 20L;
    entity.paymentMethod = PaymentMethod.CARD;
    entity.totalAmount = 250.0;
    entity.discount = 0.0;
    entity.notes = "Nota";
    entity.soldAt = SOLD_AT.toLocalDateTime();
    return entity;
  }

  private Sale sale(Long id, List<SaleItem> items) {
    return new Sale(id, 10L, 20L, PaymentMethod.CARD, 250.0, 0.0, "Nota", items, SOLD_AT);
  }
}
