package io.github.miguelarmasabt.error.mapper;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@ApplicationScoped
@RequiredArgsConstructor
public class InvalidNumberFormatExceptionResponseMapper
    extends AbstractInvalidFormatMapper
    implements ExceptionResponseMapper {

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

  @Override
  public boolean support(Throwable throwable) {
    return super.support(throwable);
  }

  @Override
  public ConfigurationBaseProperties properties() {
    return properties;
  }

  @Override
  public Response.Status status(Throwable throwable) {
    return super.status(throwable);
  }

  @Override
  public ErrorDto map(Throwable throwable) {
    return super.map(throwable);
  }

  @Override
  protected Map<Class<?>, Predicate<String>> validators() {
    return VALIDATORS;
  }

  @Override
  protected String selectMessage(InvalidFormatException exception) {
    return extractFieldName(exception)
        .map(fieldName -> fieldName + " must be a number")
        .orElse(properties().error().defaultMessage());
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
