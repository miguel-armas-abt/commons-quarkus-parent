package io.github.miguelarmasabt.rest.server;

import io.github.miguelarmasabt.constants.Tracing;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.HeaderParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultHeaders implements Serializable {

  @HeaderParam("caller-name")
  @Pattern(regexp = Tracing.CALLER_NAME_REGEX)
  @NotBlank
  private String callerName;

  @HeaderParam("trace-parent")
  @Pattern(regexp = Tracing.TRACE_PARENT_REGEX)
  @NotBlank
  private String traceParent;
}