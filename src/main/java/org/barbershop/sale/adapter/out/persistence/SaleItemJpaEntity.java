package org.barbershop.sale.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "sale_items")
public class SaleItemJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(name = "sale_id", nullable = false)
  public Long saleId;

  @Column(name = "item_id", nullable = false)
  public Long itemId;

  @Column(nullable = false)
  public Integer quantity;

  @Column(name = "unit_price", nullable = false)
  public BigDecimal unitPrice;

  @Column(name = "subtotal_amount", nullable = false)
  public BigDecimal subtotalAmount;
}
