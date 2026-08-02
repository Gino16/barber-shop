package org.barbershop.item.adapter.out.persistence;

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
import java.math.BigDecimal;
import org.barbershop.item.domain.Item;

@Entity
@Table(name = "items")
public class ItemJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;
  @Column(nullable = false, length = 100)
  public String name;
  public String description;
  @Enumerated(EnumType.STRING)
  public Item.Category category;
  @Column(nullable = false, precision = 19, scale = 2)
  public BigDecimal price;
  @Column(name = "is_active", nullable = false)
  public boolean active = true;
  @Column(name = "created_at", nullable = false)
  public LocalDateTime createdAt;

  public Item toDomain() {
    return new Item(id, name, description, category, price, active,
        createdAt.atOffset(ZoneOffset.UTC));
  }

  public static ItemJpaEntity fromDomain(Item item) {
    ItemJpaEntity entity = new ItemJpaEntity();
    entity.id = item.id();
    entity.name = item.name();
    entity.description = item.description();
    entity.category = item.category();
    entity.price = item.price();
    entity.active = item.active();
    entity.createdAt = item.createdAt().toLocalDateTime();
    return entity;
  }
}
