package com.demo.commons.properties.restclient;

import java.util.Map;

public interface RequestTemplate {

  String endpoint();
  HeaderTemplate headers();
  Map<String, String> formData();
}
