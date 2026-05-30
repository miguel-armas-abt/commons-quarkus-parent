package io.github.miguelarmasabt.error.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class ExceptionResponseMapperStrategy {

  private final Set<ExceptionResponseMapper> strategies;

  public ExceptionResponseMapperStrategy(Instance<ExceptionResponseMapper> strategyInstances) {
    this.strategies = strategyInstances.stream().collect(Collectors.toSet());
  }

  public Optional<ExceptionResponseMapper> selectStrategy(Throwable throwable) {
    return strategies.stream()
        .filter(strategy -> strategy.support(throwable))
        .findFirst();
  }
}
