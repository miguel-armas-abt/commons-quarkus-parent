package io.github.miguelarmasabt.service;

import io.github.miguelarmasabt.model.RequestBodyFix;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

public class GeneratedResourceScanner {

  public File resolveResourceFile(File resourceDir, RequestBodyFix fix)
      throws MojoExecutionException {

    File configuredResource = new File(resourceDir, fix.resource() + ".java");

    if (!configuredResource.isFile()) {
      throw new MojoExecutionException(
          "Configured generated resource file does not exist: " + configuredResource
              + " for resource=" + fix.resource()
              + ", operationId=" + fix.operationId()
      );
    }

    return configuredResource;
  }
}
