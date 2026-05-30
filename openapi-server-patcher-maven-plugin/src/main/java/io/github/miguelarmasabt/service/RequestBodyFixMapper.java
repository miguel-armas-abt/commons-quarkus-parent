package io.github.miguelarmasabt.service;

import io.github.miguelarmasabt.config.OpenApiGeneratedResourcePatcherConfig;
import io.github.miguelarmasabt.model.RequestBodyFix;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;

public final class RequestBodyFixMapper {

  private RequestBodyFixMapper() {
  }

  public static List<RequestBodyFix> toRequestBodyFixes(
      Map<String, OpenApiGeneratedResourcePatcherConfig.RequestBodyFixConfig> configured
  ) throws MojoExecutionException {

    List<RequestBodyFix> fixes = new ArrayList<>();

    if (configured == null || configured.isEmpty()) {
      return fixes;
    }

    for (Map.Entry<String, OpenApiGeneratedResourcePatcherConfig.RequestBodyFixConfig> entry
        : configured.entrySet()) {

      String operationId = entry.getKey();
      OpenApiGeneratedResourcePatcherConfig.RequestBodyFixConfig item = entry.getValue();

      RequestBodyFix fix = new RequestBodyFix();
      fix.setOperationId(operationId);
      fix.setResource(item.resource().orElse(null));
      fix.setTargetClass(item.targetClass());
      fix.setParameterName(item.parameterName());

      validate(fix);
      fixes.add(fix);
    }

    return fixes;
  }

  private static void validate(RequestBodyFix fix) throws MojoExecutionException {
    if (isBlank(fix.getOperationId())) {
      throw new MojoExecutionException("Request body fix requires operationId.");
    }

    if (isBlank(fix.getTargetClass())) {
      throw new MojoExecutionException(
          "Request body fix for operationId=" + fix.getOperationId()
              + " requires property: target-class"
      );
    }

    if (isBlank(fix.getParameterName())) {
      fix.setParameterName("data");
    }
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
