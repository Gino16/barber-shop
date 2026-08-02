package org.barbershop.sale.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.math.BigDecimal;
import org.barbershop.sale.domain.PaymentMethod;
import org.barbershop.sale.domain.Sale;
import org.barbershop.sale.domain.SaleItem;

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
  public BigDecimal totalAmount;

  @Column(nullable = false)
  public BigDecimal discount = BigDecimal.ZERO;

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
