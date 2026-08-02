package org.barbershop.item.domain;

import java.time.OffsetDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record Item(Long id, String name, String description, Category category,
                   BigDecimal price, boolean active, OffsetDateTime createdAt) {

  public Item {
    if (price == null || price.signum() < 0) {
      throw new IllegalArgumentException("El precio debe ser mayor o igual a cero");
    }
    price = price.setScale(2, RoundingMode.HALF_UP);
  }

  public enum Category {SERVICE, PRODUCT}
}
