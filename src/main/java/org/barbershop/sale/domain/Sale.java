package org.barbershop.sale.domain;

import java.time.OffsetDateTime;
import java.util.List;

public record Sale(
    Long id,
    Long customerId,
    Long employeeId,
    PaymentMethod paymentMethod,
    Double totalAmount,
    Double discount,
    String notes,
    List<SaleItem> items,
    OffsetDateTime soldAt
) {
}
