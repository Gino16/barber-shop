package org.barbershop.sale.application.port.in;

import org.barbershop.sale.application.PagedResponse;
import org.barbershop.sale.application.SaleCommand;
import org.barbershop.sale.domain.Sale;
import java.util.Optional;

public interface SaleUseCase {
    PagedResponse<Sale> list(int page, int pageSize);
    Optional<Sale> findById(Long id);
    Sale create(SaleCommand command);
}
