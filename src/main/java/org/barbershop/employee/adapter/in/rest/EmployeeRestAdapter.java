package org.barbershop.employee.adapter.in.rest;

import org.barbershop.api.EmployeesApi;
import org.barbershop.api.model.EmployeeRequest;
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
import org.jspecify.annotations.NonNull;

@ApplicationScoped
public class EmployeeRestAdapter implements EmployeesApi {

  private final EmployeeUseCase useCase;

  @Inject
  public EmployeeRestAdapter(EmployeeUseCase useCase) {
    this.useCase = useCase;
  }

  @Override
  public Response listEmployees(
      Integer page,
      Integer pageSize,
      String search,
      String role,
      Boolean active) {

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

  @Override
  public Response createEmployee(EmployeeRequest request) {
    var employeeRequestDto = toCreateEmployeeRequestDto(request);
    Employee created = useCase.create(toCommand(employeeRequestDto));
    return Response.status(Response.Status.CREATED)
        .entity(toResponse(created)).build();
  }

  @Override
  public Response getEmployee(Long id) {
    return useCase.findById(id)
        .map(e -> Response.ok(toResponse(e)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @Override
  public Response updateEmployee(Long id, EmployeeRequest request) {
    var employeeRequestDto = toCreateEmployeeRequestDto(request);
    return useCase.update(id, toCommand(employeeRequestDto))
        .map(e -> Response.ok(toResponse(e)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @Override
  public Response deleteEmployee(@PathParam("id") Long id) {
    return useCase.delete(id)
        .map(v -> Response.noContent().build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  private EmployeeRequestDTO toCreateEmployeeRequestDto(EmployeeRequest request) {
    return new EmployeeRequestDTO(
        request.getName(),
        request.getRole().toString(),
        request.getPhone(),
        request.getEmail(),
        request.getActive()
    );
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
