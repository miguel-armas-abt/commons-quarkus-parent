package io.github.miguelarmasabt;

import io.github.miguelarmasabt.config.OpenApiGeneratedResourcePatcherConfig;
import io.github.miguelarmasabt.config.QuarkusLikeConfigLoader;
import io.github.miguelarmasabt.model.RequestBodyFix;
import io.github.miguelarmasabt.service.GeneratedResourcePatcher;
import io.github.miguelarmasabt.service.GeneratedResourceScanner;
import io.github.miguelarmasabt.service.RequestBodyFixMapper;
import io.smallrye.config.SmallRyeConfig;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(
    name = "patch-generated-rest",
    defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
    threadSafe = true,
    requiresProject = true
)
public class OpenApiGeneratedResourcePatcherMojo extends AbstractMojo {

  private static final String BASE_PACKAGE_PROPERTY =
      "quarkus.openapi.generator.server.base-package";

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  @Parameter(
      property = "openapi.patcher.generatedSourcesDir",
      defaultValue = "${project.build.directory}/generated-sources/jaxrs"
  )
  private File defaultGeneratedSourcesDir;

  @Override
  public void execute() throws MojoExecutionException {
    SmallRyeConfig config = QuarkusLikeConfigLoader.load(project.getBasedir());

    OpenApiGeneratedResourcePatcherConfig patcherConfig =
        config.getConfigMapping(OpenApiGeneratedResourcePatcherConfig.class);

    if (!patcherConfig.enabled()) {
      getLog().info("OpenAPI generated resource patcher is disabled.");
      return;
    }

    String basePackage = config.getOptionalValue(BASE_PACKAGE_PROPERTY, String.class)
        .filter(value -> !value.isBlank())
        .orElseThrow(() -> new MojoExecutionException(
            "Missing required property: " + BASE_PACKAGE_PROPERTY
        ));

    File generatedSourcesDir = resolveGeneratedSourcesDir(patcherConfig);
    File resourceDir = new File(
        generatedSourcesDir,
        basePackage.replace('.', File.separatorChar)
    );

    if (!resourceDir.isDirectory()) {
      throw new MojoExecutionException(
          "Generated REST resource directory does not exist: " + resourceDir
              + ". Check " + BASE_PACKAGE_PROPERTY
              + " and quarkus.openapi.generator.server.patcher.generated-sources-dir."
      );
    }

    List<RequestBodyFix> fixes = RequestBodyFixMapper.toRequestBodyFixes(
        patcherConfig.requestBodies()
    );

    if (fixes.isEmpty()) {
      getLog().info("No OpenAPI generated request body fixes configured.");
      return;
    }

    getLog().info("Patching OpenAPI generated REST resources at: " + resourceDir);
    getLog().info("Resolved base package: " + basePackage);
    getLog().info("Configured request body fixes: " + fixes.size());

    GeneratedResourceScanner scanner = new GeneratedResourceScanner(getLog());
    GeneratedResourcePatcher patcher = new GeneratedResourcePatcher(getLog());

    patcher.patch(
        resourceDir,
        fixes,
        patcherConfig.failOnRemainingInputStream(),
        scanner
    );
  }

  private File resolveGeneratedSourcesDir(OpenApiGeneratedResourcePatcherConfig patcherConfig) {
    return patcherConfig.generatedSourcesDir()
        .filter(value -> !value.isBlank())
        .map(this::resolveFromProjectBase)
        .orElse(defaultGeneratedSourcesDir);
  }

  private File resolveFromProjectBase(String configuredValue) {
    Path configuredPath = Path.of(configuredValue);

    if (configuredPath.isAbsolute()) {
      return configuredPath.toFile();
    }

    return project.getBasedir()
        .toPath()
        .resolve(configuredPath)
        .normalize()
        .toFile();
  }
}
