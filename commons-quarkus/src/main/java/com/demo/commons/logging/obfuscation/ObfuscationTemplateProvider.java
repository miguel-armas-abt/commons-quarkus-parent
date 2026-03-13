package com.demo.commons.logging.obfuscation;

import com.demo.commons.properties.ConfigurationBaseProperties;
import com.demo.commons.properties.logging.EmptyObfuscationTemplate;
import com.demo.commons.properties.logging.LoggingTemplate;
import com.demo.commons.properties.logging.ObfuscationTemplate;
import jakarta.inject.Singleton;

@Singleton
public class ObfuscationTemplateProvider {

  private final ObfuscationTemplate template;

  public ObfuscationTemplateProvider(ConfigurationBaseProperties properties) {
    this.template = properties.logging()
        .flatMap(LoggingTemplate::obfuscation)
        .orElseGet(EmptyObfuscationTemplate::new);
  }

  public ObfuscationTemplate get() {
    return template;
  }
}
