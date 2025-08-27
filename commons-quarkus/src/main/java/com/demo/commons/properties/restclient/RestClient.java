package com.demo.commons.properties.restclient;

import java.util.Map;

public interface RestClient {

  PerformanceTemplate performance();
  RequestTemplate request();
  Map<String, RestClientError> errors();
}
