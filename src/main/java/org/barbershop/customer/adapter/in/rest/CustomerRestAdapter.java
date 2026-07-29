package org.barbershop.customer.adapter.in.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.barbershop.api.CustomersApi;
import org.barbershop.api.model.CustomerRequest;
import org.barbershop.api.model.CustomerResponse;
import org.barbershop.api.model.PaginatedCustomersResponse;
import org.barbershop.api.model.PaginationResponse;
import org.barbershop.customer.application.CustomerCommand;
import org.barbershop.customer.application.CustomerFilterQuery;
import org.barbershop.customer.application.PagedResponse;
import org.barbershop.customer.application.port.in.CustomerUseCase;
import org.barbershop.customer.domain.Customer;

@ApplicationScoped
public class CustomerRestAdapter implements CustomersApi {

  private final CustomerUseCase useCase;

  @Inject
  public CustomerRestAdapter(CustomerUseCase useCase) {
    this.useCase = useCase;
  }

  @Override
  public Response listCustomers(
      Integer page,
      Integer pageSize,
      String search) {

    CustomerFilterQuery query = new CustomerFilterQuery(
        search,
        page != null ? page : 1,
        pageSize != null ? pageSize : 10
    );

    PagedResponse<Customer> pagedResult = useCase.list(query);

    PaginatedCustomersResponse response = PaginatedCustomersResponse.builder()
        .data(pagedResult.data().stream().map(this::toResponse).toList())
        .pagination(buildPaginationResponse(pagedResult))
        .build();

    return Response.ok(response).build();
  }

  @Override
  public Response createCustomer(CustomerRequest request) {
    Customer created = useCase.create(toCommand(request));
    return Response.status(Response.Status.CREATED)
        .entity(toResponse(created)).build();
  }

  @Override
  public Response getCustomer(Long id) {
    return useCase.findById(id)
        .map(c -> Response.ok(toResponse(c)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @Override
  public Response updateCustomer(Long id, CustomerRequest request) {
    return useCase.update(id, toCommand(request))
        .map(c -> Response.ok(toResponse(c)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @Override
  public Response deleteCustomer(Long id) {
    return useCase.delete(id)
        .map(v -> Response.noContent().build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  private CustomerCommand toCommand(CustomerRequest dto) {
    return new CustomerCommand(dto.getName(), dto.getPhone(), dto.getEmail(), dto.getAddress());
  }

  private CustomerResponse toResponse(Customer customer) {
    return CustomerResponse.builder()
        .id(customer.id())
        .name(customer.name())
        .phone(customer.phone())
        .email(customer.email())
        .address(customer.address())
        .createdAt(customer.createdAt())
        .build();
  }

  private PaginationResponse buildPaginationResponse(PagedResponse<Customer> pagedResult) {
    return PaginationResponse.builder()
        .page(pagedResult.page())
        .pageSize(pagedResult.pageSize())
        .total(pagedResult.total())
        .totalPages(pagedResult.totalPages())
        .hasNextPage(pagedResult.hasNextPage())
        .build();
  }
}
