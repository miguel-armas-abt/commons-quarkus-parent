package io.github.miguelarmasabt.error.mapper;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.error.dto.ErrorOrigin;
import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import jakarta.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class AbstractInvalidFormatMapper {

  protected abstract Map<Class<?>, Predicate<String>> validators();

  protected abstract String selectMessage(InvalidFormatException exception);

  protected abstract ConfigurationBaseProperties properties();

  protected Response.Status status(Throwable throwable) {
    return Response.Status.BAD_REQUEST;
  }

  protected boolean support(Throwable throwable) {
    return asInvalidFormatException(throwable)
        .flatMap(exception -> extractValue(exception)
            .filter(value -> !validatorFor(exception).test(value)))
        .isPresent();
  }

  protected ErrorDto map(Throwable throwable) {
    InvalidFormatException exception = (InvalidFormatException) throwable;
    ErrorDto error = new ErrorDto();

    error.setOrigin(ErrorOrigin.OWN);
    error.setCode(properties().error().defaultCode());
    error.setMessage(selectMessage(exception));

    return error;
  }

  protected Optional<InvalidFormatException> asInvalidFormatException(Throwable throwable) {
    return Optional.ofNullable(throwable)
        .filter(InvalidFormatException.class::isInstance)
        .map(InvalidFormatException.class::cast);
  }

  protected Optional<String> extractValue(InvalidFormatException exception) {
    return Optional.ofNullable(exception.getTargetType())
        .filter(this::isSupportedType)
        .flatMap(ignored -> Optional.ofNullable(exception.getValue()))
        .map(String::valueOf)
        .map(String::trim);
  }

  protected static Optional<String> extractFieldName(InvalidFormatException exception) {
    return Optional.ofNullable(exception)
        .map(InvalidFormatException::getPath)
        .stream()
        .flatMap(List::stream)
        .map(JsonMappingException.Reference::getFieldName)
        .filter(StringUtils::isNotBlank)
        .reduce((a,b) -> b);
  }

  private boolean isSupportedType(Class<?> targetType) {
    return Objects.nonNull(targetType) && validators().containsKey(targetType);
  }

  private Predicate<String> validatorFor(InvalidFormatException exception) {
    return Optional.ofNullable(exception.getTargetType())
        .map(validators()::get)
        .orElse(value -> Boolean.TRUE);
  }
}
