package com.demo.commons.properties;

import com.demo.commons.properties.logging.LoggingTemplate;
import com.demo.commons.properties.restclient.RestClient;

import java.util.Map;
import java.util.Optional;

public interface ConfigurationBaseProperties {

  Optional<ProjectType> projectType();

  Optional<LoggingTemplate> logging();

  Map<String, String> errorMessages();

  Map<String, RestClient> restClients();

}