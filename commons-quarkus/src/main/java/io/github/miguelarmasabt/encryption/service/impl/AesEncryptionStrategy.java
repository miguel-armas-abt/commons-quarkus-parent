package io.github.miguelarmasabt.encryption.service.impl;

import io.github.miguelarmasabt.encryption.enums.SymmetricEncryptionType;
import io.github.miguelarmasabt.encryption.service.SymmetricEncryptionStrategy;
import io.github.miguelarmasabt.error.exceptions.ConditionFailedException;
import io.github.miguelarmasabt.error.exceptions.ProcessingFailedException;
import jakarta.enterprise.context.ApplicationScoped;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

import static io.github.miguelarmasabt.error.exceptions.ConditionFailedException.CONDITION_FAILED;
import static io.github.miguelarmasabt.error.exceptions.ProcessingFailedException.PROCESSING_FAILED;

@ApplicationScoped
public class AesEncryptionStrategy implements SymmetricEncryptionStrategy {

  private static final String ADVANCED_ENCRYPTION_STANDARD_ALGORITHM_NAME = "AES";
  private static final String ADVANCED_ENCRYPTION_STANDARD_ALGORITHM_MODE = "AES/GCM/NoPadding";
  private static final String KEY_DERIVATION_ALGORITHM_NAME = "PBKDF2WithHmacSHA512";

  private static final int SALT_SIZE_BYTES = 16;
  private static final int NONCE_SIZE_BYTES = 12;
  private static final int TAG_SIZE_BITS = 128;
  private static final int KEY_SIZE_BITS = 256;
  private static final int PBKDF2_ITERATIONS = 100_000;

  private static final int TAG_SIZE_BYTES = TAG_SIZE_BITS / Byte.SIZE;
  private static final int MIN_CIPHER_TEXT_SIZE_BYTES = 1;
  private static final int MIN_PAYLOAD_SIZE_BYTES = SALT_SIZE_BYTES
      + NONCE_SIZE_BYTES
      + MIN_CIPHER_TEXT_SIZE_BYTES
      + TAG_SIZE_BYTES;

  private static final int ENCRYPTED_PAYLOAD_OFFSET = SALT_SIZE_BYTES + NONCE_SIZE_BYTES;
  private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
  private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
  private static final byte ZERO_BYTE = (byte) 0;

  private final SecureRandom secureRandom = new SecureRandom();

  @Override
  public String encryptBlocking(String aesKey, String value) {
    validateValue(value);
    validateEncryptionKey(aesKey);

    byte[] keyBytes = null;

    try {
      byte[] salt = generateRandomBytes(SALT_SIZE_BYTES);
      byte[] nonce = generateRandomBytes(NONCE_SIZE_BYTES);

      keyBytes = deriveKey(aesKey, salt);

      Cipher cipher = Cipher.getInstance(ADVANCED_ENCRYPTION_STANDARD_ALGORITHM_MODE);
      cipher.init(
          Cipher.ENCRYPT_MODE,
          new SecretKeySpec(keyBytes, ADVANCED_ENCRYPTION_STANDARD_ALGORITHM_NAME),
          new GCMParameterSpec(TAG_SIZE_BITS, nonce));

      byte[] encryptedValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
      byte[] payload = new byte[SALT_SIZE_BYTES + NONCE_SIZE_BYTES + encryptedValue.length];
      System.arraycopy(salt, 0, payload, 0, SALT_SIZE_BYTES);
      System.arraycopy(nonce, 0, payload, SALT_SIZE_BYTES, NONCE_SIZE_BYTES);
      System.arraycopy(encryptedValue, 0, payload, ENCRYPTED_PAYLOAD_OFFSET, encryptedValue.length);

      return BASE64_URL_ENCODER.encodeToString(payload);
    } catch (GeneralSecurityException exception) {
      throw new ProcessingFailedException(PROCESSING_FAILED, exception, "AES encryption");
    } finally {
      clear(keyBytes);
    }
  }

  @Override
  public String decryptBlocking(String aesKey, String cipherMessage) {
    validateCipherMessage(cipherMessage);
    validateEncryptionKey(aesKey);

    byte[] keyBytes = null;

    try {
      byte[] payload = BASE64_URL_DECODER.decode(cipherMessage);
      if (payload.length < MIN_PAYLOAD_SIZE_BYTES) {
        throw new ConditionFailedException(CONDITION_FAILED, "The payload to decrypt is too short");
      }

      byte[] salt = Arrays.copyOfRange(payload, 0, SALT_SIZE_BYTES);

      keyBytes = deriveKey(aesKey, salt);

      Cipher cipher = Cipher.getInstance(ADVANCED_ENCRYPTION_STANDARD_ALGORITHM_MODE);
      cipher.init(
          Cipher.DECRYPT_MODE,
          new SecretKeySpec(keyBytes, ADVANCED_ENCRYPTION_STANDARD_ALGORITHM_NAME),
          new GCMParameterSpec(TAG_SIZE_BITS, payload, SALT_SIZE_BYTES, NONCE_SIZE_BYTES)
      );

      byte[] decrypted = cipher.doFinal(payload, ENCRYPTED_PAYLOAD_OFFSET, payload.length - ENCRYPTED_PAYLOAD_OFFSET);
      return new String(decrypted, StandardCharsets.UTF_8);
    } catch (GeneralSecurityException | IllegalArgumentException exception) {
      throw new ProcessingFailedException(PROCESSING_FAILED, exception, "AES decryption");
    } finally {
      clear(keyBytes);
    }
  }

  @Override
  public SymmetricEncryptionType supports() {
    return SymmetricEncryptionType.AES;
  }

  private byte[] deriveKey(String aesKey, byte[] salt) throws GeneralSecurityException {
    PBEKeySpec spec = new PBEKeySpec(
        aesKey.toCharArray(),
        salt,
        PBKDF2_ITERATIONS,
        KEY_SIZE_BITS
    );

    try {
      return SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM_NAME)
          .generateSecret(spec)
          .getEncoded();
    } finally {
      spec.clearPassword();
    }
  }

  private byte[] generateRandomBytes(int size) {
    byte[] bytes = new byte[size];
    secureRandom.nextBytes(bytes);
    return bytes;
  }

  private static void clear(byte[] bytes) {
    if (Objects.nonNull(bytes)) {
      Arrays.fill(bytes, ZERO_BYTE);
    }
  }
}
