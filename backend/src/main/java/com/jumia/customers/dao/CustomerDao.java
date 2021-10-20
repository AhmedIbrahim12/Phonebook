package com.jumia.customers.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jumia.customers.models.CustomerEntity;

public interface CustomerDao {
  Page<CustomerEntity> findCustomersByValidity(Pageable pageable, List<String> patterns, boolean valid);

  Page<CustomerEntity> findCustomersByValidity(Pageable pageable, List<String> patterns, boolean valid, String countryCode);
}
