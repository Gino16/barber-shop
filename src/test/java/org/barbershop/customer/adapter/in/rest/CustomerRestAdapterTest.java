package org.barbershop.customer.adapter.in.rest;

import jakarta.ws.rs.core.Response;
import org.barbershop.api.model.CustomerRequest;
import org.barbershop.customer.application.CustomerFilterQuery;
import org.barbershop.common.pagination.PagedResponse;
import org.barbershop.customer.application.port.in.CustomerUseCase;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerRestAdapter Tests")
class CustomerRestAdapterTest {

  @Mock
  private CustomerUseCase useCase;

  @InjectMocks
  private CustomerRestAdapter adapter;

  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private Customer sampleCustomer() {
    return new Customer(1L, "Juan García", "555-1234", "juan@mail.com", "Calle 1", NOW);
  }

  private CustomerRequest sampleRequest() {
    CustomerRequest req = new CustomerRequest();
    req.setName("Juan García");
    req.setPhone("555-1234");
    req.setEmail("juan@mail.com");
    req.setAddress("Calle 1");
    return req;
  }

  @Test
  @DisplayName("Return200WithCustomers_WhenListCalled")
  void shouldReturn200WithCustomersWhenListCalled() {
    // Arrange
    PagedResponse<Customer> paged = new PagedResponse<>(List.of(sampleCustomer()), 1, 10, 1L);
    when(useCase.list(any(CustomerFilterQuery.class))).thenReturn(paged);

    // Act
    Response response = adapter.listCustomers(1, 10, null);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    verify(useCase).list(any(CustomerFilterQuery.class));
  }

  @Test
  @DisplayName("Return200WithSearchResults_WhenSearchProvided")
  void shouldReturn200WithSearchResultsWhenSearchProvided() {
    // Arrange
    PagedResponse<Customer> paged = new PagedResponse<>(List.of(sampleCustomer()), 1, 10, 1L);
    when(useCase.list(any(CustomerFilterQuery.class))).thenReturn(paged);

    // Act
    Response response = adapter.listCustomers(1, 10, "Juan");

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return201WithCustomer_WhenCreateCalled")
  void shouldReturn201WithCustomerWhenCreateCalled() {
    // Arrange
    when(useCase.create(any())).thenReturn(sampleCustomer());

    // Act
    Response response = adapter.createCustomer(sampleRequest());

    // Assert
    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    verify(useCase).create(any());
  }

  @Test
  @DisplayName("Return200WithCustomer_WhenGetCustomerCalled")
  void shouldReturn200WithCustomerWhenGetCustomerCalled() {
    // Arrange
    when(useCase.findById(1L)).thenReturn(Optional.of(sampleCustomer()));

    // Act
    Response response = adapter.getCustomer(1L);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
  }

  @Test
  @DisplayName("Return404_WhenGetCustomerCalledWithNonExistentId")
  void shouldReturn404WhenGetCustomerCalledWithNonExistentId() {
    // Arrange
    when(useCase.findById(999L)).thenReturn(Optional.empty());

    // Act
    Response response = adapter.getCustomer(999L);

    // Assert
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return200WithUpdated_WhenUpdateCalledWithExistentId")
  void shouldReturn200WithUpdatedWhenUpdateCalledWithExistentId() {
    // Arrange
    when(useCase.update(eq(1L), any())).thenReturn(Optional.of(sampleCustomer()));

    // Act
    Response response = adapter.updateCustomer(1L, sampleRequest());

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return404_WhenUpdateCalledWithNonExistentId")
  void shouldReturn404WhenUpdateCalledWithNonExistentId() {
    // Arrange
    when(useCase.update(eq(999L), any())).thenReturn(Optional.empty());

    // Act
    Response response = adapter.updateCustomer(999L, sampleRequest());

    // Assert
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return204_WhenDeleteCalledWithExistentId")
  @SuppressWarnings("unchecked")
  void shouldReturn204WhenDeleteCalledWithExistentId() {
    // Arrange — Optional<Void> can't hold a real Void; mock the Optional so .map() returns a present Response
    Optional<Void> mockedPresent = mock(Optional.class);
    when(mockedPresent.map(any())).thenReturn(Optional.of(Response.noContent().build()));
    when(useCase.delete(1L)).thenReturn(mockedPresent);

    // Act
    Response response = adapter.deleteCustomer(1L);

    // Assert
    assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return404_WhenDeleteCalledWithNonExistentId")
  void shouldReturn404WhenDeleteCalledWithNonExistentId() {
    // Arrange
    when(useCase.delete(999L)).thenReturn(Optional.empty());

    // Act
    Response response = adapter.deleteCustomer(999L);

    // Assert
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("UseDefaultPagination_WhenNullPageParamsProvided")
  void shouldUseDefaultPaginationWhenNullPageParamsProvided() {
    // Arrange
    PagedResponse<Customer> paged = new PagedResponse<>(List.of(), 1, 10, 0L);
    when(useCase.list(any(CustomerFilterQuery.class))).thenReturn(paged);

    // Act
    Response response = adapter.listCustomers(null, null, null);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }
}
