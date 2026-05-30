package io.github.miguelarmasabt.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import java.util.Map;
import java.util.Optional;

@ConfigMapping(prefix = "quarkus.openapi.generator.server.patcher")
public interface OpenApiGeneratedResourcePatcherConfig {

  @WithDefault("true")
  boolean enabled();

  @WithDefault("true")
  boolean failOnRemainingInputStream();

  Optional<String> generatedSourcesDir();

  Map<String, RequestBodyFixConfig> requestBodies();

  interface RequestBodyFixConfig {

    Optional<String> resource();

    String targetClass();

    @WithDefault("data")
    String parameterName();
  }
}
