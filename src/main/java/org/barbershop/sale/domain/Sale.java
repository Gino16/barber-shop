package org.barbershop.sale.domain;

import java.time.OffsetDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public record Sale(
    Long id,
    Long customerId,
    Long employeeId,
    PaymentMethod paymentMethod,
    BigDecimal totalAmount,
    BigDecimal discount,
    String notes,
    List<SaleItem> items,
    OffsetDateTime soldAt
) {
  public Sale {
    if (totalAmount == null || totalAmount.signum() < 0
        || discount == null || discount.signum() < 0) {
      throw new IllegalArgumentException("Los importes de la venta no pueden ser negativos");
    }
    totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
    discount = discount.setScale(2, RoundingMode.HALF_UP);
    if (items != null && !items.isEmpty()) {
      BigDecimal subtotal = items.stream()
          .map(SaleItem::subtotalAmount)
          .reduce(BigDecimal.ZERO, BigDecimal::add)
          .setScale(2, RoundingMode.HALF_UP);
      if (totalAmount.add(discount).compareTo(subtotal) != 0) {
        throw new IllegalArgumentException("El total de la venta es inconsistente");
      }
    }
  }
}
