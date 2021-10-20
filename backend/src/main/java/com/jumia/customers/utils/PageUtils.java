package com.jumia.customers.utils;

import static org.springframework.data.domain.Sort.Direction.valueOf;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(PageUtils.class);

  private PageUtils() {
  }

  public static Pageable createPageable(int page, int size, String sortDirection, String sortBy) {
    boolean invalidPage = page < 0 || size <= 0;
    boolean invalidSort = !Objects.equals(sortDirection, "ASC") && !Objects.equals(sortDirection, "DESC");
    if (invalidPage || invalidSort) {
      LOGGER.warn("Incorrect paging options");
      throw new IllegalArgumentException();
    }
    return PageRequest.of(page, size, Sort.by(valueOf(sortDirection), sortBy));
  }
}
