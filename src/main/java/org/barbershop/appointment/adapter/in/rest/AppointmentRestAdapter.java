package org.barbershop.appointment.adapter.in.rest;

import org.barbershop.appointment.application.AppointmentCommand;
import org.barbershop.appointment.application.AppointmentFilterQuery;
import org.barbershop.appointment.application.PagedResponse;
import org.barbershop.appointment.application.port.in.AppointmentUseCase;
import org.barbershop.appointment.domain.Appointment;
import org.barbershop.appointment.domain.AppointmentStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
@Path("/appointments")
@Consumes("application/json")
@Produces("application/json")
public class AppointmentRestAdapter {

  private final AppointmentUseCase useCase;

  @Inject
  public AppointmentRestAdapter(AppointmentUseCase useCase) {
    this.useCase = useCase;
  }

  @GET
  public Response listAppointments(
      @QueryParam("startDate") String startDate,
      @QueryParam("endDate") String endDate,
      @QueryParam("employeeId") Long employeeId,
      @QueryParam("customerId") Long customerId,
      @QueryParam("status") String status,
      @QueryParam("page") Integer page,
      @QueryParam("pageSize") Integer pageSize) {
    
    AppointmentFilterQuery query = new AppointmentFilterQuery(
        startDate != null ? LocalDate.parse(startDate) : null,
        endDate != null ? LocalDate.parse(endDate) : null,
        employeeId,
        customerId,
        status != null ? AppointmentStatus.valueOf(status) : null,
        page != null ? page : 1,
        pageSize != null ? pageSize : 10
    );
    
    PagedResponse<Appointment> pagedResult = useCase.list(query);
    
    PaginatedAppointmentResponse response = new PaginatedAppointmentResponse(
        pagedResult.data().stream().map(this::toResponse).toList(),
        new PaginationInfo(pagedResult.page(), pagedResult.pageSize(), pagedResult.total(),
                          pagedResult.totalPages(), pagedResult.hasNextPage())
    );
    
    return Response.ok(response).build();
  }

  @POST
  public Response createAppointment(AppointmentRequestDTO request) {
    Appointment created = useCase.create(toCommand(request));
    return Response.status(Response.Status.CREATED)
        .entity(toResponse(created)).build();
  }

  @GET
  @Path("/{id}")
  public Response getAppointment(@PathParam("id") Long id) {
    return useCase.findById(id)
        .map(a -> Response.ok(toResponse(a)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @PUT
  @Path("/{id}")
  public Response updateAppointment(@PathParam("id") Long id, AppointmentRequestDTO request) {
    return useCase.update(id, toCommand(request))
        .map(a -> Response.ok(toResponse(a)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @DELETE
  @Path("/{id}")
  public Response deleteAppointment(@PathParam("id") Long id) {
    return useCase.delete(id)
        .map(v -> Response.noContent().build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  private AppointmentCommand toCommand(AppointmentRequestDTO dto) {
    return new AppointmentCommand(dto.getCustomerId(), dto.getEmployeeId(),
        dto.getScheduledAt(), dto.getNotes(),
        dto.getStatus() != null ? AppointmentStatus.valueOf(dto.getStatus()) : null);
  }

  private AppointmentResponseDTO toResponse(Appointment appointment) {
    return new AppointmentResponseDTO(appointment.id(), appointment.customerId(),
        appointment.employeeId(), appointment.scheduledAt(), appointment.notes(),
        appointment.status().name(), appointment.createdAt());
  }

  public static class PaginatedAppointmentResponse {
    public List<AppointmentResponseDTO> data;
    public PaginationInfo pagination;

    public PaginatedAppointmentResponse(List<AppointmentResponseDTO> data, PaginationInfo pagination) {
      this.data = data;
      this.pagination = pagination;
    }
  }

  public static class PaginationInfo {
    public int page;
    public int pageSize;
    public long total;
    public int totalPages;
    public boolean hasNextPage;

    public PaginationInfo(int page, int pageSize, long total, int totalPages, boolean hasNextPage) {
      this.page = page;
      this.pageSize = pageSize;
      this.total = total;
      this.totalPages = totalPages;
      this.hasNextPage = hasNextPage;
    }
  }
}
