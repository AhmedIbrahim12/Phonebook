package com.jumia.customers.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jumia.customers.models.Customer;
import com.jumia.customers.services.CustomerService;
import com.jumia.customers.utils.PageUtils;

import io.swagger.v3.oas.annotations.Parameter;

@RestController
@CrossOrigin
@RequestMapping(path = "/customers")
public class CustomersController {

  private CustomerService customerService;

  @Autowired
  public CustomersController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @GetMapping
  public ResponseEntity<Page<Customer>> getCustomers(
      @RequestParam(name = "page", required = false, defaultValue = "0") int page,
      @RequestParam(name = "size", required = false, defaultValue = "10") int size,
      @RequestParam(name = "sortBy", required = false, defaultValue = "id") String sortBy,
      @RequestParam(name = "sortDirection", required = false, defaultValue = "ASC") String sortDirection,
      @Parameter(description = "Filter by country")
      @RequestParam(name = "country", required = false) String country,
      @Parameter(description = "Filter by validity")
      @RequestParam(name = "valid", required = false) Boolean valid) {
    Pageable pageable = PageUtils.createPageable(page, size, sortDirection, sortBy);
    return ResponseEntity.ok(customerService.getFilteredCustomers(pageable, country, valid));
  }

  @GetMapping(path = "/countries")
  public ResponseEntity<List<String>> getCountries() {
    return ResponseEntity.ok(customerService.getCountries());
  }
}
