package org.barbershop.sale.adapter.in.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponseDTO {
    private Long id;
    private Long customerId;
    private Long employeeId;
    private String paymentMethod;
    private Double totalAmount;
    private Double discount;
    private String notes;
    private List<SaleItemResponseDTO> items;
    private OffsetDateTime soldAt;
}
