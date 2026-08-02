package org.barbershop.appointment.adapter.in.rest;

import static org.barbershop.common.utils.Constants.LIMA_ZONE;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.ZoneId;
import org.barbershop.api.AppointmentsApi;
import org.barbershop.api.model.AppointmentRequest;
import org.barbershop.api.model.AppointmentResponse;
import org.barbershop.api.model.PaginatedAppointmentsResponse;
import org.barbershop.api.model.PaginationResponse;
import org.barbershop.appointment.application.AppointmentCommand;
import org.barbershop.appointment.application.AppointmentFilterQuery;
import org.barbershop.appointment.application.port.in.AppointmentUseCase;
import org.barbershop.appointment.domain.Appointment;
import org.barbershop.appointment.domain.AppointmentStatus;
import org.barbershop.common.pagination.PagedResponse;

@ApplicationScoped
public class AppointmentRestAdapter implements AppointmentsApi {

  private final AppointmentUseCase useCase;

  @Inject
  public AppointmentRestAdapter(AppointmentUseCase useCase) {
    this.useCase = useCase;
  }

  @Override
  public Response listAppointments(
      Integer page,
      Integer pageSize,
      LocalDate startDate,
      LocalDate endDate,
      Long employeeId,
      Long customerId,
      String status) {

    AppointmentFilterQuery query = new AppointmentFilterQuery(
        startDate,
        endDate,
        employeeId,
        customerId,
        status != null ? AppointmentStatus.valueOf(status) : null,
        page != null ? page : 1,
        pageSize != null ? pageSize : 10
    );

    PagedResponse<Appointment> pagedResult = useCase.list(query);

    PaginatedAppointmentsResponse response = PaginatedAppointmentsResponse.builder()
        .data(pagedResult.data().stream().map(this::toResponse).toList())
        .pagination(buildPaginationResponse(pagedResult))
        .build();

    return Response.ok(response).build();
  }

  @Override
  public Response createAppointment(AppointmentRequest request) {
    Appointment created = useCase.create(toCommand(request));
    return Response.status(Response.Status.CREATED)
        .entity(toResponse(created)).build();
  }

  @Override
  public Response getAppointment(Long id) {
    return useCase.findById(id)
        .map(a -> Response.ok(toResponse(a)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @Override
  public Response updateAppointment(Long id, AppointmentRequest request) {
    return useCase.update(id, toCommand(request))
        .map(a -> Response.ok(toResponse(a)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @Override
  public Response deleteAppointment(Long id) {
    return useCase.delete(id)
        .map(v -> Response.noContent().build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  private AppointmentCommand toCommand(AppointmentRequest dto) {
    return new AppointmentCommand(
        dto.getCustomerId(), dto.getEmployeeId(),
        dto.getScheduledAt(), dto.getNotes(),
        dto.getStatus() != null ? AppointmentStatus.valueOf(dto.getStatus().name()) : null);
  }

  private AppointmentResponse toResponse(Appointment appointment) {
    return AppointmentResponse.builder()
        .id(appointment.id())
        .customerId(appointment.customerId())
        .employeeId(appointment.employeeId())
        .scheduledAt(appointment.scheduledAt().atZoneSameInstant(LIMA_ZONE).toOffsetDateTime())
        .notes(appointment.notes())
        .status(AppointmentResponse.StatusEnum.valueOf(appointment.status().name()))
        .createdAt(appointment.createdAt().atZoneSameInstant(LIMA_ZONE).toOffsetDateTime())
        .build();
  }

  private PaginationResponse buildPaginationResponse(
      PagedResponse<Appointment> pagedResult) {
    return PaginationResponse.builder()
        .page(pagedResult.page())
        .pageSize(pagedResult.pageSize())
        .total(pagedResult.total())
        .totalPages(pagedResult.totalPages())
        .hasNextPage(pagedResult.hasNextPage())
        .build();
  }
}
