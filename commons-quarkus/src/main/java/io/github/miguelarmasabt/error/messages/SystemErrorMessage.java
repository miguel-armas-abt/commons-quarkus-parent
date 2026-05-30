package io.github.miguelarmasabt.error.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum SystemErrorMessage {

  CONNECT_TIMEOUT("Se agotó el tiempo de espera. No fue posible concretar la comunicación con el cliente REST."),
  REFUSED_CONNECTION("El host no pudo ser encontrado. No fue posible concretar la comunicación con el cliente REST."),
  ;

  private final String message;

  public static Map<String, String> asMap() {
    return Arrays.stream(values())
        .collect(Collectors.toMap(
            SystemErrorMessage::name,
            SystemErrorMessage::getMessage
        ));
  }
}
