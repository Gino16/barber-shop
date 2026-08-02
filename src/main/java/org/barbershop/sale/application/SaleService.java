package org.barbershop.sale.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.barbershop.audit.application.AuditLogger;
import org.barbershop.audit.domain.AuditAction;
import org.barbershop.common.pagination.PagedResponse;
import org.barbershop.sale.application.port.in.SaleUseCase;
import org.barbershop.sale.application.port.out.SaleRepositoryPort;
import org.barbershop.customer.application.port.out.CustomerRepositoryPort;
import org.barbershop.employee.application.port.out.EmployeeRepositoryPort;
import org.barbershop.item.application.port.out.ItemRepositoryPort;
import org.barbershop.item.domain.Item;
import org.barbershop.sale.domain.Sale;
import org.barbershop.sale.domain.SaleItem;

@ApplicationScoped
public class SaleService implements SaleUseCase {

  private final SaleRepositoryPort repository;
  private final AuditLogger auditLogger;
  private final CustomerRepositoryPort customerRepository;
  private final EmployeeRepositoryPort employeeRepository;
  private final ItemRepositoryPort itemRepository;

  @Inject
  public SaleService(SaleRepositoryPort repository, AuditLogger auditLogger,
      CustomerRepositoryPort customerRepository, EmployeeRepositoryPort employeeRepository,
      ItemRepositoryPort itemRepository) {
    this.repository = repository;
    this.auditLogger = auditLogger;
    this.customerRepository = customerRepository;
    this.employeeRepository = employeeRepository;
    this.itemRepository = itemRepository;
  }

  @Override
  public PagedResponse<Sale> list(int page, int pageSize) {
    page = page > 0 ? page : 1;
    pageSize = pageSize > 0 && pageSize <= 100 ? pageSize : 10;
    int offset = (page - 1) * pageSize;

    var sales = repository.find(offset, pageSize);
    long total = repository.count();
    return new PagedResponse<>(sales, page, pageSize, total);
  }

  @Override
  public Optional<Sale> findById(Long id) {
    return repository.findById(id);
  }

  @Override
  public Sale create(SaleCommand command) {
    validateReferences(command);
    if (command.items() == null || command.items().isEmpty()) {
      throw new IllegalArgumentException("La venta debe contener al menos un ítem");
    }

    BigDecimal discount = normalize(command.discount() != null ? command.discount() : BigDecimal.ZERO);
    List<SaleItem> items = command.items().stream()
        .map(this::calculateItem)
        .toList();

    BigDecimal subtotal = items.stream()
        .map(SaleItem::subtotalAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);
    if (discount.signum() < 0 || discount.compareTo(subtotal) > 0) {
      throw new IllegalArgumentException("El descuento es inválido");
    }
    BigDecimal totalAmount = subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);

    Sale created = repository.save(new Sale(null, command.customerId(), command.employeeId(),
        command.paymentMethod(), totalAmount, discount, command.notes(), items,
        OffsetDateTime.now(ZoneOffset.UTC)));
    auditLogger.record("SALE", created.id(), AuditAction.CREATE, null, values(created));
    return created;
  }

  private void validateReferences(SaleCommand command) {
    if (command.customerId() == null || customerRepository.findById(command.customerId()).isEmpty()) {
      throw new IllegalArgumentException("El cliente no existe");
    }
    if (command.employeeId() == null || employeeRepository.findById(command.employeeId()).isEmpty()) {
      throw new IllegalArgumentException("El empleado no existe");
    }
  }

  private SaleItem calculateItem(SaleItemCommand command) {
    if (command == null || command.itemId() == null) {
      throw new IllegalArgumentException("El ítem es obligatorio");
    }
    if (command.quantity() == null || command.quantity() <= 0) {
      throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
    }
    Item item = itemRepository.findById(command.itemId())
        .orElseThrow(() -> new IllegalArgumentException("El ítem no existe"));
    if (!item.active()) {
      throw new IllegalArgumentException("El ítem está inactivo");
    }
    BigDecimal unitPrice = normalize(item.price());
    BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(command.quantity()))
        .setScale(2, RoundingMode.HALF_UP);
    return new SaleItem(null, null, item.id(), command.quantity(), unitPrice, subtotal);
  }

  private BigDecimal normalize(BigDecimal amount) {
    if (amount == null) {
      throw new IllegalArgumentException("El importe es obligatorio");
    }
    return amount.setScale(2, RoundingMode.HALF_UP);
  }

  private Map<String, Object> values(Sale sale) {
    Map<String, Object> values = new LinkedHashMap<>();
    values.put("id", sale.id());
    values.put("customerId", sale.customerId());
    values.put("employeeId", sale.employeeId());
    values.put("paymentMethod", sale.paymentMethod());
    values.put("totalAmount", sale.totalAmount());
    values.put("discount", sale.discount());
    values.put("notes", sale.notes());
    values.put("items", sale.items().stream().map(this::itemValues).toList());
    values.put("soldAt", sale.soldAt());
    return values;
  }

  private Map<String, Object> itemValues(SaleItem item) {
    Map<String, Object> values = new LinkedHashMap<>();
    values.put("id", item.id());
    values.put("saleId", item.saleId());
    values.put("itemId", item.itemId());
    values.put("quantity", item.quantity());
    values.put("unitPrice", item.unitPrice());
    values.put("subtotalAmount", item.subtotalAmount());
    return values;
  }
}
