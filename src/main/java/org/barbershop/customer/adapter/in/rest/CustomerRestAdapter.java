package org.barbershop.customer.adapter.in.rest;

import org.barbershop.customer.application.CustomerCommand;
import org.barbershop.customer.application.CustomerFilterQuery;
import org.barbershop.customer.application.PagedResponse;
import org.barbershop.customer.application.port.in.CustomerUseCase;
import org.barbershop.customer.domain.Customer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
@Path("/customers")
@Consumes("application/json")
@Produces("application/json")
public class CustomerRestAdapter {

  private final CustomerUseCase useCase;

  @Inject
  public CustomerRestAdapter(CustomerUseCase useCase) {
    this.useCase = useCase;
  }

  @GET
  public Response listCustomers(
      @QueryParam("search") String search,
      @QueryParam("page") Integer page,
      @QueryParam("pageSize") Integer pageSize) {
    
    CustomerFilterQuery query = new CustomerFilterQuery(
        search,
        page != null ? page : 1,
        pageSize != null ? pageSize : 10
    );
    
    PagedResponse<Customer> pagedResult = useCase.list(query);
    
    PaginatedCustomerResponse response = new PaginatedCustomerResponse(
        pagedResult.data().stream().map(this::toResponse).toList(),
        new PaginationInfo(pagedResult.page(), pagedResult.pageSize(), pagedResult.total(),
                          pagedResult.totalPages(), pagedResult.hasNextPage())
    );
    
    return Response.ok(response).build();
  }

  @POST
  public Response createCustomer(CustomerRequestDTO request) {
    Customer created = useCase.create(toCommand(request));
    return Response.status(Response.Status.CREATED)
        .entity(toResponse(created)).build();
  }

  @GET
  @Path("/{id}")
  public Response getCustomer(@PathParam("id") Long id) {
    return useCase.findById(id)
        .map(c -> Response.ok(toResponse(c)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @PUT
  @Path("/{id}")
  public Response updateCustomer(@PathParam("id") Long id, CustomerRequestDTO request) {
    return useCase.update(id, toCommand(request))
        .map(c -> Response.ok(toResponse(c)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @DELETE
  @Path("/{id}")
  public Response deleteCustomer(@PathParam("id") Long id) {
    return useCase.delete(id)
        .map(v -> Response.noContent().build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  private CustomerCommand toCommand(CustomerRequestDTO dto) {
    return new CustomerCommand(dto.getName(), dto.getPhone(), dto.getEmail(), dto.getAddress());
  }

  private CustomerResponseDTO toResponse(Customer customer) {
    return new CustomerResponseDTO(customer.id(), customer.name(), customer.phone(),
        customer.email(), customer.address(), customer.createdAt());
  }

  public static class PaginatedCustomerResponse {
    public List<CustomerResponseDTO> data;
    public PaginationInfo pagination;

    public PaginatedCustomerResponse(List<CustomerResponseDTO> data, PaginationInfo pagination) {
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
