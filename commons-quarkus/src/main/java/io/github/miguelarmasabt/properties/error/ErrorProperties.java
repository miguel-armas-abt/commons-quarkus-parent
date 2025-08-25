package io.github.miguelarmasabt.properties.error;

import io.smallrye.config.WithDefault;

import java.util.Map;

public interface ErrorProperties {

  @WithDefault("default")
  String defaultCode();

  Map<String, ErrorRule> rules();

  interface ErrorRule {
    @WithDefault("false")
    boolean exposeMessage();

    Integer httpCode();
  }
}