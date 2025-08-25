package io.github.miguelarmasabt.service;

import io.github.miguelarmasabt.model.RequestBodyFix;
import org.apache.maven.plugin.MojoExecutionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RequestBodyFixMapper {

  private RequestBodyFixMapper() {
  }

  public static List<RequestBodyFix> toRequestBodyFixes(
      Map<String, Map<String, String>> configured
  ) throws MojoExecutionException {

    List<RequestBodyFix> fixes = new ArrayList<>();

    if (configured == null || configured.isEmpty()) {
      return fixes;
    }

    for (Map.Entry<String, Map<String, String>> resourceEntry : configured.entrySet()) {
      String resource = resourceEntry.getKey();
      Map<String, String> operations = resourceEntry.getValue();

      if (isBlank(resource)) {
        throw new MojoExecutionException("Request body fix requires resource name.");
      }

      if (operations == null || operations.isEmpty()) {
        throw new MojoExecutionException(
            "Request body fix for resource=" + resource + " requires at least one operation."
        );
      }

      for (Map.Entry<String, String> operationEntry : operations.entrySet()) {
        RequestBodyFix fix = new RequestBodyFix(
            resource,
            operationEntry.getKey(),
            operationEntry.getValue()
        );

        validate(fix);
        fixes.add(fix);
      }
    }

    return fixes;
  }

  private static void validate(RequestBodyFix fix) throws MojoExecutionException {
    if (isBlank(fix.resource())) {
      throw new MojoExecutionException("Request body fix requires resource.");
    }

    if (isBlank(fix.operationId())) {
      throw new MojoExecutionException(
          "Request body fix for resource=" + fix.resource()
              + " requires operationId."
      );
    }

    if (isBlank(fix.targetClass())) {
      throw new MojoExecutionException(
          "Request body fix for resource=" + fix.resource()
              + ", operationId=" + fix.operationId()
              + " requires target class."
      );
    }

    if (isBlank(fix.simpleName())) {
      throw new MojoExecutionException(
          "Request body fix for resource=" + fix.resource()
              + ", operationId=" + fix.operationId()
              + " could not resolve target class simple name."
      );
    }
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}