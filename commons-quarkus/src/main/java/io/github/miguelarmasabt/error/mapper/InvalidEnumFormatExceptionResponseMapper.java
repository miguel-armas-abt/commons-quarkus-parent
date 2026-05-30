package io.github.miguelarmasabt.error.mapper;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@ApplicationScoped
@RequiredArgsConstructor
public class InvalidEnumFormatExceptionResponseMapper
    extends AbstractInvalidFormatMapper
    implements ExceptionResponseMapper {

  private final ConfigurationBaseProperties properties;

  @Override
  public boolean support(Throwable throwable) {
    return asInvalidFormatException(throwable)
        .filter(exception -> Objects.nonNull(exception.getTargetType()))
        .map(exception -> Objects.nonNull(exception.getTargetType().getEnumConstants()))
        .orElse(Boolean.FALSE);
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
    return Map.of();
  }

  @Override
  protected String selectMessage(InvalidFormatException exception) {
    return extractFieldName(exception)
        .map(fieldName -> fieldName + " must be in " + Arrays.toString(exception.getTargetType().getEnumConstants()))
        .orElse(properties().error().defaultMessage());
  }
}
