package io.github.miguelarmasabt.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Strings {

  public static final String EMPTY = "";
  public static final String SPACE = " ";
  public static final String COLON = ":";
  public static final String DASH = "-";
  public static final String EQUALS = "=";
  public static final String COMMA = ",";
  public static final String DOT = ".";

  public static String orEmpty(String value) {
    return Objects.isNull(value) ? EMPTY : value;
  }

  public static boolean hasText(String value) {
    return !Objects.isNull(value) && !value.isBlank();
  }
}
