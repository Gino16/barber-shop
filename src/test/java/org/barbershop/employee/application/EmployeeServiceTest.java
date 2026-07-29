package org.barbershop.employee.application;

import org.barbershop.audit.application.AuditLogger;
import org.barbershop.audit.domain.AuditAction;
import org.barbershop.employee.application.port.out.EmployeeRepositoryPort;
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
@DisplayName("EmployeeService Tests")
class EmployeeServiceTest {

  @Mock
  private EmployeeRepositoryPort repository;

  @Mock
  private AuditLogger auditLogger;

  @InjectMocks
  private EmployeeService employeeService;

  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private Employee sampleEmployee(Long id) {
    return new Employee(id, "Pedro López", EmployeeRole.BARBER, "555-9999", "pedro@mail.com", true, NOW);
  }

  private EmployeeCommand sampleCommand() {
    return new EmployeeCommand("Pedro López", EmployeeRole.BARBER, "555-9999", "pedro@mail.com", true);
  }

  @Test
  @DisplayName("ReturnPagedEmployees_WhenListCalled")
  void shouldReturnPagedEmployeesWhenListCalled() {
    // Arrange
    EmployeeFilterQuery query = new EmployeeFilterQuery(null, null, null, 1, 10);
    when(repository.find(any(EmployeeFilterQuery.class))).thenReturn(List.of(sampleEmployee(1L)));
    when(repository.count(any(EmployeeFilterQuery.class))).thenReturn(1L);

    // Act
    PagedResponse<Employee> result = employeeService.list(query);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.data().size());
    assertEquals(1, result.page());
    assertEquals(1L, result.total());
    verify(repository).find(any(EmployeeFilterQuery.class));
    verify(repository).count(any(EmployeeFilterQuery.class));
  }

  @Test
  @DisplayName("ReturnEmployee_WhenFindByIdCalledWithExistentId")
  void shouldReturnEmployeeWhenFindByIdCalledWithExistentId() {
    // Arrange
    when(repository.findById(1L)).thenReturn(Optional.of(sampleEmployee(1L)));

    // Act
    Optional<Employee> result = employeeService.findById(1L);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(1L, result.get().id());
    assertEquals(EmployeeRole.BARBER, result.get().role());
    verify(repository).findById(1L);
  }

  @Test
  @DisplayName("ReturnEmpty_WhenFindByIdCalledWithNonExistentId")
  void shouldReturnEmptyWhenFindByIdCalledWithNonExistentId() {
    // Arrange
    when(repository.findById(999L)).thenReturn(Optional.empty());

    // Act
    Optional<Employee> result = employeeService.findById(999L);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("ReturnCreatedEmployee_WhenCreateCalled")
  void shouldReturnCreatedEmployeeWhenCreateCalled() {
    // Arrange
    Employee saved = sampleEmployee(1L);
    when(repository.save(any(Employee.class))).thenReturn(saved);

    // Act
    Employee result = employeeService.create(sampleCommand());

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals("Pedro López", result.name());
    assertTrue(result.active());
    verify(repository).save(any(Employee.class));
    verify(auditLogger).record(eq("EMPLOYEE"), eq(1L), eq(AuditAction.CREATE), isNull(), any());
  }

  @Test
  @DisplayName("ReturnActiveEmployee_WhenCreateCalledWithNullActive")
  void shouldReturnActiveEmployeeWhenCreateCalledWithNullActive() {
    // Arrange
    EmployeeCommand command = new EmployeeCommand("Pedro López", EmployeeRole.BARBER, "555-9999", "pedro@mail.com", null);
    Employee saved = sampleEmployee(1L);
    when(repository.save(any(Employee.class))).thenReturn(saved);

    // Act
    Employee result = employeeService.create(command);

    // Assert
    assertTrue(result.active());
  }

  @Test
  @DisplayName("ReturnUpdatedEmployee_WhenUpdateCalledWithExistentId")
  void shouldReturnUpdatedEmployeeWhenUpdateCalledWithExistentId() {
    // Arrange
    EmployeeCommand command = new EmployeeCommand("Pedro Actualizado", EmployeeRole.MANAGER, "555-0000", "nuevo@mail.com", false);
    Employee existing = sampleEmployee(1L);
    Employee updated = new Employee(1L, "Pedro Actualizado", EmployeeRole.MANAGER, "555-0000", "nuevo@mail.com", false, NOW);
    when(repository.findById(1L)).thenReturn(Optional.of(existing));
    when(repository.save(any(Employee.class))).thenReturn(updated);

    // Act
    Optional<Employee> result = employeeService.update(1L, command);

    // Assert
    assertTrue(result.isPresent());
    assertEquals("Pedro Actualizado", result.get().name());
    assertEquals(EmployeeRole.MANAGER, result.get().role());
    verify(auditLogger).record(eq("EMPLOYEE"), eq(1L), eq(AuditAction.UPDATE), any(), any());
  }

  @Test
  @DisplayName("ReturnEmpty_WhenUpdateCalledWithNonExistentId")
  void shouldReturnEmptyWhenUpdateCalledWithNonExistentId() {
    // Arrange
    when(repository.findById(999L)).thenReturn(Optional.empty());

    // Act
    Optional<Employee> result = employeeService.update(999L, sampleCommand());

    // Assert
    assertTrue(result.isEmpty());
    verify(repository, never()).save(any());
  }

  @Test
  @DisplayName("DeleteAndAudit_WhenDeleteCalledWithExistentId")
  void shouldDeleteAndAuditWhenDeleteCalledWithExistentId() {
    // Arrange
    when(repository.findById(1L)).thenReturn(Optional.of(sampleEmployee(1L)));
    doNothing().when(repository).delete(1L);

    // Act
    employeeService.delete(1L);

    // Assert
    verify(repository).delete(1L);
    verify(auditLogger).record(eq("EMPLOYEE"), eq(1L), eq(AuditAction.DELETE), any(), isNull());
  }

  @Test
  @DisplayName("ReturnEmpty_WhenDeleteCalledWithNonExistentId")
  void shouldReturnEmptyWhenDeleteCalledWithNonExistentId() {
    // Arrange
    when(repository.findById(999L)).thenReturn(Optional.empty());

    // Act
    Optional<Void> result = employeeService.delete(999L);

    // Assert
    assertTrue(result.isEmpty());
    verify(repository, never()).delete(any());
  }
}
