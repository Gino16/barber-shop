package org.barbershop.customer.application.port.out;

import java.util.List;
import java.util.Optional;
import org.barbershop.customer.application.CustomerFilterQuery;
import org.barbershop.customer.domain.Customer;

public interface CustomerRepositoryPort {

  List<Customer> find(CustomerFilterQuery query);

  long count(CustomerFilterQuery query);

  Optional<Customer> findById(Long id);

  Customer save(Customer customer);

  void delete(Long id);
}
