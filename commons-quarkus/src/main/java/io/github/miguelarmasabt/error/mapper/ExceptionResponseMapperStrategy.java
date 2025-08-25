package io.github.miguelarmasabt.error.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ExceptionResponseMapperStrategy {

  private final List<ExceptionResponseMapper> strategies;

  public ExceptionResponseMapperStrategy(Instance<ExceptionResponseMapper> strategyInstances) {
    this.strategies = strategyInstances.stream().toList();
  }

  public Optional<ExceptionResponseMapper> selectStrategy(Throwable throwable) {
    return strategies.stream()
        .filter(strategy -> strategy.support(throwable))
        .findFirst();
  }
}
