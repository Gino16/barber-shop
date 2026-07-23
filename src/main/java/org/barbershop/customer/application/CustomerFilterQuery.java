package org.barbershop.customer.application;

public record CustomerFilterQuery(
    String search,
    int page,
    int pageSize
) {

  public int offset() {
    return (page - 1) * pageSize;
  }

  public CustomerFilterQuery withDefaults() {
    return new CustomerFilterQuery(
        search != null ? search.trim() : null,
        page > 0 ? page : 1,
        pageSize > 0 && pageSize <= 100 ? pageSize : 10
    );
  }
}
