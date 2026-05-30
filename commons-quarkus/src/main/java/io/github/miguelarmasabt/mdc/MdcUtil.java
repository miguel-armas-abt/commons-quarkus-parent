package io.github.miguelarmasabt.mdc;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jboss.logging.MDC;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MdcUtil {

  public static void clearSet(Set<String> labels) {
    Optional.ofNullable(labels)
        .ifPresent(set -> set.forEach(MDC::remove));
  }

  public static void populate(Map<String, String> labels, Runnable action) {
    try {
      labels.forEach((key, value) -> MDC.put(key, String.valueOf(value)));
      action.run();
    } finally {
      labels.forEach((key, value) -> MDC.remove(key));
    }
  }
}
