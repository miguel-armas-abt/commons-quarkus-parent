package com.demo.commons.properties.restclient;

import com.demo.commons.restclient.enums.ConcurrencyLevel;
import com.demo.commons.restclient.enums.TimeoutLevel;

public interface PerformanceTemplate {

  TimeoutLevel timeout();
  ConcurrencyLevel concurrency();
}