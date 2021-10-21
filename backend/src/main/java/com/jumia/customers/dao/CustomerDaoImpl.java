package com.jumia.customers.dao;

import static org.springframework.data.repository.support.PageableExecutionUtils.getPage;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.ParameterExpression;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.jumia.customers.models.CustomerEntity;

public class CustomerDaoImpl extends SimpleJpaRepository<CustomerEntity, Long> implements CustomerDao {

  @Autowired
  public CustomerDaoImpl(EntityManager em) {
    super(CustomerEntity.class, em);
  }

  @Override
  public Page<CustomerEntity> findCustomersByValidity(Pageable pageable, List<String> patterns, boolean valid) {
    return findCustomersByValidity(pageable, patterns, valid, null);
  }

  @Override
  public Page<CustomerEntity> findCustomersByValidity(Pageable pageable,
      List<String> patterns,
      boolean valid,
      String countryCode) {

    CustomersSpecification spec = new CustomersSpecification(countryCode, patterns, valid);
    TypedQuery<Long> count = getCountQuery(spec, CustomerEntity.class);
    setQueryParameters(count, spec.getParamValueMap());
    Long totalCount = count.getSingleResult();

    TypedQuery<CustomerEntity> query = getQuery(spec, pageable);
    setQueryParameters(query, spec.getParamValueMap());
    setPagingInfo(query, pageable);
    return getPage(query.getResultList(), pageable, () -> totalCount);
  }

  private void setQueryParameters(TypedQuery query, Map<ParameterExpression, String> paramValueMap) {
    paramValueMap.entrySet().forEach(entry -> query.setParameter(entry.getKey(), entry.getValue()));
    paramValueMap.clear();
  }

  private void setPagingInfo(TypedQuery query, Pageable pageable) {
    if (pageable.isPaged()) {
      query.setFirstResult((int) pageable.getOffset());
      query.setMaxResults(pageable.getPageSize());
    }
  }
}
