package com.demo.commons.openapi.patcher;

import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.yaml.snakeyaml.Yaml;

@Mojo(
    name = "patch-generated-rest",
    defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
    threadSafe = true,
    requiresProject = true
)
public class OpenApiGeneratedResourcePatcherMojo extends AbstractMojo {

  private static final String BASE_PACKAGE_PROPERTY =
      "quarkus.openapi.generator.server.base-package";

  private static final String PATCHER_PREFIX =
      "quarkus.openapi.generator.server.patcher";

  private static final String ENABLED_PROPERTY =
      PATCHER_PREFIX + ".enabled";

  private static final String FAIL_ON_INPUT_STREAM_PROPERTY =
      PATCHER_PREFIX + ".fail-on-remaining-input-stream";

  private static final String GENERATED_SOURCES_DIR_PROPERTY =
      PATCHER_PREFIX + ".generated-sources-dir";

  private static final String REQUEST_BODIES_PREFIX =
      PATCHER_PREFIX + ".request-bodies.";

  private static final Pattern PACKAGE_DECLARATION =
      Pattern.compile("(?m)^package\\s+[a-zA-Z0-9_.]+;\\R?");

  private static final Pattern IMPORT_DECLARATION =
      Pattern.compile("(?m)^import\\s+[^;]+;\\R?");

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  @Parameter(
      property = "openapi.patcher.generatedSourcesDir",
      defaultValue = "${project.build.directory}/generated-sources/jaxrs"
  )
  private File defaultGeneratedSourcesDir;

  @Override
  public void execute() throws MojoExecutionException {
    SmallRyeConfig config = loadConfig();

    boolean enabled = config.getOptionalValue(ENABLED_PROPERTY, Boolean.class)
        .orElse(true);

    if (!enabled) {
      getLog().info("OpenAPI generated resource patcher is disabled.");
      return;
    }

    String basePackage = config.getOptionalValue(BASE_PACKAGE_PROPERTY, String.class)
        .filter(value -> !value.isBlank())
        .orElseThrow(() -> new MojoExecutionException(
            "Missing required property: " + BASE_PACKAGE_PROPERTY
        ));

    boolean failOnRemainingInputStream = config
        .getOptionalValue(FAIL_ON_INPUT_STREAM_PROPERTY, Boolean.class)
        .orElse(true);

    File generatedSourcesDir = resolveGeneratedSourcesDir(config);

    File resourceDir = new File(
        generatedSourcesDir,
        basePackage.replace('.', File.separatorChar)
    );

    if (!resourceDir.isDirectory()) {
      throw new MojoExecutionException(
          "Generated REST resource directory does not exist: " + resourceDir
              + ". Check " + BASE_PACKAGE_PROPERTY
              + " and " + GENERATED_SOURCES_DIR_PROPERTY + "."
      );
    }

    List<RequestBodyFix> requestBodyFixes = loadRequestBodyFixes(config);

    if (requestBodyFixes.isEmpty()) {
      getLog().info("No OpenAPI generated request body fixes configured.");
      return;
    }

    getLog().info("Patching OpenAPI generated REST resources at: " + resourceDir);
    getLog().info("Resolved base package: " + basePackage);

    for (RequestBodyFix fix : requestBodyFixes) {
      patchFix(resourceDir, basePackage, fix, failOnRemainingInputStream);
    }
  }

  private SmallRyeConfig loadConfig() throws MojoExecutionException {
    Map<String, String> flattenedApplicationConfig = loadApplicationConfigAsFlatMap();

    return new SmallRyeConfigBuilder()
        .addDefaultSources()
        .withSources(new PropertiesConfigSource(
            flattenedApplicationConfig,
            "openapi-generated-resource-patcher-application-config",
            250
        ))
        .build();
  }

  private Map<String, String> loadApplicationConfigAsFlatMap() throws MojoExecutionException {
    Map<String, String> result = new LinkedHashMap<>();

    File resourcesDir = new File(project.getBasedir(), "src/main/resources");

    File applicationProperties = new File(resourcesDir, "application.properties");
    File applicationYml = new File(resourcesDir, "application.yml");
    File applicationYaml = new File(resourcesDir, "application.yaml");

    if (applicationProperties.isFile()) {
      result.putAll(loadProperties(applicationProperties));
    }

    if (applicationYml.isFile()) {
      result.putAll(loadYaml(applicationYml));
    }

    if (applicationYaml.isFile()) {
      result.putAll(loadYaml(applicationYaml));
    }

    return result;
  }

  private Map<String, String> loadProperties(File file) throws MojoExecutionException {
    Properties properties = new Properties();

    try (FileInputStream input = new FileInputStream(file)) {
      properties.load(input);
    } catch (IOException ex) {
      throw new MojoExecutionException("Cannot read " + file, ex);
    }

    Map<String, String> map = new LinkedHashMap<>();

    for (String name : properties.stringPropertyNames()) {
      map.put(name, properties.getProperty(name));
    }

    return map;
  }

  @SuppressWarnings("unchecked")
  private Map<String, String> loadYaml(File file) throws MojoExecutionException {
    try (FileInputStream input = new FileInputStream(file)) {
      Object loaded = new Yaml().load(input);

      Map<String, String> flattened = new LinkedHashMap<>();

      if (loaded instanceof Map<?, ?> root) {
        flattenYaml("", (Map<String, Object>) root, flattened);
      }

      return flattened;
    } catch (IOException ex) {
      throw new MojoExecutionException("Cannot read " + file, ex);
    }
  }

  @SuppressWarnings("unchecked")
  private void flattenYaml(String prefix, Map<String, Object> source, Map<String, String> target) {
    for (Map.Entry<String, Object> entry : source.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      String propertyName = prefix.isBlank() ? key : prefix + "." + key;

      if (value instanceof Map<?, ?> nestedMap) {
        flattenYaml(propertyName, (Map<String, Object>) nestedMap, target);
      } else if (value instanceof List<?> list) {
        for (int index = 0; index < list.size(); index++) {
          Object item = list.get(index);
          String listPropertyName = propertyName + "[" + index + "]";

          if (item instanceof Map<?, ?> nestedListMap) {
            flattenYaml(listPropertyName, (Map<String, Object>) nestedListMap, target);
          } else if (item != null) {
            target.put(listPropertyName, String.valueOf(item));
          }
        }
      } else if (value != null) {
        target.put(propertyName, String.valueOf(value));
      }
    }
  }

  private File resolveGeneratedSourcesDir(SmallRyeConfig config) {
    String configuredValue = config
        .getOptionalValue(GENERATED_SOURCES_DIR_PROPERTY, String.class)
        .orElse(null);

    if (configuredValue == null || configuredValue.isBlank()) {
      return defaultGeneratedSourcesDir;
    }

    Path configuredPath = Path.of(configuredValue);

    if (configuredPath.isAbsolute()) {
      return configuredPath.toFile();
    }

    return project.getBasedir().toPath().resolve(configuredPath).normalize().toFile();
  }

  private List<RequestBodyFix> loadRequestBodyFixes(SmallRyeConfig config)
      throws MojoExecutionException {

    Map<String, Map<String, String>> grouped = new LinkedHashMap<>();

    for (String propertyName : config.getPropertyNames()) {
      if (!propertyName.startsWith(REQUEST_BODIES_PREFIX)) {
        continue;
      }

      String remaining = propertyName.substring(REQUEST_BODIES_PREFIX.length());
      int dotIndex = remaining.indexOf('.');

      if (dotIndex <= 0 || dotIndex == remaining.length() - 1) {
        continue;
      }

      String operationId = remaining.substring(0, dotIndex);
      String fieldName = remaining.substring(dotIndex + 1);

      grouped
          .computeIfAbsent(operationId, key -> new LinkedHashMap<>())
          .put(fieldName, config.getValue(propertyName, String.class));
    }

    List<RequestBodyFix> fixes = new ArrayList<>();

    for (Map.Entry<String, Map<String, String>> entry : grouped.entrySet()) {
      String operationId = entry.getKey();
      Map<String, String> values = entry.getValue();

      RequestBodyFix fix = new RequestBodyFix();
      fix.setOperationId(operationId);
      fix.setResource(values.get("resource"));
      fix.setTargetClass(values.get("target-class"));
      fix.setParameterName(values.getOrDefault("parameter-name", "data"));

      validateFix(fix);
      fixes.add(fix);
    }

    return fixes;
  }

  private void validateFix(RequestBodyFix fix) throws MojoExecutionException {
    if (isBlank(fix.getOperationId())) {
      throw new MojoExecutionException("Request body fix requires operationId.");
    }

    if (isBlank(fix.getResource())) {
      throw new MojoExecutionException(
          "Request body fix for operationId=" + fix.getOperationId()
              + " requires property: resource"
      );
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

  private void patchFix(File resourceDir,
                        String resolvedBasePackage,
                        RequestBodyFix fix,
                        boolean failOnRemainingInputStream)
      throws MojoExecutionException {

    File resourceFile = new File(resourceDir, fix.getResource() + ".java");

    if (!resourceFile.isFile()) {
      throw new MojoExecutionException("Generated resource file does not exist: " + resourceFile);
    }

    String content = read(resourceFile);
    String before = content;

    String simpleName = fix.simpleName();
    String generatedDtoClass = resolvedBasePackage + ".beans." + simpleName;
    String targetDtoClass = fix.getTargetClass();

    content = removeImport(content, generatedDtoClass);
    content = ensureImport(content, targetDtoClass);
    content = ensureImport(content, "jakarta.validation.Valid");

    content = patchMethodBodyParameter(content, fix, simpleName);

    if (!content.contains("InputStream ")) {
      content = removeImport(content, "java.io.InputStream");
    }

    validatePatchedContent(
        content,
        resourceFile,
        fix,
        simpleName,
        targetDtoClass,
        generatedDtoClass,
        failOnRemainingInputStream
    );

    if (!before.equals(content)) {
      write(resourceFile, content);
      getLog().info("Patched " + resourceFile.getName()
          + " operationId=" + fix.getOperationId()
          + " bodyType=" + fix.getTargetClass());
    } else {
      getLog().warn("No changes applied to " + resourceFile.getName()
          + " operationId=" + fix.getOperationId()
          + ". Check generated signature.");
    }
  }

  private String patchMethodBodyParameter(String content, RequestBodyFix fix, String simpleName)
      throws MojoExecutionException {

    String marker = "operationId = \"" + fix.getOperationId() + "\"";
    int markerIndex = content.indexOf(marker);

    if (markerIndex < 0) {
      throw new MojoExecutionException(
          "Cannot find operationId=" + fix.getOperationId()
              + " in generated resource " + fix.getResource()
      );
    }

    int methodEnd = content.indexOf(");", markerIndex);

    if (methodEnd < 0) {
      throw new MojoExecutionException(
          "Cannot find method end for operationId=" + fix.getOperationId()
      );
    }

    String prefix = content.substring(0, markerIndex);
    String methodBlock = content.substring(markerIndex, methodEnd + 2);
    String suffix = content.substring(methodEnd + 2);

    String parameterName = fix.getParameterName();

    methodBlock = methodBlock.replace(
        "@Valid @NotNull InputStream " + parameterName,
        "@Valid @NotNull " + simpleName + " " + parameterName
    );

    methodBlock = methodBlock.replace(
        "@NotNull InputStream " + parameterName,
        "@Valid @NotNull " + simpleName + " " + parameterName
    );

    methodBlock = methodBlock.replace(
        "@NotNull " + simpleName + " " + parameterName,
        "@Valid @NotNull " + simpleName + " " + parameterName
    );

    methodBlock = methodBlock.replace(
        "@Valid @Valid @NotNull " + simpleName + " " + parameterName,
        "@Valid @NotNull " + simpleName + " " + parameterName
    );

    return prefix + methodBlock + suffix;
  }

  private void validatePatchedContent(String content,
                                      File resourceFile,
                                      RequestBodyFix fix,
                                      String simpleName,
                                      String targetDtoClass,
                                      String generatedDtoClass,
                                      boolean failOnRemainingInputStream)
      throws MojoExecutionException {

    if (content.contains("import import ")) {
      throw new MojoExecutionException(
          "Patch failed for " + resourceFile
              + ". Invalid duplicated import keyword found: 'import import'."
      );
    }

    if (hasInvalidDoubleSemicolonImport(content)) {
      throw new MojoExecutionException(
          "Patch failed for " + resourceFile
              + ". Invalid duplicated semicolon found in imports."
      );
    }

    if (!hasImport(content, "jakarta.validation.Valid")) {
      throw new MojoExecutionException(
          "Patch failed for " + resourceFile
              + ". Missing import: jakarta.validation.Valid"
      );
    }

    if (!hasImport(content, targetDtoClass)) {
      throw new MojoExecutionException(
          "Patch failed for " + resourceFile
              + ". Missing import: " + targetDtoClass
      );
    }

    if (hasImport(content, generatedDtoClass)) {
      throw new MojoExecutionException(
          "Patch failed for " + resourceFile
              + ". Generated DTO import still present: " + generatedDtoClass
      );
    }

    String methodBlock = findMethodBlock(content, fix);

    if (failOnRemainingInputStream && methodBlock.contains(" InputStream ")) {
      throw new MojoExecutionException(
          "Patch failed for " + resourceFile
              + ". Operation " + fix.getOperationId()
              + " still uses InputStream."
      );
    }

    String expectedParameter =
        "@Valid @NotNull " + simpleName + " " + fix.getParameterName();

    if (!methodBlock.contains(expectedParameter)) {
      throw new MojoExecutionException(
          "Patch failed for " + resourceFile
              + ". Operation " + fix.getOperationId()
              + " does not contain expected parameter: " + expectedParameter
      );
    }
  }

  private String findMethodBlock(String content, RequestBodyFix fix)
      throws MojoExecutionException {

    String marker = "operationId = \"" + fix.getOperationId() + "\"";
    int markerIndex = content.indexOf(marker);

    if (markerIndex < 0) {
      throw new MojoExecutionException(
          "Cannot find operationId=" + fix.getOperationId()
      );
    }

    int methodEnd = content.indexOf(");", markerIndex);

    if (methodEnd < 0) {
      throw new MojoExecutionException(
          "Cannot find method end for operationId=" + fix.getOperationId()
      );
    }

    return content.substring(markerIndex, methodEnd + 2);
  }

  private String ensureImport(String content, String classOrImportLine) {
    String className = normalizeImportClassName(classOrImportLine);

    if (className == null || className.isBlank()) {
      return content;
    }

    if (hasImport(content, className)) {
      return content;
    }

    String lineSeparator = detectLineSeparator(content);
    String importLine = "import " + className + ";";

    Matcher firstImport = IMPORT_DECLARATION.matcher(content);

    if (firstImport.find()) {
      return content.substring(0, firstImport.start())
          + importLine
          + lineSeparator
          + content.substring(firstImport.start());
    }

    Matcher packageMatcher = PACKAGE_DECLARATION.matcher(content);

    if (packageMatcher.find()) {
      return content.substring(0, packageMatcher.end())
          + lineSeparator
          + importLine
          + lineSeparator
          + content.substring(packageMatcher.end());
    }

    return importLine + lineSeparator + content;
  }

  private String removeImport(String content, String classOrImportLine) {
    String className = normalizeImportClassName(classOrImportLine);

    if (className == null || className.isBlank()) {
      return content;
    }

    return content.replaceAll(
        "(?m)^import\\s+" + Pattern.quote(className) + ";\\R?",
        ""
    );
  }

  private boolean hasImport(String content, String classOrImportLine) {
    String className = normalizeImportClassName(classOrImportLine);

    if (className == null || className.isBlank()) {
      return false;
    }

    Pattern exactImport = Pattern.compile(
        "(?m)^import\\s+" + Pattern.quote(className) + ";\\s*$"
    );

    return exactImport.matcher(content).find();
  }

  private String normalizeImportClassName(String classOrImportLine) {
    if (classOrImportLine == null) {
      return null;
    }

    String value = classOrImportLine.trim();

    if (value.startsWith("import ")) {
      value = value.substring("import ".length()).trim();
    }

    while (value.endsWith(";")) {
      value = value.substring(0, value.length() - 1).trim();
    }

    return value;
  }

  private boolean hasInvalidDoubleSemicolonImport(String content) {
    Pattern invalidImport = Pattern.compile("(?m)^import\\s+[^;]+;;\\s*$");
    return invalidImport.matcher(content).find();
  }

  private String detectLineSeparator(String content) {
    return content.contains("\r\n") ? "\r\n" : "\n";
  }

  private String read(File file) throws MojoExecutionException {
    try {
      return Files.readString(file.toPath(), StandardCharsets.UTF_8);
    } catch (IOException ex) {
      throw new MojoExecutionException("Cannot read file: " + file, ex);
    }
  }

  private void write(File file, String content) throws MojoExecutionException {
    try {
      Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
    } catch (IOException ex) {
      throw new MojoExecutionException("Cannot write file: " + file, ex);
    }
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}