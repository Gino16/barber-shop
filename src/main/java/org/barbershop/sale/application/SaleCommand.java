package org.barbershop.sale.application;

import java.util.List;
import java.math.BigDecimal;
import org.barbershop.sale.domain.PaymentMethod;

public record SaleCommand(
    Long customerId,
    Long employeeId,
    PaymentMethod paymentMethod,
    BigDecimal discount,
    String notes,
    List<SaleItemCommand> items
) {

}
