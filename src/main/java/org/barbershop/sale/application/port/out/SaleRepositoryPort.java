package org.barbershop.sale.application.port.out;

import java.util.List;
import java.util.Optional;
import org.barbershop.sale.domain.Sale;

public interface SaleRepositoryPort {

  List<Sale> find(int offset, int pageSize);

  long count();

  Optional<Sale> findById(Long id);

  Sale save(Sale sale);
}
