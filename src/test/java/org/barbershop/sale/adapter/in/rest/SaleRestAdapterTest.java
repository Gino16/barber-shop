package org.barbershop.sale.adapter.in.rest;

import jakarta.ws.rs.core.Response;
import org.barbershop.api.model.SaleItemRequest;
import org.barbershop.api.model.SaleRequest;
import org.barbershop.sale.application.PagedResponse;
import org.barbershop.sale.application.port.in.SaleUseCase;
import org.barbershop.sale.domain.PaymentMethod;
import org.barbershop.sale.domain.Sale;
import org.barbershop.sale.domain.SaleItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SaleRestAdapter Tests")
class SaleRestAdapterTest {

  @Mock
  private SaleUseCase useCase;

  @InjectMocks
  private SaleRestAdapter adapter;

  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private Sale sampleSale() {
    SaleItem item = new SaleItem(1L, 1L, 10L, 2, 50.0, 100.0);
    return new Sale(1L, 1L, 2L, PaymentMethod.CASH, 90.0, 10.0, "Nota", List.of(item), NOW);
  }

  private SaleRequest sampleRequest() {
    SaleItemRequest itemReq = new SaleItemRequest();
    itemReq.setItemId(10L);
    itemReq.setQuantity(2);
    itemReq.setUnitPrice(50.0);

    SaleRequest req = new SaleRequest();
    req.setCustomerId(1L);
    req.setEmployeeId(2L);
    req.setPaymentMethod(SaleRequest.PaymentMethodEnum.CASH);
    req.setDiscount(10.0);
    req.setNotes("Nota");
    req.setItems(List.of(itemReq));
    return req;
  }

  @Test
  @DisplayName("Return200WithSales_WhenListCalled")
  void shouldReturn200WithSalesWhenListCalled() {
    // Arrange
    PagedResponse<Sale> paged = new PagedResponse<>(List.of(sampleSale()), 1, 10, 1L);
    when(useCase.list(anyInt(), anyInt())).thenReturn(paged);

    // Act
    Response response = adapter.listSales(1, 10);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    verify(useCase).list(anyInt(), anyInt());
  }

  @Test
  @DisplayName("UseDefaultPagination_WhenNullPageParamsProvided")
  void shouldUseDefaultPaginationWhenNullPageParamsProvided() {
    // Arrange
    PagedResponse<Sale> paged = new PagedResponse<>(List.of(), 1, 10, 0L);
    when(useCase.list(anyInt(), anyInt())).thenReturn(paged);

    // Act
    Response response = adapter.listSales(null, null);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return201WithSale_WhenCreateCalled")
  void shouldReturn201WithSaleWhenCreateCalled() {
    // Arrange
    when(useCase.create(any())).thenReturn(sampleSale());

    // Act
    Response response = adapter.createSale(sampleRequest());

    // Assert
    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    verify(useCase).create(any());
  }

  @Test
  @DisplayName("Return200WithSale_WhenGetSaleCalled")
  void shouldReturn200WithSaleWhenGetSaleCalled() {
    // Arrange
    when(useCase.findById(1L)).thenReturn(Optional.of(sampleSale()));

    // Act
    Response response = adapter.getSale(1L);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
  }

  @Test
  @DisplayName("Return404_WhenGetSaleCalledWithNonExistentId")
  void shouldReturn404WhenGetSaleCalledWithNonExistentId() {
    // Arrange
    when(useCase.findById(999L)).thenReturn(Optional.empty());

    // Act
    Response response = adapter.getSale(999L);

    // Assert
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }
}
