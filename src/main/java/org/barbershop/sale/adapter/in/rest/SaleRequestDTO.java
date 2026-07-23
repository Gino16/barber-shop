package org.barbershop.sale.adapter.in.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleRequestDTO {
    private Long customerId;
    private Long employeeId;
    private String paymentMethod;
    private Double discount;
    private String notes;
    private List<SaleItemRequestDTO> items;
}
