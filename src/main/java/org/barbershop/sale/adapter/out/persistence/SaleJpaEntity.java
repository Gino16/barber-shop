package org.barbershop.sale.adapter.out.persistence;

import org.barbershop.sale.domain.Sale;
import org.barbershop.sale.domain.SaleItem;
import org.barbershop.sale.domain.PaymentMethod;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Entity
@Table(name = "sales")
public class SaleJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(name = "customer_id", nullable = false)
  public Long customerId;

  @Column(name = "employee_id", nullable = false)
  public Long employeeId;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_method", nullable = false)
  public PaymentMethod paymentMethod;

  @Column(name = "total_amount", nullable = false)
  public Double totalAmount;

  @Column(nullable = false)
  public Double discount = 0.0;

  @Column(columnDefinition = "TEXT")
  public String notes;

  @Column(name = "sold_at", nullable = false)
  public LocalDateTime soldAt;

  public Sale toDomain(List<SaleItem> items) {
    return new Sale(
        id,
        customerId,
        employeeId,
        paymentMethod,
        totalAmount,
        discount,
        notes,
        items,
        soldAt.atOffset(ZoneOffset.UTC)
    );
  }

  public static SaleJpaEntity fromDomain(Sale sale) {
    SaleJpaEntity entity = new SaleJpaEntity();
    entity.id = sale.id();
    entity.customerId = sale.customerId();
    entity.employeeId = sale.employeeId();
    entity.paymentMethod = sale.paymentMethod();
    entity.totalAmount = sale.totalAmount();
    entity.discount = sale.discount();
    entity.notes = sale.notes();
    entity.soldAt = sale.soldAt().toLocalDateTime();
    return entity;
  }
}
