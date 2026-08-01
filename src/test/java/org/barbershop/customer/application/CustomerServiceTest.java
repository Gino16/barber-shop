package org.barbershop.customer.application;

import org.barbershop.common.pagination.PagedResponse;
import org.barbershop.audit.application.AuditLogger;
import org.barbershop.audit.domain.AuditAction;
import org.barbershop.customer.application.port.out.CustomerRepositoryPort;
import org.barbershop.customer.domain.Customer;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Tests")
class CustomerServiceTest {

  @Mock
  private CustomerRepositoryPort repository;

  @Mock
  private AuditLogger auditLogger;

  @InjectMocks
  private CustomerService customerService;

  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private Customer sampleCustomer(Long id) {
    return new Customer(id, "Juan García", "555-1234", "juan@mail.com", "Calle 1", NOW);
  }

  @Test
  @DisplayName("ReturnPagedCustomers_WhenListCalled")
  void shouldReturnPagedCustomersWhenListCalled() {
    // Arrange
    CustomerFilterQuery query = new CustomerFilterQuery(null, 1, 10);
    when(repository.find(any(CustomerFilterQuery.class))).thenReturn(List.of(sampleCustomer(1L)));
    when(repository.count(any(CustomerFilterQuery.class))).thenReturn(1L);

    // Act
    PagedResponse<Customer> result = customerService.list(query);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.data().size());
    assertEquals(1, result.page());
    assertEquals(1L, result.total());
    verify(repository).find(any(CustomerFilterQuery.class));
    verify(repository).count(any(CustomerFilterQuery.class));
  }

  @Test
  @DisplayName("ReturnCustomer_WhenFindByIdCalledWithExistentId")
  void shouldReturnCustomerWhenFindByIdCalledWithExistentId() {
    // Arrange
    when(repository.findById(1L)).thenReturn(Optional.of(sampleCustomer(1L)));

    // Act
    Optional<Customer> result = customerService.findById(1L);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(1L, result.get().id());
    assertEquals("Juan García", result.get().name());
    verify(repository).findById(1L);
  }

  @Test
  @DisplayName("ReturnEmpty_WhenFindByIdCalledWithNonExistentId")
  void shouldReturnEmptyWhenFindByIdCalledWithNonExistentId() {
    // Arrange
    when(repository.findById(999L)).thenReturn(Optional.empty());

    // Act
    Optional<Customer> result = customerService.findById(999L);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("ReturnCreatedCustomer_WhenCreateCalled")
  void shouldReturnCreatedCustomerWhenCreateCalled() {
    // Arrange
    CustomerCommand command = new CustomerCommand("Juan García", "555-1234", "juan@mail.com", "Calle 1");
    Customer saved = sampleCustomer(1L);
    when(repository.save(any(Customer.class))).thenReturn(saved);

    // Act
    Customer result = customerService.create(command);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals("Juan García", result.name());
    verify(repository).save(any(Customer.class));
    verify(auditLogger).record(eq("CUSTOMER"), eq(1L), eq(AuditAction.CREATE), isNull(), any());
  }

  @Test
  @DisplayName("ReturnUpdatedCustomer_WhenUpdateCalledWithExistentId")
  void shouldReturnUpdatedCustomerWhenUpdateCalledWithExistentId() {
    // Arrange
    CustomerCommand command = new CustomerCommand("Juan Actualizado", "555-9999", "nuevo@mail.com", "Calle 2");
    Customer existing = sampleCustomer(1L);
    Customer updated = new Customer(1L, "Juan Actualizado", "555-9999", "nuevo@mail.com", "Calle 2", NOW);
    when(repository.findById(1L)).thenReturn(Optional.of(existing));
    when(repository.save(any(Customer.class))).thenReturn(updated);

    // Act
    Optional<Customer> result = customerService.update(1L, command);

    // Assert
    assertTrue(result.isPresent());
    assertEquals("Juan Actualizado", result.get().name());
    verify(auditLogger).record(eq("CUSTOMER"), eq(1L), eq(AuditAction.UPDATE), any(), any());
  }

  @Test
  @DisplayName("ReturnEmpty_WhenUpdateCalledWithNonExistentId")
  void shouldReturnEmptyWhenUpdateCalledWithNonExistentId() {
    // Arrange
    when(repository.findById(999L)).thenReturn(Optional.empty());

    // Act
    Optional<Customer> result = customerService.update(999L, new CustomerCommand("A", "B", "C", "D"));

    // Assert
    assertTrue(result.isEmpty());
    verify(repository, never()).save(any());
  }

  @Test
  @DisplayName("DeleteAndAudit_WhenDeleteCalledWithExistentId")
  void shouldDeleteAndAuditWhenDeleteCalledWithExistentId() {
    // Arrange
    Customer existing = sampleCustomer(1L);
    when(repository.findById(1L)).thenReturn(Optional.of(existing));
    doNothing().when(repository).delete(1L);

    // Act
    customerService.delete(1L);

    // Assert
    verify(repository).delete(1L);
    verify(auditLogger).record(eq("CUSTOMER"), eq(1L), eq(AuditAction.DELETE), any(), isNull());
  }

  @Test
  @DisplayName("ReturnEmpty_WhenDeleteCalledWithNonExistentId")
  void shouldReturnEmptyWhenDeleteCalledWithNonExistentId() {
    // Arrange
    when(repository.findById(999L)).thenReturn(Optional.empty());

    // Act
    Optional<Void> result = customerService.delete(999L);

    // Assert
    assertTrue(result.isEmpty());
    verify(repository, never()).delete(any());
  }
}
