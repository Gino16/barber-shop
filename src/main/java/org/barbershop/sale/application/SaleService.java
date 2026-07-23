package org.barbershop.sale.application;

import org.barbershop.sale.application.port.in.SaleUseCase;
import org.barbershop.sale.application.port.out.SaleRepositoryPort;
import org.barbershop.sale.domain.Sale;
import org.barbershop.sale.domain.SaleItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SaleService implements SaleUseCase {

  private final SaleRepositoryPort repository;

  @Inject
  public SaleService(SaleRepositoryPort repository) {
    this.repository = repository;
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
        .map(item -> new SaleItem(null, null, item.itemId(), item.quantity(), item.unitPrice()))
        .toList();
    
    Double totalAmount = calculateTotal(items, discount);
    
    return repository.save(new Sale(null, command.customerId(), command.employeeId(),
        command.paymentMethod(), totalAmount, discount, command.notes(), items,
        OffsetDateTime.now(ZoneOffset.UTC)));
  }

  private Double calculateTotal(List<SaleItem> items, Double discount) {
    Double subtotal = items.stream()
        .mapToDouble(item -> item.quantity() * item.unitPrice())
        .sum();
    return subtotal - discount;
  }
}
