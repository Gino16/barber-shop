package org.barbershop.employee.adapter.in.rest;

import org.barbershop.employee.application.EmployeeCommand;
import org.barbershop.employee.application.EmployeeFilterQuery;
import org.barbershop.employee.application.PagedResponse;
import org.barbershop.employee.application.port.in.EmployeeUseCase;
import org.barbershop.employee.domain.Employee;
import org.barbershop.employee.domain.EmployeeRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
@Path("/employees")
@Consumes("application/json")
@Produces("application/json")
public class EmployeeRestAdapter {

  private final EmployeeUseCase useCase;

  @Inject
  public EmployeeRestAdapter(EmployeeUseCase useCase) {
    this.useCase = useCase;
  }

  @GET
  public Response listEmployees(
      @QueryParam("search") String search,
      @QueryParam("role") String role,
      @QueryParam("active") Boolean active,
      @QueryParam("page") Integer page,
      @QueryParam("pageSize") Integer pageSize) {
    
    EmployeeFilterQuery query = new EmployeeFilterQuery(
        search,
        role != null ? EmployeeRole.valueOf(role) : null,
        active,
        page != null ? page : 1,
        pageSize != null ? pageSize : 10
    );
    
    PagedResponse<Employee> pagedResult = useCase.list(query);
    
    PaginatedEmployeeResponse response = new PaginatedEmployeeResponse(
        pagedResult.data().stream().map(this::toResponse).toList(),
        new PaginationInfo(pagedResult.page(), pagedResult.pageSize(), pagedResult.total(),
                          pagedResult.totalPages(), pagedResult.hasNextPage())
    );
    
    return Response.ok(response).build();
  }

  @POST
  public Response createEmployee(EmployeeRequestDTO request) {
    Employee created = useCase.create(toCommand(request));
    return Response.status(Response.Status.CREATED)
        .entity(toResponse(created)).build();
  }

  @GET
  @Path("/{id}")
  public Response getEmployee(@PathParam("id") Long id) {
    return useCase.findById(id)
        .map(e -> Response.ok(toResponse(e)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @PUT
  @Path("/{id}")
  public Response updateEmployee(@PathParam("id") Long id, EmployeeRequestDTO request) {
    return useCase.update(id, toCommand(request))
        .map(e -> Response.ok(toResponse(e)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @DELETE
  @Path("/{id}")
  public Response deleteEmployee(@PathParam("id") Long id) {
    return useCase.delete(id)
        .map(v -> Response.noContent().build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  private EmployeeCommand toCommand(EmployeeRequestDTO dto) {
    return new EmployeeCommand(dto.getName(), EmployeeRole.valueOf(dto.getRole()),
        dto.getPhone(), dto.getEmail(), dto.getActive());
  }

  private EmployeeResponseDTO toResponse(Employee employee) {
    return new EmployeeResponseDTO(employee.id(), employee.name(), employee.role().name(),
        employee.phone(), employee.email(), employee.active(), employee.createdAt());
  }

  public static class PaginatedEmployeeResponse {
    public List<EmployeeResponseDTO> data;
    public PaginationInfo pagination;

    public PaginatedEmployeeResponse(List<EmployeeResponseDTO> data, PaginationInfo pagination) {
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
