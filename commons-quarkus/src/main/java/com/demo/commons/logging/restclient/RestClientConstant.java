package com.demo.commons.logging.restclient;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestClientConstant {
  public static final String METHOD = ".method";
  public static final String URI = ".uri";
  public static final String HEADERS = ".headers";
  public static final String BODY = ".body";
  public static final String STATUS = ".status";
}
