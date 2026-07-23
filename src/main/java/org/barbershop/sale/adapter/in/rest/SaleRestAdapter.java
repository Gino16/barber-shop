package org.barbershop.sale.adapter.in.rest;

import org.barbershop.sale.application.PagedResponse;
import org.barbershop.sale.application.SaleCommand;
import org.barbershop.sale.application.SaleItemCommand;
import org.barbershop.sale.application.port.in.SaleUseCase;
import org.barbershop.sale.domain.PaymentMethod;
import org.barbershop.sale.domain.Sale;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
@Path("/sales")
@Consumes("application/json")
@Produces("application/json")
public class SaleRestAdapter {

  private final SaleUseCase useCase;

  @Inject
  public SaleRestAdapter(SaleUseCase useCase) {
    this.useCase = useCase;
  }

  @GET
  public Response listSales(
      @QueryParam("page") Integer page,
      @QueryParam("pageSize") Integer pageSize) {
    
    PagedResponse<Sale> pagedResult = useCase.list(
        page != null ? page : 1,
        pageSize != null ? pageSize : 10
    );
    
    PaginatedSaleResponse response = new PaginatedSaleResponse(
        pagedResult.data().stream().map(this::toResponse).toList(),
        new PaginationInfo(pagedResult.page(), pagedResult.pageSize(), pagedResult.total(),
                          pagedResult.totalPages(), pagedResult.hasNextPage())
    );
    
    return Response.ok(response).build();
  }

  @POST
  public Response createSale(SaleRequestDTO request) {
    Sale created = useCase.create(toCommand(request));
    return Response.status(Response.Status.CREATED)
        .entity(toResponse(created)).build();
  }

  @GET
  @Path("/{id}")
  public Response getSale(@PathParam("id") Long id) {
    return useCase.findById(id)
        .map(s -> Response.ok(toResponse(s)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  private SaleCommand toCommand(SaleRequestDTO dto) {
    List<SaleItemCommand> items = dto.getItems().stream()
        .map(item -> new SaleItemCommand(item.getItemId(), item.getQuantity(), item.getUnitPrice()))
        .toList();
    
    return new SaleCommand(dto.getCustomerId(), dto.getEmployeeId(),
        PaymentMethod.valueOf(dto.getPaymentMethod()), dto.getDiscount(),
        dto.getNotes(), items);
  }

  private SaleResponseDTO toResponse(Sale sale) {
    List<SaleItemResponseDTO> items = sale.items().stream()
        .map(item -> new SaleItemResponseDTO(item.id(), item.saleId(), item.itemId(),
            item.quantity(), item.unitPrice()))
        .toList();
    
    return new SaleResponseDTO(sale.id(), sale.customerId(), sale.employeeId(),
        sale.paymentMethod().name(), sale.totalAmount(), sale.discount(),
        sale.notes(), items, sale.soldAt());
  }

  public static class PaginatedSaleResponse {
    public List<SaleResponseDTO> data;
    public PaginationInfo pagination;

    public PaginatedSaleResponse(List<SaleResponseDTO> data, PaginationInfo pagination) {
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
