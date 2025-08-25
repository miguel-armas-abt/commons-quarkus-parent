package io.github.miguelarmasabt.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import io.github.miguelarmasabt.constants.Strings;
import io.github.miguelarmasabt.error.exceptions.ProcessingFailedException;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static io.github.miguelarmasabt.error.exceptions.ProcessingFailedException.PROCESSING_FAILED;

@ApplicationScoped
@RequiredArgsConstructor
public class JsonSerializer {

  private final ObjectMapper objectMapper;

  private static final Logger log = Logger.getLogger(JsonSerializer.class);

  protected InputStream getResourceAsStream(String filePath) {
    String normalizedPath = filePath.startsWith("/") ? filePath.substring(1) : filePath;
    return Thread.currentThread().getContextClassLoader().getResourceAsStream(normalizedPath);
  }

  protected <T> T readValue(InputStream inputStream, Class<T> objectClass) {
    try (InputStream is = inputStream) {
      return objectMapper.readValue(is, objectClass);
    } catch (IOException exception) {
      throw new ProcessingFailedException(PROCESSING_FAILED, exception, "The input stream cannot be converted to JSON");
    }
  }

  protected <T> List<T> readList(InputStream inputStream, Class<T> objectClass) {
    try (InputStream is = inputStream) {
      CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, objectClass);
      return objectMapper.readValue(is, collectionType);
    } catch (IOException exception) {
      throw new ProcessingFailedException(PROCESSING_FAILED, exception, "The input stream cannot be converted to JSON array");
    }
  }

  public <T> T readElementFromFile(String filePath, Class<T> objectClass) {
    return readValue(getResourceAsStream(filePath), objectClass);
  }

  public <T> List<T> readListFromFile(String filePath, Class<T> objectClass) {
    return readList(getResourceAsStream(filePath), objectClass);
  }

  public String toJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException exception) {
      log.warn("The object cannot be converted to JSON", exception);
      return Strings.EMPTY;
    }
  }
}
