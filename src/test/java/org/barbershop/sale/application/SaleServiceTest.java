package org.barbershop.sale.application;

import org.barbershop.common.pagination.PagedResponse;
import org.barbershop.audit.application.AuditLogger;
import org.barbershop.audit.domain.AuditAction;
import org.barbershop.sale.application.port.out.SaleRepositoryPort;
import org.barbershop.sale.domain.PaymentMethod;
import org.barbershop.sale.domain.Sale;
import org.barbershop.sale.domain.SaleItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
@DisplayName("SaleService Tests")
class SaleServiceTest {

  @Mock
  private SaleRepositoryPort repository;

  @Mock
  private AuditLogger auditLogger;

  @InjectMocks
  private SaleService saleService;

  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private Sale sampleSale(Long id) {
    SaleItem item = new SaleItem(1L, id, 10L, 2, 50.0, 100.0);
    return new Sale(id, 1L, 2L, PaymentMethod.CASH, 90.0, 10.0, "Nota", List.of(item), NOW);
  }

  private SaleCommand sampleCommand() {
    SaleItemCommand itemCmd = new SaleItemCommand(10L, 2, 50.0);
    return new SaleCommand(1L, 2L, PaymentMethod.CASH, 10.0, "Nota", List.of(itemCmd));
  }

  @Test
  @DisplayName("ReturnPagedSales_WhenListCalled")
  void shouldReturnPagedSalesWhenListCalled() {
    // Arrange
    when(repository.find(anyInt(), anyInt())).thenReturn(List.of(sampleSale(1L)));
    when(repository.count()).thenReturn(1L);

    // Act
    PagedResponse<Sale> result = saleService.list(1, 10);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.data().size());
    assertEquals(1, result.page());
    assertEquals(1L, result.total());
    verify(repository).find(anyInt(), anyInt());
    verify(repository).count();
  }

  @Test
  @DisplayName("ReturnPageWithDefaults_WhenListCalledWithZeroPageParams")
  void shouldReturnPageWithDefaultsWhenListCalledWithZeroPageParams() {
    // Arrange
    when(repository.find(anyInt(), anyInt())).thenReturn(List.of());
    when(repository.count()).thenReturn(0L);

    // Act
    PagedResponse<Sale> result = saleService.list(0, 0);

    // Assert
    assertEquals(1, result.page());
    assertEquals(10, result.pageSize());
  }

  @Test
  @DisplayName("ReturnSale_WhenFindByIdCalledWithExistentId")
  void shouldReturnSaleWhenFindByIdCalledWithExistentId() {
    // Arrange
    when(repository.findById(1L)).thenReturn(Optional.of(sampleSale(1L)));

    // Act
    Optional<Sale> result = saleService.findById(1L);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(1L, result.get().id());
    assertEquals(PaymentMethod.CASH, result.get().paymentMethod());
    verify(repository).findById(1L);
  }

  @Test
  @DisplayName("ReturnEmpty_WhenFindByIdCalledWithNonExistentId")
  void shouldReturnEmptyWhenFindByIdCalledWithNonExistentId() {
    // Arrange
    when(repository.findById(999L)).thenReturn(Optional.empty());

    // Act
    Optional<Sale> result = saleService.findById(999L);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("ReturnCreatedSale_WhenCreateCalled")
  void shouldReturnCreatedSaleWhenCreateCalled() {
    // Arrange
    Sale saved = sampleSale(1L);
    when(repository.save(any(Sale.class))).thenReturn(saved);

    // Act
    Sale result = saleService.create(sampleCommand());

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals(PaymentMethod.CASH, result.paymentMethod());
    verify(repository).save(any(Sale.class));
    verify(auditLogger).record(eq("SALE"), eq(1L), eq(AuditAction.CREATE), isNull(), any());
  }

  @Test
  @DisplayName("CalculateTotalCorrectly_WhenCreateCalledWithDiscount")
  void shouldCalculateTotalCorrectlyWhenCreateCalledWithDiscount() {
    // Arrange
    ArgumentCaptor<Sale> captor = ArgumentCaptor.forClass(Sale.class);
    when(repository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

    // Act
    saleService.create(sampleCommand()); // items: qty=2, price=50 → subtotal=100, discount=10 → total=90

    // Assert
    Sale savedSale = captor.getValue();
    assertEquals(90.0, savedSale.totalAmount());
  }

  @Test
  @DisplayName("UseZeroDiscount_WhenCreateCalledWithNullDiscount")
  void shouldUseZeroDiscountWhenCreateCalledWithNullDiscount() {
    // Arrange
    SaleCommand command = new SaleCommand(1L, 2L, PaymentMethod.CARD, null, "Nota",
        List.of(new SaleItemCommand(10L, 1, 100.0)));
    ArgumentCaptor<Sale> captor = ArgumentCaptor.forClass(Sale.class);
    when(repository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

    // Act
    saleService.create(command);

    // Assert
    assertEquals(100.0, captor.getValue().totalAmount());
  }

  @Test
  @DisplayName("ReturnMultiplePagesIfNeeded_WhenListCalledWithLargeTotal")
  void shouldReturnMultiplePagesIfNeededWhenListCalledWithLargeTotal() {
    // Arrange
    when(repository.find(anyInt(), anyInt())).thenReturn(List.of(sampleSale(1L)));
    when(repository.count()).thenReturn(35L);

    // Act
    PagedResponse<Sale> result = saleService.list(1, 10);

    // Assert
    assertEquals(4, result.totalPages());
    assertTrue(result.hasNextPage());
  }
}
