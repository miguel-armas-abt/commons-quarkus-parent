package com.demo.commons.properties.rest;

import java.util.Map;

public interface RestClient {

  Map<String, Error> error();

  interface Error {
    String customCode();

    String message();

    Integer httpCode();
  }

}
