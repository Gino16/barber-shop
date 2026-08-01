package org.barbershop.common.pagination;

import java.util.List;

public record PagedResponse<T>(
    List<T> data,
    int page,
    int pageSize,
    long total,
    int totalPages,
    boolean hasNextPage
) {

  public PagedResponse(List<T> data, int page, int pageSize, long total) {
    this(data, page, pageSize, total, (int) Math.ceil((double) total / pageSize),
        (long) page * pageSize + pageSize < total);
  }
}
