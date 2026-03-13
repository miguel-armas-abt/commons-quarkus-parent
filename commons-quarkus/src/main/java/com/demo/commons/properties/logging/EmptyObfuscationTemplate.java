package com.demo.commons.properties.logging;

import java.util.Set;

public class EmptyObfuscationTemplate implements ObfuscationTemplate {

  @Override
  public Set<String> bodyFields() {
    return Set.of();
  }

  @Override
  public Set<String> headers() {
    return Set.of();
  }
}
