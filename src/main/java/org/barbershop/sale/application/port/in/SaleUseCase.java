package org.barbershop.sale.application.port.in;

import java.util.Optional;
import org.barbershop.common.pagination.PagedResponse;
import org.barbershop.sale.application.SaleCommand;
import org.barbershop.sale.domain.Sale;

public interface SaleUseCase {

  PagedResponse<Sale> list(int page, int pageSize);

  Optional<Sale> findById(Long id);

  Sale create(SaleCommand command);
}
