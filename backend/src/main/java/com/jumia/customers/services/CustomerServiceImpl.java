package com.jumia.customers.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jumia.customers.cache.CountryInfoCache;
import com.jumia.customers.dao.CustomerRepository;
import com.jumia.customers.models.Customer;

@Service
public class CustomerServiceImpl implements CustomerService {

  private CountryInfoCache countryInfoCache;
  private CustomerRepository customerRepository;
  private CustomerConverter customerConverter;

  @Autowired
  public CustomerServiceImpl(CountryInfoCache countryInfoCache,
      CustomerRepository customerRepository,
      CustomerConverter customerConverter) {
    this.countryInfoCache = countryInfoCache;
    this.customerRepository = customerRepository;
    this.customerConverter = customerConverter;
  }

  @Override
  @Transactional(readOnly = true)
  public List<String> getCountries() {
    return countryInfoCache.getSortedCountryNames();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Customer> getFilteredCustomers(Pageable pageable, String country, Boolean valid) {

    if (country != null && valid == null) {
      return filterByCountry(pageable, country);
    } else if (country == null && valid != null) {
      return filterByValidity(pageable, valid);
    } else if (country != null && valid != null) {
      return filterByCountryAndValidity(pageable, country, valid);
    }

    return customerRepository.findAll(pageable)
        .map(customerConverter::convertToDto);
  }

  private Page<Customer> filterByCountry(Pageable pageable, String country) {
    String countryCode = countryInfoCache.getCountryCode(country);
    return customerRepository.findByPhoneStartsWith(countryCode, pageable)
        .map(entity -> customerConverter.convertToDto(entity, country));
  }

  private Page<Customer> filterByValidity(Pageable pageable, Boolean valid) {
    List<String> patterns = countryInfoCache.getCountriesRegex();
    return customerRepository.findCustomersByValidity(pageable, patterns, valid)
        .map(entity -> customerConverter.convertToDto(entity, valid));
  }

  private Page<Customer> filterByCountryAndValidity(Pageable pageable, String country, Boolean valid) {
    String countryCode = countryInfoCache.getCountryCode(country);
    List<String> patterns = Arrays.asList(countryInfoCache.getCountryRegex(country));
    return customerRepository.findCustomersByValidity(pageable, patterns, valid, countryCode)
        .map(entity -> customerConverter.convertToDto(entity, country, valid));
  }
}
