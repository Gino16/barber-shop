package org.barbershop.employee.adapter.in.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.barbershop.api.EmployeesApi;
import org.barbershop.api.model.EmployeeRequest;
import org.barbershop.api.model.EmployeeResponse;
import org.barbershop.api.model.EmployeeResponse.RoleEnum;
import org.barbershop.api.model.PaginatedEmployeesResponse;
import org.barbershop.api.model.PaginationResponse;
import org.barbershop.common.pagination.PagedResponse;
import org.barbershop.employee.application.EmployeeCommand;
import org.barbershop.employee.application.EmployeeFilterQuery;
import org.barbershop.employee.application.port.in.EmployeeUseCase;
import org.barbershop.employee.domain.Employee;
import org.barbershop.employee.domain.EmployeeRole;

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

    PaginatedEmployeesResponse response = PaginatedEmployeesResponse.builder()
        .data(pagedResult.data().stream().map(this::toResponse).toList())
        .pagination(buildPaginationResponse(pagedResult))
        .build();

    return Response.ok(response).build();
  }

  @Override
  public Response createEmployee(EmployeeRequest request) {
    Employee created = useCase.create(toCommand(request));
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
    return useCase.update(id, toCommand(request))
        .map(e -> Response.ok(toResponse(e)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @Override
  public Response deleteEmployee(Long id) {
    return useCase.delete(id)
        .map(v -> Response.noContent().build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  private EmployeeCommand toCommand(EmployeeRequest request) {
    return new EmployeeCommand(request.getName(), EmployeeRole.valueOf(request.getRole().value()),
        request.getPhone(), request.getEmail(), request.getActive());
  }

  private EmployeeResponse toResponse(Employee employee) {
    return EmployeeResponse.builder()
        .id(employee.id())
        .name(employee.name())
        .role(RoleEnum.fromValue(employee.role().name()))
        .phone(employee.phone())
        .email(employee.email())
        .active(employee.active())
        .createdAt(employee.createdAt())
        .build();
  }

  private PaginationResponse buildPaginationResponse(PagedResponse<Employee> pagedResult) {
    return PaginationResponse.builder()
        .page(pagedResult.page())
        .pageSize(pagedResult.pageSize())
        .total(pagedResult.total())
        .totalPages(pagedResult.totalPages())
        .hasNextPage(pagedResult.hasNextPage())
        .build();
  }
}
