package org.barbershop.appointment.adapter.in.rest;

import jakarta.ws.rs.core.Response;
import org.barbershop.api.model.AppointmentRequest;
import org.barbershop.appointment.application.AppointmentCommand;
import org.barbershop.appointment.application.AppointmentFilterQuery;
import org.barbershop.common.pagination.PagedResponse;
import org.barbershop.appointment.application.port.in.AppointmentUseCase;
import org.barbershop.appointment.domain.Appointment;
import org.barbershop.appointment.domain.AppointmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentRestAdapter Tests")
class AppointmentRestAdapterTest {

  @Mock
  private AppointmentUseCase useCase;

  @InjectMocks
  private AppointmentRestAdapter adapter;

  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private Appointment sampleAppointment() {
    return new Appointment(1L, 1L, 2L, NOW, "Nota", AppointmentStatus.SCHEDULED, NOW);
  }

  private AppointmentRequest sampleRequest() {
    AppointmentRequest req = new AppointmentRequest();
    req.setCustomerId(1L);
    req.setEmployeeId(2L);
    req.setScheduledAt(NOW);
    req.setNotes("Nota");
    req.setStatus(AppointmentRequest.StatusEnum.SCHEDULED);
    return req;
  }

  @Test
  @DisplayName("Return200WithAppointments_WhenListCalled")
  void shouldReturn200WithAppointmentsWhenListCalled() {
    // Arrange
    PagedResponse<Appointment> paged = new PagedResponse<>(List.of(sampleAppointment()), 1, 10, 1L);
    when(useCase.list(any(AppointmentFilterQuery.class))).thenReturn(paged);

    // Act
    Response response = adapter.listAppointments(1, 10, null, null, null, null, null);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    verify(useCase).list(any(AppointmentFilterQuery.class));
  }

  @Test
  @DisplayName("Return200WithFilteredAppointments_WhenStatusFilterApplied")
  void shouldReturn200WithFilteredAppointmentsWhenStatusFilterApplied() {
    // Arrange
    PagedResponse<Appointment> paged = new PagedResponse<>(List.of(sampleAppointment()), 1, 10, 1L);
    when(useCase.list(any(AppointmentFilterQuery.class))).thenReturn(paged);

    // Act
    Response response = adapter.listAppointments(1, 10, null, null, null, null, "SCHEDULED");

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return201WithAppointment_WhenCreateCalled")
  void shouldReturn201WithAppointmentWhenCreateCalled() {
    // Arrange
    when(useCase.create(any(AppointmentCommand.class))).thenReturn(sampleAppointment());

    // Act
    Response response = adapter.createAppointment(sampleRequest());

    // Assert
    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    verify(useCase).create(any(AppointmentCommand.class));
  }

  @Test
  @DisplayName("Return200WithAppointment_WhenGetAppointmentCalled")
  void shouldReturn200WithAppointmentWhenGetAppointmentCalled() {
    // Arrange
    when(useCase.findById(1L)).thenReturn(Optional.of(sampleAppointment()));

    // Act
    Response response = adapter.getAppointment(1L);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
  }

  @Test
  @DisplayName("Return404_WhenGetAppointmentCalledWithNonExistentId")
  void shouldReturn404WhenGetAppointmentCalledWithNonExistentId() {
    // Arrange
    when(useCase.findById(999L)).thenReturn(Optional.empty());

    // Act
    Response response = adapter.getAppointment(999L);

    // Assert
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return200WithUpdated_WhenUpdateCalledWithExistentId")
  void shouldReturn200WithUpdatedWhenUpdateCalledWithExistentId() {
    // Arrange
    when(useCase.update(eq(1L), any(AppointmentCommand.class))).thenReturn(Optional.of(sampleAppointment()));

    // Act
    Response response = adapter.updateAppointment(1L, sampleRequest());

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return404_WhenUpdateCalledWithNonExistentId")
  void shouldReturn404WhenUpdateCalledWithNonExistentId() {
    // Arrange
    when(useCase.update(eq(999L), any(AppointmentCommand.class))).thenReturn(Optional.empty());

    // Act
    Response response = adapter.updateAppointment(999L, sampleRequest());

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
    Response response = adapter.deleteAppointment(1L);

    // Assert
    assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return404_WhenDeleteCalledWithNonExistentId")
  void shouldReturn404WhenDeleteCalledWithNonExistentId() {
    // Arrange
    when(useCase.delete(999L)).thenReturn(Optional.empty());

    // Act
    Response response = adapter.deleteAppointment(999L);

    // Assert
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("UseDefaultPagination_WhenNullPageParamsProvided")
  void shouldUseDefaultPaginationWhenNullPageParamsProvided() {
    // Arrange
    PagedResponse<Appointment> paged = new PagedResponse<>(List.of(), 1, 10, 0L);
    when(useCase.list(any(AppointmentFilterQuery.class))).thenReturn(paged);

    // Act
    Response response = adapter.listAppointments(null, null, null, null, null, null, null);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }
}
