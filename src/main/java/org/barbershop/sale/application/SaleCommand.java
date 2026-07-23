package org.barbershop.sale.application;

import org.barbershop.sale.domain.PaymentMethod;
import java.util.List;

public record SaleCommand(
    Long customerId,
    Long employeeId,
    PaymentMethod paymentMethod,
    Double discount,
    String notes,
    List<SaleItemCommand> items
) {
}
