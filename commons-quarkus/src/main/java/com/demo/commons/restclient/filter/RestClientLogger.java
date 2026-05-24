package com.demo.commons.restclient.filter;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.client.api.ClientLogger;

import java.util.concurrent.atomic.AtomicInteger;

@Unremovable
@ApplicationScoped
public class RestClientLogger implements ClientLogger {

  private static final Logger log = Logger.getLogger(RestClientLogger.class);

  private final AtomicInteger bodyLimit = new AtomicInteger(2048);

  private final long maxResponse
}
