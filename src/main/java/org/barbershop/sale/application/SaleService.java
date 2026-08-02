package org.barbershop.sale.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.barbershop.audit.application.AuditLogger;
import org.barbershop.audit.domain.AuditAction;
import org.barbershop.common.pagination.PagedResponse;
import org.barbershop.sale.application.port.in.SaleUseCase;
import org.barbershop.sale.application.port.out.SaleRepositoryPort;
import org.barbershop.sale.domain.Sale;
import org.barbershop.sale.domain.SaleItem;

@ApplicationScoped
public class SaleService implements SaleUseCase {

  private final SaleRepositoryPort repository;
  private final AuditLogger auditLogger;

  @Inject
  public SaleService(SaleRepositoryPort repository, AuditLogger auditLogger) {
    this.repository = repository;
    this.auditLogger = auditLogger;
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
    Double discount = command.discount() != null ? command.discount() : 0.0;

    List<SaleItem> items = command.items().stream()
        .map(item -> new SaleItem(null, null, item.itemId(), item.quantity(), item.unitPrice(),
            item.quantity() * item.unitPrice()))
        .toList();

    Double totalAmount = calculateTotal(items, discount);

    Sale created = repository.save(new Sale(null, command.customerId(), command.employeeId(),
        command.paymentMethod(), totalAmount, discount, command.notes(), items,
        OffsetDateTime.now(ZoneOffset.UTC)));
    auditLogger.record("SALE", created.id(), AuditAction.CREATE, null, values(created));
    return created;
  }

  private Double calculateTotal(List<SaleItem> items, Double discount) {
    Double subtotal = items.stream()
        .mapToDouble(item -> item.quantity() * item.unitPrice())
        .sum();
    return subtotal - discount;
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
