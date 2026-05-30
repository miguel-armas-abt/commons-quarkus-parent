package io.github.miguelarmasabt.service;

import io.github.miguelarmasabt.model.RequestBodyFix;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

public class GeneratedResourceScanner {

  private final Log log;

  public GeneratedResourceScanner(Log log) {
    this.log = log;
  }

  public File resolveResourceFile(File resourceDir, RequestBodyFix fix)
      throws MojoExecutionException {

    if (fix.getResource() != null && !fix.getResource().isBlank()) {
      File configuredResource = new File(resourceDir, fix.getResource() + ".java");

      if (!configuredResource.isFile()) {
        throw new MojoExecutionException(
            "Configured generated resource file does not exist: " + configuredResource
                + " for operationId=" + fix.getOperationId()
        );
      }

      return configuredResource;
    }

    return findResourceByOperationId(resourceDir, fix);
  }

  private File findResourceByOperationId(File resourceDir, RequestBodyFix fix)
      throws MojoExecutionException {

    String marker = operationIdMarker(fix.getOperationId());

    try (Stream<Path> paths = Files.walk(resourceDir.toPath())) {
      List<Path> matches = paths
          .filter(Files::isRegularFile)
          .filter(path -> path.getFileName().toString().endsWith("Resource.java"))
          .filter(path -> contains(path, marker))
          .toList();

      if (matches.isEmpty()) {
        throw new MojoExecutionException(
            "Cannot find a generated Resource.java containing operationId="
                + fix.getOperationId()
                + ". Configure 'resource' explicitly or verify generated sources."
        );
      }

      if (matches.size() > 1) {
        throw new MojoExecutionException(
            "Expected exactly one generated Resource.java for operationId="
                + fix.getOperationId()
                + ", but found "
                + matches.size()
                + ": "
                + matches
        );
      }

      Path resolved = matches.get(0);
      log.debug("Resolved operationId=" + fix.getOperationId() + " to " + resolved);
      return resolved.toFile();

    } catch (IOException ex) {
      throw new MojoExecutionException(
          "Cannot scan generated resource directory: " + resourceDir,
          ex
      );
    }
  }

  private boolean contains(Path path, String text) {
    try {
      return Files.readString(path, StandardCharsets.UTF_8).contains(text);
    } catch (IOException ex) {
      log.warn("Cannot read generated resource while scanning: " + path, ex);
      return false;
    }
  }

  private String operationIdMarker(String operationId) {
    return "operationId = \"" + operationId + "\"";
  }
}
