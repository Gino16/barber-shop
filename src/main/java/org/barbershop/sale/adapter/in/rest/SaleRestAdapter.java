package org.barbershop.sale.adapter.in.rest;

import org.barbershop.api.SalesApi;
import org.barbershop.api.model.PaginatedSalesResponse;
import org.barbershop.api.model.PaginationResponse;
import org.barbershop.api.model.SaleRequest;
import org.barbershop.api.model.SaleItemResponse;
import org.barbershop.api.model.SaleResponse;
import org.barbershop.sale.application.PagedResponse;
import org.barbershop.sale.application.SaleCommand;
import org.barbershop.sale.application.SaleItemCommand;
import org.barbershop.sale.application.port.in.SaleUseCase;
import org.barbershop.sale.domain.PaymentMethod;
import org.barbershop.sale.domain.Sale;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class SaleRestAdapter implements SalesApi {

  private final SaleUseCase useCase;

  @Inject
  public SaleRestAdapter(SaleUseCase useCase) {
    this.useCase = useCase;
  }

  @Override
  public Response listSales(
      Integer page,
      Integer pageSize) {

    PagedResponse<Sale> pagedResult = useCase.list(
        page != null ? page : 1,
        pageSize != null ? pageSize : 10
    );

    PaginatedSalesResponse response = new PaginatedSalesResponse()
        .data(pagedResult.data().stream().map(this::toResponse).toList())
        .pagination(new PaginationResponse()
            .page(pagedResult.page())
            .pageSize(pagedResult.pageSize())
            .total(pagedResult.total())
            .totalPages(pagedResult.totalPages())
            .hasNextPage(pagedResult.hasNextPage()));

    return Response.ok(response).build();
  }

  @Override
  public Response createSale(SaleRequest request) {
    Sale created = useCase.create(toCommand(request));
    return Response.status(Response.Status.CREATED)
        .entity(toResponse(created)).build();
  }

  @Override
  public Response getSale(Long id) {
    return useCase.findById(id)
        .map(s -> Response.ok(toResponse(s)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  private SaleCommand toCommand(SaleRequest request) {
    return new SaleCommand(request.getCustomerId(), request.getEmployeeId(),
        PaymentMethod.valueOf(request.getPaymentMethod().value()), request.getDiscount(),
        request.getNotes(), request.getItems().stream()
            .map(item -> new SaleItemCommand(item.getItemId(), item.getQuantity(),
                item.getUnitPrice()))
            .toList());
  }

  private SaleResponse toResponse(Sale sale) {
    return new SaleResponse()
        .id(sale.id())
        .customerId(sale.customerId())
        .employeeId(sale.employeeId())
        .paymentMethod(SaleResponse.PaymentMethodEnum
            .fromValue(sale.paymentMethod().name()))
        .totalAmount(sale.totalAmount())
        .discount(sale.discount())
        .notes(sale.notes())
        .items(sale.items().stream().map(this::toResponse).toList())
        .soldAt(sale.soldAt());
  }

  private SaleItemResponse toResponse(org.barbershop.sale.domain.SaleItem item) {
    return new SaleItemResponse()
        .id(item.id())
        .saleId(item.saleId())
        .itemId(item.itemId())
        .quantity(item.quantity())
        .unitPrice(item.unitPrice())
        .subtotalAmount(item.subtotalAmount());
  }
}
