package org.barbershop.appointment.application;

import jakarta.ws.rs.NotFoundException;
import org.barbershop.audit.application.AuditLogger;
import org.barbershop.appointment.application.port.out.AppointmentRepositoryPort;
import org.barbershop.appointment.domain.Appointment;
import org.barbershop.appointment.domain.AppointmentStatus;
import org.barbershop.customer.application.port.out.CustomerRepositoryPort;
import org.barbershop.customer.domain.Customer;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService Tests")
class AppointmentServiceTest {

  @Mock
  private AppointmentRepositoryPort repository;

  @Mock
  private CustomerRepositoryPort customerRepository;

  @Mock
  private EmployeeRepositoryPort employeeRepository;

  @Mock
  private AuditLogger auditLogger;

  @InjectMocks
  private AppointmentService appointmentService;

  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private Appointment sampleAppointment(Long id) {
    return new Appointment(id, 1L, 2L, NOW, "Corte normal", AppointmentStatus.SCHEDULED, NOW);
  }

  private AppointmentCommand sampleCommand() {
    return new AppointmentCommand(1L, 2L, NOW, "Corte normal", AppointmentStatus.SCHEDULED);
  }

  private Customer sampleCustomer() {
    return new Customer(1L, "Juan", "555-1234", "juan@mail.com", "Calle 1", NOW);
  }

  private Employee sampleEmployee() {
    return new Employee(2L, "Pedro", EmployeeRole.BARBER, "555-9999", "pedro@mail.com", true, NOW);
  }

  @Test
  @DisplayName("ReturnPagedAppointments_WhenListCalled")
  void shouldReturnPagedAppointmentsWhenListCalled() {
    // Arrange
    AppointmentFilterQuery query = new AppointmentFilterQuery(null, null, null, null, null, 1, 10);
    when(repository.find(any(AppointmentFilterQuery.class))).thenReturn(List.of(sampleAppointment(1L)));
    when(repository.count(any(AppointmentFilterQuery.class))).thenReturn(1L);

    // Act
    PagedResponse<Appointment> result = appointmentService.list(query);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.data().size());
    assertEquals(1, result.page());
    assertEquals(1L, result.total());
    verify(repository).find(any(AppointmentFilterQuery.class));
    verify(repository).count(any(AppointmentFilterQuery.class));
  }

  @Test
  @DisplayName("ReturnAppointment_WhenFindByIdCalledWithExistentId")
  void shouldReturnAppointmentWhenFindByIdCalledWithExistentId() {
    // Arrange
    when(repository.findById(1L)).thenReturn(Optional.of(sampleAppointment(1L)));

    // Act
    Optional<Appointment> result = appointmentService.findById(1L);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(1L, result.get().id());
    verify(repository).findById(1L);
  }

  @Test
  @DisplayName("ReturnEmpty_WhenFindByIdCalledWithNonExistentId")
  void shouldReturnEmptyWhenFindByIdCalledWithNonExistentId() {
    // Arrange
    when(repository.findById(999L)).thenReturn(Optional.empty());

    // Act
    Optional<Appointment> result = appointmentService.findById(999L);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("ReturnCreatedAppointment_WhenCreateCalledWithValidCommand")
  void shouldReturnCreatedAppointmentWhenCreateCalledWithValidCommand() {
    // Arrange
    AppointmentCommand command = sampleCommand();
    when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer()));
    when(employeeRepository.findById(2L)).thenReturn(Optional.of(sampleEmployee()));
    when(repository.save(any(Appointment.class))).thenReturn(sampleAppointment(1L));

    // Act
    Appointment result = appointmentService.create(command);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals(AppointmentStatus.SCHEDULED, result.status());
    verify(repository).save(any(Appointment.class));
    verify(auditLogger).record(eq("APPOINTMENT"), eq(1L), any(), isNull(), any());
  }

  @Test
  @DisplayName("UseDefaultScheduledStatus_WhenCreateCalledWithNullStatus")
  void shouldUseDefaultScheduledStatusWhenCreateCalledWithNullStatus() {
    // Arrange
    AppointmentCommand command = new AppointmentCommand(1L, 2L, NOW, "Nota", null);
    when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer()));
    when(employeeRepository.findById(2L)).thenReturn(Optional.of(sampleEmployee()));
    when(repository.save(any(Appointment.class))).thenAnswer(inv -> {
      Appointment a = inv.getArgument(0);
      return new Appointment(1L, a.customerId(), a.employeeId(), a.scheduledAt(), a.notes(), a.status(), a.createdAt());
    });

    // Act
    Appointment result = appointmentService.create(command);

    // Assert
    assertEquals(AppointmentStatus.SCHEDULED, result.status());
  }

  @Test
  @DisplayName("ThrowNotFoundException_WhenCreateCalledWithNonExistentCustomer")
  void shouldThrowNotFoundExceptionWhenCreateCalledWithNonExistentCustomer() {
    // Arrange
    AppointmentCommand command = sampleCommand();
    when(customerRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(NotFoundException.class, () -> appointmentService.create(command));
    verify(repository, never()).save(any());
  }

  @Test
  @DisplayName("ThrowNotFoundException_WhenCreateCalledWithNonExistentEmployee")
  void shouldThrowNotFoundExceptionWhenCreateCalledWithNonExistentEmployee() {
    // Arrange
    AppointmentCommand command = sampleCommand();
    when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer()));
    when(employeeRepository.findById(2L)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(NotFoundException.class, () -> appointmentService.create(command));
    verify(repository, never()).save(any());
  }

  @Test
  @DisplayName("ReturnUpdatedAppointment_WhenUpdateCalledWithExistentId")
  void shouldReturnUpdatedAppointmentWhenUpdateCalledWithExistentId() {
    // Arrange
    AppointmentCommand command = new AppointmentCommand(1L, 2L, NOW, "Actualizado", AppointmentStatus.IN_PROGRESS);
    Appointment existing = sampleAppointment(1L);
    Appointment updated = new Appointment(1L, 1L, 2L, NOW, "Actualizado", AppointmentStatus.IN_PROGRESS, NOW);
    when(repository.findById(1L)).thenReturn(Optional.of(existing));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer()));
    when(employeeRepository.findById(2L)).thenReturn(Optional.of(sampleEmployee()));
    when(repository.save(any(Appointment.class))).thenReturn(updated);

    // Act
    Optional<Appointment> result = appointmentService.update(1L, command);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(AppointmentStatus.IN_PROGRESS, result.get().status());
    verify(auditLogger).record(eq("APPOINTMENT"), eq(1L), any(), any(), any());
  }

  @Test
  @DisplayName("ReturnEmpty_WhenUpdateCalledWithNonExistentId")
  void shouldReturnEmptyWhenUpdateCalledWithNonExistentId() {
    // Arrange
    when(repository.findById(999L)).thenReturn(Optional.empty());

    // Act
    Optional<Appointment> result = appointmentService.update(999L, sampleCommand());

    // Assert
    assertTrue(result.isEmpty());
    verify(repository, never()).save(any());
  }

  @Test
  @DisplayName("ReturnEmpty_WhenDeleteCalledWithNonExistentId")
  void shouldReturnEmptyWhenDeleteCalledWithNonExistentId() {
    // Arrange
    when(repository.findById(999L)).thenReturn(Optional.empty());

    // Act
    Optional<Void> result = appointmentService.delete(999L);

    // Assert
    assertTrue(result.isEmpty());
    verify(repository, never()).delete(any());
  }

  @Test
  @DisplayName("DeleteAndAudit_WhenDeleteCalledWithExistentId")
  void shouldDeleteAndAuditWhenDeleteCalledWithExistentId() {
    // Arrange
    when(repository.findById(1L)).thenReturn(Optional.of(sampleAppointment(1L)));
    doNothing().when(repository).delete(1L);

    // Act
    appointmentService.delete(1L);

    // Assert
    verify(repository).delete(1L);
    verify(auditLogger).record(eq("APPOINTMENT"), eq(1L), any(), any(), isNull());
  }
}
