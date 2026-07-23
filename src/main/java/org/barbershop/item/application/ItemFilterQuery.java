package org.barbershop.item.application;

import org.barbershop.item.domain.Item.Category;

public record ItemFilterQuery(
    String search,
    Category category,
    Boolean active,
    int page,
    int pageSize,
    String sortBy,
    String sortDirection
) {

  public int offset() {
    return (page - 1) * pageSize;
  }

  public ItemFilterQuery withDefaults() {
    return new ItemFilterQuery(
        search != null ? search.trim() : null,
        category,
        active,
        page > 0 ? page : 1,
        pageSize > 0 && pageSize <= 100 ? pageSize : 10,
        sortBy != null && !sortBy.isBlank() ? sortBy : "id",
        sortDirection != null && sortDirection.equalsIgnoreCase("desc") ? "desc" : "asc"
    );
  }
}
