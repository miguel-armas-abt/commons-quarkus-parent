package com.demo.commons.properties;

import com.demo.commons.properties.error.ErrorProperties;
import com.demo.commons.properties.rest.RestProperties;
import io.smallrye.config.WithDefault;

public interface ConfigurationBaseProperties {

  @WithDefault("BACKEND")
  ProjectType projectType();

  ErrorProperties error();

  RestProperties rest();
}