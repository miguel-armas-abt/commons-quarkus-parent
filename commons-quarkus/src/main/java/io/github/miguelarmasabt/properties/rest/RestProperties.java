package io.github.miguelarmasabt.properties.rest;

import io.smallrye.config.WithDefault;

import java.util.Map;
import java.util.Set;

public interface RestProperties {

  Map<String, RestClient> client();

  Obfuscation obfuscation();

  Logging logging();

  interface Obfuscation {

    @WithDefault("userEmail")
    Set<String> bodyFields();

    @WithDefault("Authorization")
    Set<String> headers();
  }

  interface Logging {

    @WithDefault("262144")
    Long maxResponseBodyBytes();

    @WithDefault("true")
    Boolean showResponseBodyIfChunked();
  }
}
