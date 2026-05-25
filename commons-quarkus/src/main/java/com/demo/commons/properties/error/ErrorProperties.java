package com.demo.commons.properties.error;

import io.smallrye.config.WithDefault;

import java.util.Map;

public interface ErrorProperties {

  @WithDefault("No hemos podido realizar tu operación. Estamos trabajando para solucionar el inconveniente.")
  String defaultMessage();

  @WithDefault("Default")
  String defaultCode();

  Map<String, String> messages();
}
