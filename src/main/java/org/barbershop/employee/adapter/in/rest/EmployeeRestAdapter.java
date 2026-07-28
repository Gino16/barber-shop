package org.barbershop.employee.adapter.in.rest;

import org.barbershop.api.EmployeesApi;
import org.barbershop.api.model.EmployeeRequest;
import org.barbershop.api.model.PaginatedEmployees;
import org.barbershop.api.model.Pagination;
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

    PaginatedEmployees response = new PaginatedEmployees()
        .data(pagedResult.data().stream().map(this::toResponse).toList())
        .pagination(new Pagination()
            .page(pagedResult.page())
            .pageSize(pagedResult.pageSize())
            .total(pagedResult.total())
            .totalPages(pagedResult.totalPages())
            .hasNextPage(pagedResult.hasNextPage()));

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
  public Response deleteEmployee(@PathParam("id") Long id) {
    return useCase.delete(id)
        .map(v -> Response.noContent().build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  private EmployeeCommand toCommand(EmployeeRequest request) {
    return new EmployeeCommand(request.getName(), EmployeeRole.valueOf(request.getRole().value()),
        request.getPhone(), request.getEmail(), request.getActive());
  }

  private org.barbershop.api.model.Employee toResponse(Employee employee) {
    return new org.barbershop.api.model.Employee()
        .id(employee.id())
        .name(employee.name())
        .role(org.barbershop.api.model.Employee.RoleEnum.fromValue(employee.role().name()))
        .phone(employee.phone())
        .email(employee.email())
        .active(employee.active())
        .createdAt(employee.createdAt());
  }
}
