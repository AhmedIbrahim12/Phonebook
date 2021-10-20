package com.jumia.customers.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jumia.customers.models.Customer;

public interface CustomerService {
  List<String> getCountries();

  Page<Customer> getFilteredCustomers(Pageable pageable, String country, Boolean valid);
}
