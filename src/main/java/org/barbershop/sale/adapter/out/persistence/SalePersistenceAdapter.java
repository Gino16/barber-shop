package org.barbershop.sale.adapter.out.persistence;

import org.barbershop.sale.application.port.out.SaleRepositoryPort;
import org.barbershop.sale.domain.Sale;
import org.barbershop.sale.domain.SaleItem;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class SalePersistenceAdapter implements SaleRepositoryPort {

  @Inject
  SalePanacheRepository repository;

  @Inject
  EntityManager em;

  @Override
  public List<Sale> find(int offset, int pageSize) {
    return repository.find("ORDER BY sold_at DESC")
        .range(offset, offset + pageSize - 1)
        .stream()
        .map(entity -> entity.toDomain(getSaleItems(entity.id)))
        .toList();
  }

  @Override
  public long count() {
    return repository.count();
  }

  @Override
  public Optional<Sale> findById(Long id) {
    Optional<SaleJpaEntity> entity = repository.find("id", id).firstResultOptional();
    return entity.map(e -> e.toDomain(getSaleItems(e.id)));
  }

  @Override
  public Sale save(Sale sale) {
    SaleJpaEntity entity =
        sale.id() == null
            ? SaleJpaEntity.fromDomain(sale)
            : findByIdEntity(sale.id());
    entity.customerId = sale.customerId();
    entity.employeeId = sale.employeeId();
    entity.paymentMethod = sale.paymentMethod();
    entity.totalAmount = sale.totalAmount();
    entity.discount = sale.discount();
    entity.notes = sale.notes();
    entity.soldAt = sale.soldAt().toLocalDateTime();

    if (entity.id == null) {
      repository.persist(entity);
    }

    if (sale.items() != null && !sale.items().isEmpty()) {
      saveSaleItems(entity.id, sale.items());
    }

    return entity.toDomain(sale.items() != null ? sale.items() : List.of());
  }

  private SaleJpaEntity findByIdEntity(Long id) {
    return repository.find("id", id).firstResult();
  }

  private List<SaleItem> getSaleItems(Long saleId) {
    return em.createQuery(
            "SELECT new org.barbershop.sale.domain.SaleItem(id, saleId, itemId, quantity, unitPrice) " +
            "FROM SaleItemJpaEntity WHERE saleId = ?1",
            SaleItem.class
        )
        .setParameter(1, saleId)
        .getResultList();
  }

  private void saveSaleItems(Long saleId, List<SaleItem> items) {
    em.createQuery("DELETE FROM SaleItemJpaEntity WHERE saleId = ?1")
        .setParameter(1, saleId)
        .executeUpdate();

    for (SaleItem item : items) {
      SaleItemJpaEntity saleItem = new SaleItemJpaEntity();
      saleItem.saleId = saleId;
      saleItem.itemId = item.itemId();
      saleItem.quantity = item.quantity();
      saleItem.unitPrice = item.unitPrice();
      em.persist(saleItem);
    }
  }
}
