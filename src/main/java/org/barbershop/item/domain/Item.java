package org.barbershop.item.domain;

import java.time.OffsetDateTime;

public record Item(Long id, String name, String description, Category category,
                   boolean active, OffsetDateTime createdAt) {

  public enum Category {SERVICE, PRODUCT}
}
