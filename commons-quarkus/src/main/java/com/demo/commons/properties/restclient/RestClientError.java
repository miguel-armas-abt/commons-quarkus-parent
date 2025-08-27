package com.demo.commons.properties.restclient;

public interface RestClientError {

  String customCode();
  String message();
  Integer httpCode();
}