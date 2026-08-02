package org.barbershop.sale.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record SaleItem(
    Long id,
    Long saleId,
    Long itemId,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal subtotalAmount
) {
  public SaleItem {
    if (quantity == null || quantity <= 0 || unitPrice == null || unitPrice.signum() < 0
        || subtotalAmount == null || subtotalAmount.signum() < 0) {
      throw new IllegalArgumentException("La cantidad y los importes de la línea son inválidos");
    }
    unitPrice = unitPrice.setScale(2, RoundingMode.HALF_UP);
    subtotalAmount = subtotalAmount.setScale(2, RoundingMode.HALF_UP);
    if (unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP)
        .compareTo(subtotalAmount) != 0) {
      throw new IllegalArgumentException("El subtotal de la línea es inconsistente");
    }
  }
}
