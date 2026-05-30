package io.github.miguelarmasabt.service;

import io.github.miguelarmasabt.model.RequestBodyFix;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

public class GeneratedResourcePatcher {

  private final Log log;

  public GeneratedResourcePatcher(Log log) {
    this.log = log;
  }

  public void patch(File resourceDir,
                    List<RequestBodyFix> fixes,
                    boolean failOnRemainingInputStream,
                    GeneratedResourceScanner scanner)
      throws MojoExecutionException {

    Map<File, List<RequestBodyFix>> fixesByResourceFile =
        groupByResourceFile(resourceDir, fixes, scanner);

    for (Map.Entry<File, List<RequestBodyFix>> entry : fixesByResourceFile.entrySet()) {
      patchResourceFile(entry.getKey(), entry.getValue(), failOnRemainingInputStream);
    }
  }

  private Map<File, List<RequestBodyFix>> groupByResourceFile(File resourceDir,
                                                              List<RequestBodyFix> fixes,
                                                              GeneratedResourceScanner scanner)
      throws MojoExecutionException {

    Map<File, List<RequestBodyFix>> grouped = new LinkedHashMap<>();

    for (RequestBodyFix fix : fixes) {
      File resourceFile = scanner.resolveResourceFile(resourceDir, fix);
      grouped.computeIfAbsent(resourceFile, key -> new java.util.ArrayList<>()).add(fix);
    }

    return grouped;
  }

  private void patchResourceFile(File resourceFile,
                                 List<RequestBodyFix> fixes,
                                 boolean failOnRemainingInputStream)
      throws MojoExecutionException {

    String content = read(resourceFile);
    String original = content;

    for (RequestBodyFix fix : fixes) {
      content = patchMethodBodyParameter(content, fix);
      validateMethod(content, resourceFile, fix, failOnRemainingInputStream);
    }

    if (!original.equals(content)) {
      write(resourceFile, content);
      log.info("Patched " + resourceFile.getName()
          + " operations="
          + fixes.stream()
              .map(RequestBodyFix::getOperationId)
              .collect(Collectors.joining(", ")));
    } else {
      log.warn("No changes applied to " + resourceFile
          + ". Check generated signatures and patch configuration.");
    }
  }

  private String patchMethodBodyParameter(String content, RequestBodyFix fix)
      throws MojoExecutionException {

    MethodRegion methodRegion = findMethodRegion(content, fix);
    String methodBlock = methodRegion.content();

    String parameterName = fix.getParameterName();

    String targetParameter =
        "@jakarta.validation.Valid @jakarta.validation.constraints.NotNull "
            + fix.getTargetClass()
            + " "
            + parameterName;

    String patchedBlock = methodBlock;

    patchedBlock = replaceRequestBodyParameter(
        patchedBlock,
        "@jakarta\\.validation\\.Valid\\s+@jakarta\\.validation\\.constraints\\.NotNull\\s+",
        parameterName,
        targetParameter
    );

    patchedBlock = replaceRequestBodyParameter(
        patchedBlock,
        "@Valid\\s+@NotNull\\s+",
        parameterName,
        targetParameter
    );

    patchedBlock = replaceRequestBodyParameter(
        patchedBlock,
        "@NotNull\\s+",
        parameterName,
        targetParameter
    );

    if (methodBlock.equals(patchedBlock)) {
      throw new MojoExecutionException(
          "Could not patch request body parameter for operationId="
              + fix.getOperationId()
              + ". Expected parameter name: "
              + parameterName
              + ". Method block was: "
              + methodBlock
      );
    }

    return content.substring(0, methodRegion.start())
        + patchedBlock
        + content.substring(methodRegion.end());
  }

  private String replaceRequestBodyParameter(String methodBlock,
                                             String annotationRegex,
                                             String parameterName,
                                             String targetParameter) {

    String typeRegex = "[a-zA-Z_$][a-zA-Z0-9_$.]*(?:<[^>]+>)?";
    String regex = annotationRegex
        + typeRegex
        + "\\s+"
        + Pattern.quote(parameterName)
        + "(?=\\s*[,\\)])";

    return Pattern
        .compile(regex, Pattern.MULTILINE | Pattern.DOTALL)
        .matcher(methodBlock)
        .replaceAll(Matcher.quoteReplacement(targetParameter));
  }

  private void validateMethod(String content,
                              File resourceFile,
                              RequestBodyFix fix,
                              boolean failOnRemainingInputStream)
      throws MojoExecutionException {

    MethodRegion methodRegion = findMethodRegion(content, fix);
    String methodBlock = methodRegion.content();

    if (failOnRemainingInputStream && methodBlock.contains(" InputStream ")) {
      throw new MojoExecutionException(
          "Patch failed for "
              + resourceFile
              + ". Operation "
              + fix.getOperationId()
              + " still uses InputStream."
      );
    }

    String expected =
        "@jakarta.validation.Valid @jakarta.validation.constraints.NotNull "
            + fix.getTargetClass()
            + " "
            + fix.getParameterName();

    if (!methodBlock.contains(expected)) {
      throw new MojoExecutionException(
          "Patch failed for "
              + resourceFile
              + ". Operation "
              + fix.getOperationId()
              + " does not contain expected request body parameter: "
              + expected
      );
    }
  }

  private MethodRegion findMethodRegion(String content, RequestBodyFix fix)
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

    return new MethodRegion(markerIndex, methodEnd + 2, content.substring(markerIndex, methodEnd + 2));
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

  private record MethodRegion(int start, int end, String content) {
  }
}
