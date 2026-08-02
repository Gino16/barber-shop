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
import java.math.BigDecimal;
import org.barbershop.customer.application.port.out.CustomerRepositoryPort;
import org.barbershop.employee.application.port.out.EmployeeRepositoryPort;
import org.barbershop.item.application.port.out.ItemRepositoryPort;
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

  @Mock
  private CustomerRepositoryPort customerRepository;

  @Mock
  private EmployeeRepositoryPort employeeRepository;

  @Mock
  private ItemRepositoryPort itemRepository;

  @InjectMocks
  private SaleService saleService;

  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private Sale sampleSale(Long id) {
    SaleItem item = new SaleItem(1L, id, 10L, 2, BigDecimal.valueOf(50), BigDecimal.valueOf(100));
    return new Sale(id, 1L, 2L, PaymentMethod.CASH, BigDecimal.valueOf(90),
        BigDecimal.TEN, "Nota", List.of(item), NOW);
  }

  private SaleCommand sampleCommand() {
    SaleItemCommand itemCmd = new SaleItemCommand(10L, 2);
    return new SaleCommand(1L, 2L, PaymentMethod.CASH, BigDecimal.TEN, "Nota", List.of(itemCmd));
  }

  private void stubValidReferences() {
    when(customerRepository.findById(1L))
        .thenReturn(Optional.of(mock(org.barbershop.customer.domain.Customer.class)));
    when(employeeRepository.findById(2L))
        .thenReturn(Optional.of(mock(org.barbershop.employee.domain.Employee.class)));
  }

  private org.barbershop.item.domain.Item item(long id, String name, String price) {
    return new org.barbershop.item.domain.Item(id, name, null,
        org.barbershop.item.domain.Item.Category.SERVICE, new BigDecimal(price), true, NOW);
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
    when(customerRepository.findById(1L)).thenReturn(Optional.of(mock(org.barbershop.customer.domain.Customer.class)));
    when(employeeRepository.findById(2L)).thenReturn(Optional.of(mock(org.barbershop.employee.domain.Employee.class)));
    when(itemRepository.findById(10L)).thenReturn(Optional.of(
        new org.barbershop.item.domain.Item(10L, "Corte", null,
            org.barbershop.item.domain.Item.Category.SERVICE, BigDecimal.valueOf(50), true, NOW)));

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
    when(customerRepository.findById(1L)).thenReturn(Optional.of(mock(org.barbershop.customer.domain.Customer.class)));
    when(employeeRepository.findById(2L)).thenReturn(Optional.of(mock(org.barbershop.employee.domain.Employee.class)));
    when(itemRepository.findById(10L)).thenReturn(Optional.of(
        new org.barbershop.item.domain.Item(10L, "Corte", null,
            org.barbershop.item.domain.Item.Category.SERVICE, BigDecimal.valueOf(50), true, NOW)));
    when(repository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

    // Act
    saleService.create(sampleCommand()); // items: qty=2, price=50 → subtotal=100, discount=10 → total=90

    // Assert
    Sale savedSale = captor.getValue();
    assertEquals(BigDecimal.valueOf(90).setScale(2), savedSale.totalAmount());
  }

  @Test
  @DisplayName("UseZeroDiscount_WhenCreateCalledWithNullDiscount")
  void shouldUseZeroDiscountWhenCreateCalledWithNullDiscount() {
    // Arrange
    SaleCommand command = new SaleCommand(1L, 2L, PaymentMethod.CARD, null, "Nota",
        List.of(new SaleItemCommand(10L, 1)));
    when(customerRepository.findById(1L)).thenReturn(Optional.of(mock(org.barbershop.customer.domain.Customer.class)));
    when(employeeRepository.findById(2L)).thenReturn(Optional.of(mock(org.barbershop.employee.domain.Employee.class)));
    when(itemRepository.findById(10L)).thenReturn(Optional.of(
        new org.barbershop.item.domain.Item(10L, "Corte", null,
            org.barbershop.item.domain.Item.Category.SERVICE, BigDecimal.valueOf(100), true, NOW)));
    ArgumentCaptor<Sale> captor = ArgumentCaptor.forClass(Sale.class);
    when(repository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

    // Act
    saleService.create(command);

    // Assert
    assertEquals(BigDecimal.valueOf(100).setScale(2), captor.getValue().totalAmount());
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

  @Test
  void shouldUseCatalogPriceAndIgnoreAnyClientPrice() {
    stubValidReferences();
    when(itemRepository.findById(10L)).thenReturn(Optional.of(item(10L, "Corte", "125.00")));
    ArgumentCaptor<Sale> captor = ArgumentCaptor.forClass(Sale.class);
    when(repository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

    saleService.create(new SaleCommand(1L, 2L, PaymentMethod.CASH, BigDecimal.ZERO, null,
        List.of(new SaleItemCommand(10L, 2))));

    Sale saved = captor.getValue();
    assertEquals(new BigDecimal("125.00"), saved.items().getFirst().unitPrice());
    assertEquals(new BigDecimal("250.00"), saved.items().getFirst().subtotalAmount());
    assertEquals(new BigDecimal("250.00"), saved.totalAmount());
  }

  @Test
  void shouldRejectUnknownItemAndLeaveRepositoryUntouched() {
    stubValidReferences();
    when(itemRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> saleService.create(
        new SaleCommand(1L, 2L, PaymentMethod.CASH, BigDecimal.ZERO, null,
            List.of(new SaleItemCommand(99L, 1)))));
    verify(repository, never()).save(any());
  }

  @Test
  void shouldRejectInactiveItem() {
    stubValidReferences();
    when(itemRepository.findById(10L)).thenReturn(Optional.of(
        new org.barbershop.item.domain.Item(10L, "Corte", null,
            org.barbershop.item.domain.Item.Category.SERVICE, new BigDecimal("50.00"), false, NOW)));

    assertThrows(IllegalArgumentException.class, () -> saleService.create(sampleCommand()));
    verify(repository, never()).save(any());
  }

  @Test
  void shouldRejectInvalidQuantity() {
    stubValidReferences();

    assertThrows(IllegalArgumentException.class, () -> saleService.create(
        new SaleCommand(1L, 2L, PaymentMethod.CASH, BigDecimal.ZERO, null,
            List.of(new SaleItemCommand(10L, 0)))));
    verifyNoInteractions(itemRepository);
    verify(repository, never()).save(any());
  }

  @Test
  void shouldRejectNegativeAndExcessiveDiscount() {
    stubValidReferences();
    when(itemRepository.findById(10L)).thenReturn(Optional.of(item(10L, "Corte", "50.00")));

    assertThrows(IllegalArgumentException.class, () -> saleService.create(
        new SaleCommand(1L, 2L, PaymentMethod.CASH, new BigDecimal("-1"), null,
            List.of(new SaleItemCommand(10L, 1)))));
    assertThrows(IllegalArgumentException.class, () -> saleService.create(
        new SaleCommand(1L, 2L, PaymentMethod.CASH, new BigDecimal("50.01"), null,
            List.of(new SaleItemCommand(10L, 1)))));
    verify(repository, never()).save(any());
  }

  @Test
  void shouldCalculateMultipleLinesWithMoneyRounding() {
    stubValidReferences();
    when(itemRepository.findById(10L)).thenReturn(Optional.of(item(10L, "Corte", "10.005")));
    when(itemRepository.findById(11L)).thenReturn(Optional.of(item(11L, "Cera", "2.335")));
    ArgumentCaptor<Sale> captor = ArgumentCaptor.forClass(Sale.class);
    when(repository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

    saleService.create(new SaleCommand(1L, 2L, PaymentMethod.CARD, new BigDecimal("1.00"), null,
        List.of(new SaleItemCommand(10L, 2), new SaleItemCommand(11L, 3))));

    Sale saved = captor.getValue();
    assertEquals(new BigDecimal("20.02"), saved.items().get(0).subtotalAmount());
    assertEquals(new BigDecimal("7.02"), saved.items().get(1).subtotalAmount());
    assertEquals(new BigDecimal("26.04"), saved.totalAmount());
  }

  @Test
  void shouldPreserveHistoricalUnitPriceWhenCatalogPriceChanges() {
    stubValidReferences();
    when(itemRepository.findById(10L))
        .thenReturn(Optional.of(item(10L, "Corte", "50.00")))
        .thenReturn(Optional.of(item(10L, "Corte", "75.00")));
    when(repository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Sale first = saleService.create(new SaleCommand(1L, 2L, PaymentMethod.CASH, null, null,
        List.of(new SaleItemCommand(10L, 1))));
    Sale second = saleService.create(new SaleCommand(1L, 2L, PaymentMethod.CASH, null, null,
        List.of(new SaleItemCommand(10L, 1))));

    assertEquals(new BigDecimal("50.00"), first.items().getFirst().unitPrice());
    assertEquals(new BigDecimal("75.00"), second.items().getFirst().unitPrice());
  }
}
