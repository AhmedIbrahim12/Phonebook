package com.jumia.customers.services;

import java.util.regex.Pattern;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jumia.customers.cache.CountryInfoCache;
import com.jumia.customers.models.Customer;
import com.jumia.customers.models.CustomerEntity;

/**
 * Converts the CustomerEntity to its DTO mapping
 * having calculated fields not mapped in the database (country and validity)
 */
@Component
public class CustomerConverter {

  private final ModelMapper modelMapper = new ModelMapper();
  private final CountryInfoCache countryInfoCache;

  @Autowired
  public CustomerConverter(CountryInfoCache countryInfoCache) {
    this.countryInfoCache = countryInfoCache;
  }

  public Customer convertToDto(CustomerEntity customerEntity) {
    Customer customerDTO = modelMapper.map(customerEntity, Customer.class);
    String phoneCode = customerEntity.getPhone().substring(0, 5);
    String countryName = countryInfoCache.getCountryByCode(phoneCode);
    String regex = countryInfoCache.getCountryRegex(countryName);
    customerDTO.setCountryName(countryName);
    customerDTO.setValid(Pattern.matches(regex, customerEntity.getPhone()));
    return customerDTO;
  }

  public Customer convertToDto(CustomerEntity customerEntity, String countryName) {
    Customer customerDTO = modelMapper.map(customerEntity, Customer.class);
    String regex = countryInfoCache.getCountryRegex(countryName);
    customerDTO.setCountryName(countryName);
    customerDTO.setValid(Pattern.matches(regex, customerEntity.getPhone()));
    return customerDTO;
  }

  public Customer convertToDto(CustomerEntity customerEntity, Boolean valid) {
    Customer customerDTO = modelMapper.map(customerEntity, Customer.class);
    String phoneCode = customerEntity.getPhone().substring(1, 4);
    String countryName = countryInfoCache.getCountryByCode(phoneCode);
    customerDTO.setCountryName(countryName);
    customerDTO.setValid(valid);
    return customerDTO;
  }

  public Customer convertToDto(CustomerEntity customerEntity, String countryName, Boolean valid) {
    Customer customerDTO = modelMapper.map(customerEntity, Customer.class);
    customerDTO.setCountryName(countryName);
    customerDTO.setValid(valid);
    return customerDTO;
  }
}
