package org.barbershop.employee.adapter.in.rest;

import jakarta.ws.rs.core.Response;
import org.barbershop.api.model.EmployeeRequest;
import org.barbershop.employee.application.EmployeeFilterQuery;
import org.barbershop.employee.application.PagedResponse;
import org.barbershop.employee.application.port.in.EmployeeUseCase;
import org.barbershop.employee.domain.Employee;
import org.barbershop.employee.domain.EmployeeRole;
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
@DisplayName("EmployeeRestAdapter Tests")
class EmployeeRestAdapterTest {

  @Mock
  private EmployeeUseCase useCase;

  @InjectMocks
  private EmployeeRestAdapter adapter;

  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private Employee sampleEmployee() {
    return new Employee(1L, "Pedro López", EmployeeRole.BARBER, "555-9999", "pedro@mail.com", true, NOW);
  }

  private EmployeeRequest sampleRequest() {
    EmployeeRequest req = new EmployeeRequest();
    req.setName("Pedro López");
    req.setRole(EmployeeRequest.RoleEnum.BARBER);
    req.setPhone("555-9999");
    req.setEmail("pedro@mail.com");
    req.setActive(true);
    return req;
  }

  @Test
  @DisplayName("Return200WithEmployees_WhenListCalled")
  void shouldReturn200WithEmployeesWhenListCalled() {
    // Arrange
    PagedResponse<Employee> paged = new PagedResponse<>(List.of(sampleEmployee()), 1, 10, 1L);
    when(useCase.list(any(EmployeeFilterQuery.class))).thenReturn(paged);

    // Act
    Response response = adapter.listEmployees(1, 10, null, null, null);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    verify(useCase).list(any(EmployeeFilterQuery.class));
  }

  @Test
  @DisplayName("Return200WithFilteredEmployees_WhenRoleFilterApplied")
  void shouldReturn200WithFilteredEmployeesWhenRoleFilterApplied() {
    // Arrange
    PagedResponse<Employee> paged = new PagedResponse<>(List.of(sampleEmployee()), 1, 10, 1L);
    when(useCase.list(any(EmployeeFilterQuery.class))).thenReturn(paged);

    // Act
    Response response = adapter.listEmployees(1, 10, null, "BARBER", null);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return201WithEmployee_WhenCreateCalled")
  void shouldReturn201WithEmployeeWhenCreateCalled() {
    // Arrange
    when(useCase.create(any())).thenReturn(sampleEmployee());

    // Act
    Response response = adapter.createEmployee(sampleRequest());

    // Assert
    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    verify(useCase).create(any());
  }

  @Test
  @DisplayName("Return200WithEmployee_WhenGetEmployeeCalled")
  void shouldReturn200WithEmployeeWhenGetEmployeeCalled() {
    // Arrange
    when(useCase.findById(1L)).thenReturn(Optional.of(sampleEmployee()));

    // Act
    Response response = adapter.getEmployee(1L);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
  }

  @Test
  @DisplayName("Return404_WhenGetEmployeeCalledWithNonExistentId")
  void shouldReturn404WhenGetEmployeeCalledWithNonExistentId() {
    // Arrange
    when(useCase.findById(999L)).thenReturn(Optional.empty());

    // Act
    Response response = adapter.getEmployee(999L);

    // Assert
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return200WithUpdated_WhenUpdateCalledWithExistentId")
  void shouldReturn200WithUpdatedWhenUpdateCalledWithExistentId() {
    // Arrange
    when(useCase.update(eq(1L), any())).thenReturn(Optional.of(sampleEmployee()));

    // Act
    Response response = adapter.updateEmployee(1L, sampleRequest());

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return404_WhenUpdateCalledWithNonExistentId")
  void shouldReturn404WhenUpdateCalledWithNonExistentId() {
    // Arrange
    when(useCase.update(eq(999L), any())).thenReturn(Optional.empty());

    // Act
    Response response = adapter.updateEmployee(999L, sampleRequest());

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
    Response response = adapter.deleteEmployee(1L);

    // Assert
    assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return404_WhenDeleteCalledWithNonExistentId")
  void shouldReturn404WhenDeleteCalledWithNonExistentId() {
    // Arrange
    when(useCase.delete(999L)).thenReturn(Optional.empty());

    // Act
    Response response = adapter.deleteEmployee(999L);

    // Assert
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }
}
