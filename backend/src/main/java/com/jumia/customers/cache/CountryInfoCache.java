package com.jumia.customers.cache;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumia.customers.models.CountryInfo;

/**
 * A cache for CountryInfo (name, code, validity rule) load from "countries.json" file
 */
@Component
public class CountryInfoCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(CountryInfoCache.class);
  private Map<String, CountryInfo> countryToInfoMap;
  private Map<String, String> codeToNameMap; // Entry <(code), country name>

  @PostConstruct
  private void init() {
    List<CountryInfo> countriesInfo = loadCountriesInfo();
    countryToInfoMap = countriesInfo.stream()
        .collect(Collectors.toMap(CountryInfo::getCountry, Function.identity()));
    codeToNameMap = countriesInfo.stream()
        .collect(Collectors.toMap(this::getCodeFromInfo, CountryInfo::getCountry));
  }

  private List<CountryInfo> loadCountriesInfo() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      InputStream inputStream = new ClassPathResource("countries.json").getInputStream();
      List<CountryInfo> countriesInfo = Arrays.asList(mapper.readValue(inputStream, CountryInfo[].class));
      LOGGER.info("Countries info loaded successfully");
      return countriesInfo;
    } catch (Exception e) {
      LOGGER.error("Error while loading countries info");
    }
    return new ArrayList<>();
  }

  public List<String> getSortedCountryNames() {
    return countryToInfoMap.keySet().stream()
        .sorted()
        .collect(Collectors.toList());
  }

  public String getCountryCode(String country) {
    if (!countryToInfoMap.containsKey(country)) {
      return "";
    }
    return getCodeFromInfo(countryToInfoMap.get(country));
  }

  private String getCodeFromInfo(CountryInfo info) {
    String countryCode = info.getCode();
    return "(" + countryCode.substring(1) + ")";
  }

  public String getCountryRegex(String country) {
    if (!countryToInfoMap.containsKey(country)) {
      return "";
    }
    return countryToInfoMap.get(country).getRegex();
  }

  public String getCountryByCode(String code) {
    return codeToNameMap.getOrDefault(code, "");
  }

  public List<String> getCountriesRegex() {
    return countryToInfoMap.values().stream()
        .map(CountryInfo::getRegex)
        .collect(Collectors.toList());
  }
}
