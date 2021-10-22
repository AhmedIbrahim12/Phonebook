package com.jumia.customers;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.jumia.customers.dao.CustomerRepository;
import com.jumia.customers.models.Customer;
import com.jumia.customers.models.CustomerEntity;
import com.jumia.customers.services.CustomerService;

import config.DatabaseTestConfiguration;

/**
 * This test runs on a sqlite db similar to the one used in the app
 * to ensure the correct behaviour of the registered "REGEXP" function
 * in addition to the service logic.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = { DatabaseTestConfiguration.class },
    loader = AnnotationConfigContextLoader.class)
public class CustomersServiceTest {

  @Autowired
  private CustomerService customerService;
  @Autowired
  private CustomerRepository repository;

  @Before
  public void setUp() {
    // Initializing some customers data to test validity and filtering
    repository.save(new CustomerEntity("Valid Cameroon", "(237) 697151594"));
    repository.save(new CustomerEntity("Valid Morocco", "(212) 698054317"));
    repository.save(new CustomerEntity("Valid Uganda", "(256) 775069443"));
    repository.save(new CustomerEntity("Invalid Uganda", "(256) 7503O6263"));
    repository.save(new CustomerEntity("Invalid Uganda 2", "(256) 7505O6263"));
    repository.save(new CustomerEntity("Invalid Mozambique", "(258) 84330678235"));
    repository.save(new CustomerEntity("Invalid Ethiopia", "(251) 9119454961"));
  }

  @After
  public void onTearDown() {
    repository.deleteAll();
  }

  @Test
  public void testFindAll() {
    // Given
    // Seven customers were defined

    // When
    // Fetching unpaged and unfiltered
    Pageable pageable = Pageable.unpaged();
    Page<Customer> page = customerService.getFilteredCustomers(pageable, null, null);

    // Then
    // All seven are returned
    Assert.assertEquals(7, page.getTotalElements());
  }

  @Test
  public void testPagination() {
    // Given
    // Seven customers were defined

    // When
    // Fetching unfiltered and paged (page number = 0, size = 5)
    Pageable pageable = PageRequest.of(0, 5);
    Page<Customer> page = customerService.getFilteredCustomers(pageable, null, null);

    // Then
    // 5 elements are returned
    Assert.assertEquals(7, page.getTotalElements());
    Assert.assertEquals(2, page.getTotalPages());
    Assert.assertEquals(5, page.getNumberOfElements());

    // When
    // Fetching unfiltered and paged (page number = 1, size = 5)
    pageable = PageRequest.of(1, 5);
    page = customerService.getFilteredCustomers(pageable, null, null);

    // Then
    // 2 elements are returned
    Assert.assertEquals(7, page.getTotalElements());
    Assert.assertEquals(2, page.getTotalPages());
    Assert.assertEquals(2, page.getNumberOfElements());
  }

  @Test
  public void testFilterByValidity() {
    // Given
    // Seven customers were defined, 3 of them are valid

    // When
    // Filtering by validity = true
    Pageable pageable = Pageable.unpaged();
    Page<Customer> page = customerService.getFilteredCustomers(pageable, null, true);

    // Then
    // 3 elements are returned, with status valid
    Assert.assertEquals(3, page.getTotalElements());
    page.getContent().forEach(customer -> Assert.assertEquals(true, customer.isValid()));

    // When
    // Filtering by validity = false
    page = customerService.getFilteredCustomers(pageable, null, false);

    // Then
    // 4 elements are returned, with status invalid
    Assert.assertEquals(4, page.getTotalElements());
    page.getContent().forEach(customer -> Assert.assertEquals(false, customer.isValid()));
  }

  @Test
  public void testFilterByCountry() {
    // Given
    // Seven customers were defined, 3 of them are in Uganda

    // When
    // Filtering by country = Uganda
    Pageable pageable = Pageable.unpaged();
    Page<Customer> page = customerService.getFilteredCustomers(pageable, "Uganda", null);

    // Then
    // 3 elements are returned, with country set to Uganda
    Assert.assertEquals(3, page.getTotalElements());
    page.getContent().forEach(customer -> Assert.assertEquals("Uganda", customer.getCountryName()));
  }

  @Test
  public void testFilterByCountryAndValidity() {
    // Given
    // 3 customers are in Uganda, 2 have invalid phone numbers and one has a valid number

    // When
    // Filtering by country = Uganda, valid = false
    Pageable pageable = Pageable.unpaged();
    Page<Customer> page = customerService.getFilteredCustomers(pageable, "Uganda", false);

    // Then
    // 2 elements are returned country = Uganda, validity = false
    Assert.assertEquals(2, page.getTotalElements());
    page.getContent().forEach(customer -> {
      Assert.assertEquals("Uganda", customer.getCountryName());
      Assert.assertEquals(false, customer.isValid());
    });

    // When
    // Filtering by country = Uganda, valid = true
    page = customerService.getFilteredCustomers(pageable, "Uganda", true);

    // Then
    // 1 element is returned country = Uganda, validity = true
    Assert.assertEquals(1, page.getTotalElements());
    page.getContent().forEach(customer -> {
      Assert.assertEquals("Uganda", customer.getCountryName());
      Assert.assertEquals(true, customer.isValid());
    });
  }

  @Test
  public void testSortingByNameFilteredByCountry() {
    // Given
    // Seven customers were defined, 3 of them are in Uganda

    // When
    // Filtering by country = Uganda, Sorted by name ascendingly
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
    Page<Customer> page = customerService.getFilteredCustomers(pageable, "Uganda", null);

    // Then
    // 3 elements are returned, with country set to Uganda and sorted by name
    Assert.assertEquals(3, page.getTotalElements());
    List<Customer> content = page.getContent();
    Assert.assertEquals(content.get(0).getName(), "Invalid Uganda");
    Assert.assertEquals(content.get(1).getName(), "Invalid Uganda 2");
    Assert.assertEquals(content.get(2).getName(), "Valid Uganda");
  }
}
