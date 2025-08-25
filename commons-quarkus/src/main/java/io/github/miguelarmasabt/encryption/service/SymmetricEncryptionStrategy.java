package io.github.miguelarmasabt.encryption.service;

import io.github.miguelarmasabt.constants.Strings;
import io.github.miguelarmasabt.encryption.enums.SymmetricEncryptionType;
import io.github.miguelarmasabt.error.exceptions.MissingConfigurationFieldException;
import io.github.miguelarmasabt.error.exceptions.RequiredFieldException;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;

import static io.github.miguelarmasabt.error.exceptions.MissingConfigurationFieldException.MISSING_CONFIGURATION_FIELD;
import static io.github.miguelarmasabt.error.exceptions.RequiredFieldException.REQUIRED_FIELD;

public interface SymmetricEncryptionStrategy {

  default Uni<String> encrypt(String key, String value) {
    return Uni.createFrom()
        .item(() -> encryptBlocking(key, value))
        .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
  }

  default Uni<String> decrypt(String key, String cipherMessage) {
    return Uni.createFrom()
        .item(() -> decryptBlocking(key, cipherMessage))
        .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
  }

  String encryptBlocking(String key, String value);

  String decryptBlocking(String key, String cipherMessage);

  default void validateEncryptionKey(String key) {
    if (!Strings.hasText(key)) {
      throw new MissingConfigurationFieldException(MISSING_CONFIGURATION_FIELD, "encryption key");
    }
  }

  default void validateValue(String value) {
    if (!Strings.hasText(value)) {
      throw new RequiredFieldException(REQUIRED_FIELD, "value to encrypt");
    }
  }

  default void validateCipherMessage(String cipherMessage) {
    if (!Strings.hasText(cipherMessage)) {
      throw new RequiredFieldException(REQUIRED_FIELD, "cipherMessage");
    }
  }

  SymmetricEncryptionType supports();
}
