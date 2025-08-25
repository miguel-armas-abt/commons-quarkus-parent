package io.github.miguelarmasabt.config;

import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;
import io.smallrye.config.source.yaml.YamlConfigSource;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public final class ApplicationConfigLoader {

  private static final int APPLICATION_PROPERTIES_ORDINAL = 250;
  private static final int APPLICATION_YAML_ORDINAL = 260;

  private ApplicationConfigLoader() {
  }

  public static SmallRyeConfig load(File projectBaseDir) throws MojoExecutionException {
    SmallRyeConfigBuilder builder = new SmallRyeConfigBuilder()
        .addDefaultSources()
        .addDiscoveredSources()
        .addDefaultInterceptors()
        .addDiscoveredInterceptors()
        .addDiscoveredConverters()
        .withMapping(OpenApiResourcePatcherProperties.class);

    File resourcesDir = new File(projectBaseDir, "src/main/resources");

    File applicationProperties = new File(resourcesDir, "application.properties");
    File applicationYml = new File(resourcesDir, "application.yml");
    File applicationYaml = new File(resourcesDir, "application.yaml");

    try {
      if (applicationProperties.isFile()) {
        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream(applicationProperties)) {
          properties.load(input);
        }

        builder.withSources(new PropertiesConfigSource(
            propertiesToMap(properties),
            applicationProperties.toString(),
            APPLICATION_PROPERTIES_ORDINAL
        ));
      }

      if (applicationYml.isFile()) {
        builder.withSources(new YamlConfigSource(
            applicationYml.toURI().toURL(),
            APPLICATION_YAML_ORDINAL
        ));
      }

      if (applicationYaml.isFile()) {
        builder.withSources(new YamlConfigSource(
            applicationYaml.toURI().toURL(),
            APPLICATION_YAML_ORDINAL
        ));
      }

      return builder.build();

    } catch (MalformedURLException ex) {
      throw new MojoExecutionException("Invalid application.yaml URL", ex);
    } catch (IOException ex) {
      throw new MojoExecutionException("Cannot read application config", ex);
    }
  }

  private static Map<String, String> propertiesToMap(Properties properties) {
    Map<String, String> map = new LinkedHashMap<>();

    for (String name : properties.stringPropertyNames()) {
      map.put(name, properties.getProperty(name));
    }

    return map;
  }
}