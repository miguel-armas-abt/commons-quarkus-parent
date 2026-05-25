package com.demo.commons.utils;

import com.demo.commons.constants.Strings;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextTruncator {

  private static final String TRUNCATED = "...";

  public static String truncate(String text, int maxChars) {
    return Optional.ofNullable(text)
        .map(txt -> {
          if (maxChars <= 0) {
            return Strings.EMPTY;
          }
          return txt.length() <= maxChars ? txt : txt.substring(0, maxChars) + TRUNCATED;
        })
        .orElse(null);
  }
}
