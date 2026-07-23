package org.barbershop.customer.application.port.in;

import org.barbershop.customer.application.CustomerCommand;
import org.barbershop.customer.application.CustomerFilterQuery;
import org.barbershop.customer.application.PagedResponse;
import org.barbershop.customer.domain.Customer;
import java.util.Optional;

public interface CustomerUseCase {
    PagedResponse<Customer> list(CustomerFilterQuery query);
    Optional<Customer> findById(Long id);
    Customer create(CustomerCommand command);
    Optional<Customer> update(Long id, CustomerCommand command);
    Optional<Void> delete(Long id);
}
