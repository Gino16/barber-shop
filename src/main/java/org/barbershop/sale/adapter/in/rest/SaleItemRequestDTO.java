package org.barbershop.sale.adapter.in.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemRequestDTO {
    private Long itemId;
    private Integer quantity;
    private Double unitPrice;
}
