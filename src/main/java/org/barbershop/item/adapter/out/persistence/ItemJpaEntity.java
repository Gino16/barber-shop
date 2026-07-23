package org.barbershop.item.adapter.out.persistence;

import org.barbershop.item.domain.Item;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
  @Column(name = "is_active", nullable = false)
  public boolean active = true;
  @Column(name = "created_at", nullable = false)
  public LocalDateTime createdAt;

  public Item toDomain() {
    return new Item(id, name, description, category, active, createdAt.atOffset(ZoneOffset.UTC));
  }

  public static ItemJpaEntity fromDomain(Item item) {
    ItemJpaEntity entity = new ItemJpaEntity();
    entity.id = item.id();
    entity.name = item.name();
    entity.description = item.description();
    entity.category = item.category();
    entity.active = item.active();
    entity.createdAt = item.createdAt().toLocalDateTime();
    return entity;
  }
}
