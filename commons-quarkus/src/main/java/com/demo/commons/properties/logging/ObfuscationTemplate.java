package com.demo.commons.properties.logging;

import java.util.Set;

public interface ObfuscationTemplate {
  Set<String> bodyFields();
  Set<String> headers();
}
