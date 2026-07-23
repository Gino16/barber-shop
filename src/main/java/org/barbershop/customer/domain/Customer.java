package org.barbershop.customer.domain;

import java.time.OffsetDateTime;

public record Customer(
    Long id,
    String name,
    String phone,
    String email,
    String address,
    OffsetDateTime createdAt
) {
}
