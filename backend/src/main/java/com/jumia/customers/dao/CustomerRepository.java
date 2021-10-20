package com.jumia.customers.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import com.jumia.customers.models.CustomerEntity;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long>, JpaSpecificationExecutor, CustomerDao {

  Page<CustomerEntity> findByPhoneStartsWith(@Param("phone") String phone, Pageable pageable);

}
