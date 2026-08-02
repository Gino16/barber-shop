package org.barbershop.customer.application.port.in;

import java.util.Optional;
import org.barbershop.common.pagination.PagedResponse;
import org.barbershop.customer.application.CustomerCommand;
import org.barbershop.customer.application.CustomerFilterQuery;
import org.barbershop.customer.domain.Customer;

public interface CustomerUseCase {

  PagedResponse<Customer> list(CustomerFilterQuery query);

  Optional<Customer> findById(Long id);

  Customer create(CustomerCommand command);

  Optional<Customer> update(Long id, CustomerCommand command);

  Optional<Void> delete(Long id);
}
