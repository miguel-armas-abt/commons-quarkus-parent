package io.github.miguelarmasabt.validations;

import io.github.miguelarmasabt.constants.Strings;
import io.github.miguelarmasabt.error.exceptions.InvalidFieldException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class BodyValidator {

  private final Validator validator;

  public <T> Uni<T> validateAndGet(T body) {
    Set<ConstraintViolation<T>> violations = validator.validate(body);
    if (!violations.isEmpty()) {
      String errorMessages = violations.stream()
          .map(v -> String.format("The value of %s %s",
              v.getPropertyPath(), v.getMessage()))
          .collect(Collectors.joining(Strings.COMMA));
      return Uni.createFrom().failure(new InvalidFieldException(errorMessages));
    }
    return Uni.createFrom().item(body);
  }

  public <T> boolean isValid(T body) {
    return validator.validate(body).isEmpty();
  }
}