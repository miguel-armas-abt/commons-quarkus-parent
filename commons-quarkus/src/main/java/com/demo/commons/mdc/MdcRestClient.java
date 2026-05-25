package com.demo.commons.mdc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MdcRestClient {

  METHOD("rest.client.method"),
  STATUS("rest.client.status"),
  URI("rest.client.uri");

  private final String label;

}
