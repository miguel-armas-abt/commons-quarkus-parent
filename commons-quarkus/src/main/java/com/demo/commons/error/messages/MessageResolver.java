package com.demo.commons.error.messages;

import com.demo.commons.properties.ConfigurationBaseProperties;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class MessageResolver {

  private final Map<String, String> messages;

  public MessageResolver(ConfigurationBaseProperties properties) {
    Map<String, String> resolvedMessages = new HashMap<>(SystemErrorMessage.asMap());
    Map<String, String> configMessages = properties.error().messages();
    resolvedMessages.putAll(configMessages);
    this.messages = Map.copyOf(resolvedMessages);
  }

  public Map<String, String> messages() {
    return this.messages;
  }
}
