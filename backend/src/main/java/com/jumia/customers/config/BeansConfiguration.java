package com.jumia.customers.config;

import javax.sql.DataSource;

import org.modelmapper.ModelMapper;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfiguration {

  @Bean
  public DataSource createDataSource() {
    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder.driverClassName("org.sqlite.JDBC");
    dataSourceBuilder.url("jdbc:sqlite::resource:sample.db");
    dataSourceBuilder.username("admin");
    dataSourceBuilder.password("admin");
    dataSourceBuilder.type(CustomDataSource.class);
    return dataSourceBuilder.build();
  }

  @Bean
  public ModelMapper createModelMapper() {
    return new ModelMapper();
  }
}
