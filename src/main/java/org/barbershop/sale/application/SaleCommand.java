package org.barbershop.sale.application;

import java.util.List;
import org.barbershop.sale.domain.PaymentMethod;

public record SaleCommand(
    Long customerId,
    Long employeeId,
    PaymentMethod paymentMethod,
    Double discount,
    String notes,
    List<SaleItemCommand> items
) {

}
