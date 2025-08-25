package io.github.miguelarmasabt.error.helper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RequestScoped
public class RequestLocaleResolver {

  private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

  @Context
  HttpHeaders httpHeaders;

  public Locale resolve() {
    return Optional.ofNullable(httpHeaders)
        .map(HttpHeaders::getAcceptableLanguages)
        .filter(languages -> !languages.isEmpty())
        .map(List::getFirst)
        .orElse(DEFAULT_LOCALE);
  }
}
