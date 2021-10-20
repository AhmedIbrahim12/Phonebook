package com.jumia.customers.dao;

import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.jumia.customers.models.CustomerEntity;

@Repository
public class CustomerDaoImpl implements CustomerDao {

  private EntityManager em;

  @Autowired
  public CustomerDaoImpl(EntityManager em) {
    this.em = em;
  }

  @Override
  public List<CustomerEntity> findCustomersByValidity(Pageable pageable, List<String> patterns, boolean valid) {
    return findCustomersByValidity(pageable, patterns, valid, null);
  }

  @Override
  public List<CustomerEntity> findCustomersByValidity(Pageable pageable,
      List<String> patterns,
      boolean valid,
      String countryCode) {

    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<CustomerEntity> cq = cb.createQuery(CustomerEntity.class);
    Root<CustomerEntity> root = cq.from(CustomerEntity.class);
    Path phone = root.get("phone");

    Map<ParameterExpression, String> paramValueMap = new HashMap<>();
    Predicate matchingPredicate = addRegexMatching(cb, phone, paramValueMap, patterns, valid);
    addCountryFilter(cb, cq, phone, matchingPredicate, countryCode);
    cq.orderBy(toOrders(pageable.getSort(), root, cb));

    TypedQuery<CustomerEntity> query = em.createQuery(cq);
    setQueryParameters(query, paramValueMap);
    setPaginationInfo(query, pageable);
    return query.getResultList();
  }

  @Override
  public long countValidatedCustomers(List<String> patterns, boolean valid) {
    return countValidatedCustomers(patterns, valid, null);
  }

  @Override
  public long countValidatedCustomers(List<String> patterns, boolean valid, String countryCode) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<CustomerEntity> root = cq.from(CustomerEntity.class);
    Path phone = root.get("phone");

    Map<ParameterExpression, String> paramValueMap = new HashMap<>();
    Predicate matchingPredicate = addRegexMatching(cb, phone, paramValueMap, patterns, valid);
    addCountryFilter(cb, cq, phone, matchingPredicate, countryCode);
    cq.select(cb.count(root));

    TypedQuery<Long> query = em.createQuery(cq);
    setQueryParameters(query, paramValueMap);
    return query.getSingleResult();
  }

  private void addCountryFilter(CriteriaBuilder cb,
      CriteriaQuery cq,
      Path phone,
      Predicate matchingPredicate,
      String countryCode) {
    
    if (countryCode != null) {
      Predicate countryMatch = cb.like(phone, countryCode + "%");
      cq.where(cb.and(countryMatch, matchingPredicate));
    } else {
      cq.where(matchingPredicate);
    }
  }

  private Predicate addRegexMatching(CriteriaBuilder cb,
      Path phone,
      Map<ParameterExpression, String> paramValueMap,
      List<String> patterns,
      boolean valid) {

    int patternSize = patterns.size();
    Predicate[] predicates = new Predicate[patternSize];

    for (int i = 0; i < patternSize; i++) {
      ParameterExpression regexParam = cb.parameter(String.class);
      paramValueMap.put(regexParam, patterns.get(i));
      Predicate statusPredicate = cb.equal(cb.function("REGEXP", Boolean.class, phone, regexParam), valid);
      predicates[i] = statusPredicate;
    }

    return valid ? cb.or(predicates) : cb.and(predicates);
  }

  private void setQueryParameters(TypedQuery<?> query, Map<ParameterExpression, String> paramValueMap) {
    paramValueMap.entrySet().forEach(entry -> query.setParameter(entry.getKey(), entry.getValue()));
  }

  private void setPaginationInfo(TypedQuery<CustomerEntity> query, Pageable pageable) {
    Long offset = pageable.getOffset();
    query.setFirstResult(offset.intValue());
    query.setMaxResults(pageable.getPageSize());
  }
}
