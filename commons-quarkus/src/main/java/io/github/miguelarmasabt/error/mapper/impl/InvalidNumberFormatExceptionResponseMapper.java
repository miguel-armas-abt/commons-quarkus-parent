package io.github.miguelarmasabt.error.mapper.impl;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.github.miguelarmasabt.error.helper.ConfiguredMessageResolver;
import io.github.miguelarmasabt.error.mapper.AbstractInvalidFormatMapper;
import io.github.miguelarmasabt.error.mapper.ExceptionResponseMapper;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@ApplicationScoped
@RequiredArgsConstructor
public class InvalidNumberFormatExceptionResponseMapper
    extends AbstractInvalidFormatMapper
    implements ExceptionResponseMapper {

  public static final String INVALID_NUMBER = "invalid_number";

  private static final Predicate<String> BIG_DECIMAL = isNumber(BigDecimal::new);
  private static final Predicate<String> INTEGER = isNumber(Integer::parseInt);
  private static final Predicate<String> LONG = isNumber(Long::parseLong);

  private static final Map<Class<?>, Predicate<String>> VALIDATORS = Map.of(
      BigDecimal.class, BIG_DECIMAL,
      Integer.class, INTEGER,
      int.class, INTEGER,
      Long.class, LONG,
      long.class, LONG
  );

  private final ConfigurationBaseProperties properties;
  private final ConfiguredMessageResolver configuredMessageResolver;

  @Override
  public boolean support(Throwable throwable) {
    return super.support(throwable);
  }

  @Override
  public ConfigurationBaseProperties properties() {
    return properties;
  }

  @Override
  public ConfiguredMessageResolver configuredMessageResolver() {
    return configuredMessageResolver;
  }

  @Override
  public Response.Status selectStatus(Throwable throwable) {
    return Response.Status.BAD_REQUEST;
  }

  @Override
  protected Map<Class<?>, Predicate<String>> validators() {
    return VALIDATORS;
  }

  @Override
  public Optional<String> selectCode(Throwable throwable) {
    return Optional.of(INVALID_NUMBER);
  }

  @Override
  public Optional<String> selectMessage(Throwable throwable, Locale locale) {
    InvalidFormatException exception = (InvalidFormatException) throwable;

    return extractFieldName(exception)
        .flatMap(fieldName -> configuredMessageResolver.resolve(INVALID_NUMBER, locale, fieldName))
        .map(selectedMessage -> this.mapMessage(selectedMessage, true, locale));
  }

  private static Predicate<String> isNumber(Function<String, ?> parser) {
    return value -> {
      try {
        parser.apply(value);
        return true;
      } catch (NumberFormatException exception) {
        return false;
      }
    };
  }

}

