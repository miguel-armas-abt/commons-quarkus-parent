package io.github.miguelarmasabt.encryption;

import io.github.miguelarmasabt.encryption.enums.SymmetricEncryptionType;
import io.github.miguelarmasabt.encryption.service.SymmetricEncryptionStrategy;
import io.github.miguelarmasabt.error.exceptions.NoSuchStrategyException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static io.github.miguelarmasabt.error.exceptions.NoSuchStrategyException.NO_SUCH_STRATEGY;

@ApplicationScoped
public class SymmetricEncryptionService {

  private final Map<SymmetricEncryptionType, SymmetricEncryptionStrategy> strategies;

  public SymmetricEncryptionService(Instance<SymmetricEncryptionStrategy> symmetricEncryptionStrategies) {
    EnumMap<SymmetricEncryptionType, SymmetricEncryptionStrategy> strategies = new EnumMap<>(SymmetricEncryptionType.class);
    symmetricEncryptionStrategies.forEach(strategy -> strategies.put(strategy.supports(), strategy));
    this.strategies = Map.copyOf(strategies);
  }

  public Uni<String> encrypt(SymmetricEncryptionType encryptionType, String key, String value) {
    return selectStrategy(encryptionType).encrypt(key, value);
  }

  public Uni<String> decrypt(SymmetricEncryptionType encryptionType, String key, String cipherMessage) {
    return selectStrategy(encryptionType).decrypt(key, cipherMessage);
  }

  private SymmetricEncryptionStrategy selectStrategy(SymmetricEncryptionType encryptionType) {
    return Optional.ofNullable(strategies.get(encryptionType))
        .orElseThrow(() -> new NoSuchStrategyException(NO_SUCH_STRATEGY, encryptionType.name()));
  }
}
