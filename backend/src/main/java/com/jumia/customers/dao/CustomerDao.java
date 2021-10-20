package com.jumia.customers.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.jumia.customers.models.CustomerEntity;

public interface CustomerDao {
  List<CustomerEntity> findCustomersByValidity(Pageable pageable, List<String> patterns, boolean valid);

  List<CustomerEntity> findCustomersByValidity(Pageable pageable, List<String> patterns, boolean valid, String countryCode);

  long countValidatedCustomers(List<String> patterns, boolean valid);

  long countValidatedCustomers(List<String> patterns, boolean valid, String countryCode);
}
