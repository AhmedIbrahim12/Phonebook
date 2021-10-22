package com.jumia.customers.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.jumia.customers.models.CustomerEntity;

/**
 * A JPA Specification to find customers filtered either by validity or both validity & country
 */
public class CustomersSpecification implements Specification<CustomerEntity> {

  private String countryCode;
  private List<String> patterns;
  private boolean valid;
  private Map<ParameterExpression, String> paramValueMap = new HashMap<>();

  public CustomersSpecification(String countryCode, List<String> patterns, boolean valid) {
    this.patterns = patterns;
    this.valid = valid;
    this.countryCode = countryCode;
  }

  @Override
  public Predicate toPredicate(Root root, CriteriaQuery cq, CriteriaBuilder cb) {
    Path phone = root.get("phone");
    Predicate matchingPredicate = addRegexMatching(cb, phone);
    if (countryCode == null) {
      return matchingPredicate;
    }

    Predicate countryMatch = cb.like(phone, countryCode + "%");
    return cb.and(countryMatch, matchingPredicate);
  }

  private Predicate addRegexMatching(CriteriaBuilder cb, Path phone) {
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

  public Map<ParameterExpression, String> getParamValueMap() {
    return paramValueMap;
  }
}
