package org.barbershop.customer.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.barbershop.customer.domain.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerPersistenceAdapter Tests")
class CustomerPersistenceAdapterTest {

  @Mock
  private CustomerPanacheRepository repository;

  @InjectMocks
  private CustomerPersistenceAdapter adapter;

  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private CustomerJpaEntity sampleEntity(Long id) {
    CustomerJpaEntity entity = new CustomerJpaEntity();
    entity.id = id;
    entity.name = "Juan García";
    entity.phone = "555-1234";
    entity.email = "juan@mail.com";
    entity.address = "Calle 1";
    entity.createdAt = LocalDateTime.now();
    return entity;
  }

  @Test
  @DisplayName("ReturnCustomer_WhenFindByIdCalledWithExistentId")
  @SuppressWarnings("unchecked")
  void shouldReturnCustomerWhenFindByIdCalledWithExistentId() {
    // Arrange
    CustomerJpaEntity entity = sampleEntity(1L);
    PanacheQuery<CustomerJpaEntity> mockQuery = mock(PanacheQuery.class);
    when(repository.find("id", 1L)).thenReturn(mockQuery);
    when(mockQuery.firstResultOptional()).thenReturn(Optional.of(entity));

    // Act
    Optional<Customer> result = adapter.findById(1L);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(1L, result.get().id());
    assertEquals("Juan García", result.get().name());
  }

  @Test
  @DisplayName("ReturnEmpty_WhenFindByIdCalledWithNonExistentId")
  @SuppressWarnings("unchecked")
  void shouldReturnEmptyWhenFindByIdCalledWithNonExistentId() {
    // Arrange
    PanacheQuery<CustomerJpaEntity> mockQuery = mock(PanacheQuery.class);
    when(repository.find("id", 999L)).thenReturn(mockQuery);
    when(mockQuery.firstResultOptional()).thenReturn(Optional.empty());

    // Act
    Optional<Customer> result = adapter.findById(999L);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("PersistEntity_WhenSaveCalledWithNewCustomer")
  void shouldPersistEntityWhenSaveCalledWithNewCustomer() {
    // Arrange
    Customer newCustomer = new Customer(null, "Juan García", "555-1234", "juan@mail.com", "Calle 1", NOW);
    // Panache void methods are no-ops by default in mocks — no stubbing needed

    // Act
    Customer result = adapter.save(newCustomer);

    // Assert
    assertNotNull(result);
    assertEquals("Juan García", result.name());
    assertEquals("555-1234", result.phone());
  }

  @Test
  @DisplayName("UpdateEntity_WhenSaveCalledWithExistingCustomer")
  @SuppressWarnings("unchecked")
  void shouldUpdateEntityWhenSaveCalledWithExistingCustomer() {
    // Arrange
    Customer existingCustomer = new Customer(1L, "Juan Actualizado", "555-9999", "nuevo@mail.com", "Calle 2", NOW);
    CustomerJpaEntity existingEntity = sampleEntity(1L);
    PanacheQuery<CustomerJpaEntity> mockQuery = mock(PanacheQuery.class);
    when(repository.find("id", 1L)).thenReturn(mockQuery);
    when(mockQuery.firstResult()).thenReturn(existingEntity);

    // Act
    Customer result = adapter.save(existingCustomer);

    // Assert
    assertNotNull(result);
    assertEquals("Juan Actualizado", result.name());
  }

  @Test
  @DisplayName("CallDeleteById_WhenDeleteCalled")
  void shouldCallDeleteByIdWhenDeleteCalled() {
    // Arrange
    when(repository.deleteById(1L)).thenReturn(true);

    // Act
    adapter.delete(1L);

    // Assert
    verify(repository).deleteById(1L);
  }

  @Test
  @DisplayName("ConvertJpaEntityToDomain_WhenToDomainCalled")
  void shouldConvertJpaEntityToDomainWhenToDomainCalled() {
    // Arrange
    CustomerJpaEntity entity = sampleEntity(1L);

    // Act
    Customer domain = entity.toDomain();

    // Assert
    assertEquals(1L, domain.id());
    assertEquals("Juan García", domain.name());
    assertEquals("555-1234", domain.phone());
    assertEquals("juan@mail.com", domain.email());
    assertEquals("Calle 1", domain.address());
  }

  @Test
  @DisplayName("ConvertDomainToJpaEntity_WhenFromDomainCalled")
  void shouldConvertDomainToJpaEntityWhenFromDomainCalled() {
    // Arrange
    Customer domain = new Customer(1L, "Juan García", "555-1234", "juan@mail.com", "Calle 1", NOW);

    // Act
    CustomerJpaEntity entity = CustomerJpaEntity.fromDomain(domain);

    // Assert
    assertEquals(1L, entity.id);
    assertEquals("Juan García", entity.name);
    assertEquals("555-1234", entity.phone);
    assertEquals("juan@mail.com", entity.email);
    assertEquals("Calle 1", entity.address);
  }
}
