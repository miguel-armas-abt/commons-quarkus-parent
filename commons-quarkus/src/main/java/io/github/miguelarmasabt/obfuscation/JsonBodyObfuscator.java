package io.github.miguelarmasabt.obfuscation;

import io.github.miguelarmasabt.properties.ConfigurationBaseProperties;
import io.github.miguelarmasabt.properties.rest.RestProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static io.github.miguelarmasabt.obfuscation.MaskingWildcard.OBFUSCATION_MASK;

@ApplicationScoped
public class JsonBodyObfuscator {

  private static final String JSON_SEGMENT_SPLITTER_REGEX = "\\.";

  private static final Logger log = Logger.getLogger(JsonBodyObfuscator.class);

  private final ObjectMapper objectMapper;
  private final RestProperties.Obfuscation obfuscation;

  public JsonBodyObfuscator(ObjectMapper objectMapper,
                            ConfigurationBaseProperties properties) {
    this.objectMapper = objectMapper;
    this.obfuscation = properties.rest().obfuscation();
  }

  private static void obfuscateFieldRecursively(JsonNode jsonNode, String[] fieldPathSegments, int index) {
    if (wereAllFieldsProcessed(fieldPathSegments, index)) return;

    String segment = fieldPathSegments[index];

    if (segment.contains(MaskingWildcard.ARRAY_WILDCARD)) {
      String arrayKey = segment.substring(0, segment.indexOf('['));
      JsonNode arrayNode = jsonNode.get(arrayKey);
      processArray(arrayNode, fieldPathSegments, index);
    } else {
      processObject(jsonNode, segment, fieldPathSegments, index);
    }
  }

  public String process(String jsonBody) {
    Set<String> bodyFields = obfuscation.bodyFields();
    if (StringUtils.isEmpty(jsonBody) || bodyFields.isEmpty())
      return jsonBody;

    try {
      JsonNode jsonNode = objectMapper.readTree(jsonBody);

      bodyFields.forEach(fieldPath -> {
        String[] fieldPathSegments = fieldPath.split(JSON_SEGMENT_SPLITTER_REGEX);
        int incrementalIndex = 0;
        obfuscateFieldRecursively(jsonNode, fieldPathSegments, incrementalIndex);
      });

      return objectMapper.writeValueAsString(jsonNode);
    } catch (Exception exception) {
      log.warn("Json object cannot be serialized", exception);
      return jsonBody;
    }
  }

  private static boolean wereAllFieldsProcessed(String[] fieldPathSegments, int index) {
    return index >= fieldPathSegments.length;
  }

  private static void processArray(JsonNode arrayNode, String[] fieldPathSegments, int index) {
    Optional.ofNullable(arrayNode).ifPresent(array -> IntStream.range(0, array.size())
        .mapToObj(array::get)
        .filter(JsonNode::isObject)
        .forEach(jsonObject -> obfuscateFieldRecursively(jsonObject, fieldPathSegments, index + 1)));
  }

  private static void processObject(JsonNode jsonNode, String segment, String[] fieldPathSegments, int index) {
    if (wasLastSegment(fieldPathSegments, index)) {
      obfuscateTargetField(jsonNode, segment);
    } else if (jsonNode.has(segment) && jsonNode.get(segment).isObject()) {
      obfuscateFieldRecursively(jsonNode.get(segment), fieldPathSegments, index + 1);
    }
  }

  private static boolean wasLastSegment(String[] fieldPathSegments, int index) {
    return index == fieldPathSegments.length - 1;
  }

  private static void obfuscateTargetField(JsonNode jsonNode, String field) {
    if (jsonNode.has(field) && jsonNode.get(field).isTextual()) {
      ((ObjectNode) jsonNode).put(field, partiallyObfuscate(jsonNode.get(field).asText()));
    }
  }

  private static String partiallyObfuscate(String value) {
    return value.length() > 6
        ? value.substring(0, 3) + OBFUSCATION_MASK + value.substring(value.length() - 3)
        : value;
  }
}
