package org.barbershop.sale.domain;

public record SaleItem(
    Long id,
    Long saleId,
    Long itemId,
    Integer quantity,
    Double unitPrice,
    Double subtotalAmount
) {

}
