package io.github.miguelarmasabt.properties;

import io.github.miguelarmasabt.properties.error.ErrorProperties;
import io.github.miguelarmasabt.properties.rest.RestProperties;
import io.smallrye.config.WithDefault;

public interface ConfigurationBaseProperties {

  @WithDefault("BACKEND")
  ProjectType projectType();

  ErrorProperties error();

  RestProperties rest();
}