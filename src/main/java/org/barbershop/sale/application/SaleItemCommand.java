package org.barbershop.sale.application;

public record SaleItemCommand(Long itemId, Integer quantity, Double unitPrice) {

}
