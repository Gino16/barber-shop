package org.barbershop.item.adapter.in.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.barbershop.api.ItemsApi;
import org.barbershop.api.model.ItemRequest;
import org.barbershop.api.model.ItemResponse;
import org.barbershop.api.model.PaginatedItemsResponse;
import org.barbershop.api.model.PaginationResponse;
import org.barbershop.item.application.ItemCommand;
import org.barbershop.item.application.ItemFilterQuery;
import org.barbershop.common.pagination.PagedResponse;
import org.barbershop.item.application.port.in.ItemUseCase;
import org.barbershop.item.domain.Item;
import org.barbershop.item.domain.Item.Category;

@ApplicationScoped
public class ItemRestAdapter implements ItemsApi {

  private final ItemUseCase useCase;

  @Inject
  public ItemRestAdapter(ItemUseCase useCase) {
    this.useCase = useCase;
  }

  @Override
  public Response listItems(Integer page, Integer pageSize, String search, String category,
      Boolean active, String sortBy, String sortDirection) {
    ItemFilterQuery query = new ItemFilterQuery(
        search,
        category != null ? Category.valueOf(category) : null,
        active,
        page != null ? page : 1,
        pageSize != null ? pageSize : 10,
        sortBy,
        sortDirection
    );

    PagedResponse<Item> pagedResult = useCase.list(query.withDefaults());

    PaginatedItemsResponse response = PaginatedItemsResponse.builder()
        .data(pagedResult.data().stream().map(this::toModel).toList())
        .pagination(buildPaginationResponse(pagedResult))
        .build();

    return Response.ok(response).build();
  }

  @Override
  public Response getItem(Long id) {
    return useCase.findById(id).map(i -> Response.ok(toModel(i)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @Override
  public Response createItem(ItemRequest request) {
    return Response.status(Response.Status.CREATED)
        .entity(toModel(useCase.create(toCommand(request)))).build();
  }

  @Override
  public Response updateItem(Long id, ItemRequest request) {
    return useCase.update(id, toCommand(request))
        .map(i -> Response.ok(toModel(i)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  private ItemCommand toCommand(ItemRequest r) {
    return new ItemCommand(r.getName(), r.getDescription(),
        Item.Category.valueOf(r.getCategory().value()), r.getActive());
  }

  private ItemResponse toModel(Item i) {
    return ItemResponse.builder()
        .id(i.id()).name(i.name()).description(i.description())
        .category(ItemResponse.CategoryEnum.fromValue(i.category().name())).active(i.active())
        .createdAt(i.createdAt())
        .build();
  }

  private PaginationResponse buildPaginationResponse(PagedResponse<Item> pagedResult) {
    return PaginationResponse.builder()
        .page(pagedResult.page())
        .pageSize(pagedResult.pageSize())
        .total(pagedResult.total())
        .totalPages(pagedResult.totalPages())
        .hasNextPage(pagedResult.hasNextPage())
        .build();
  }
}
