package io.github.miguelarmasabt.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Tracing {

  public static final String CALLER_NAME_REGEX = "^[a-zA-Z0-9._-]+$";
  public static final String TRACE_PARENT_REGEX = "^[0-9A-Fa-f]{2}-[0-9A-Fa-f]{32}-[0-9A-Fa-f]{16}-[0-9A-Fa-f]{2}$";

}
