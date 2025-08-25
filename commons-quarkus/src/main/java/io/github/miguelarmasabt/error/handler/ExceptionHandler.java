package io.github.miguelarmasabt.error.handler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.error.helper.RequestLocaleResolver;
import io.github.miguelarmasabt.error.mapper.ErrorMapper;
import io.github.miguelarmasabt.error.mapper.ExceptionResponseMapper;
import io.github.miguelarmasabt.error.mapper.ExceptionResponseMapperStrategy;
import io.github.miguelarmasabt.serialize.JsonSerializer;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.Locale;
import java.util.Optional;

@RequiredArgsConstructor
public class ExceptionHandler {

  private final ExceptionResponseMapperStrategy responseMapperStrategy;
  private final ErrorMapper defaultMapper;
  private final RequestLocaleResolver requestLocaleResolver;
  private final JsonSerializer jsonSerializer;

  private final static Logger log = Logger.getLogger(ExceptionHandler.class);

  @ServerExceptionMapper
  public RestResponse<ErrorDto> toResponse(Throwable throwable) {
    log.error(throwable.getMessage(), throwable);
    return resolve(throwable);
  }

  @ServerExceptionMapper
  public RestResponse<ErrorDto> toResponse(ConstraintViolationException throwable) {
    log.error(throwable.getMessage(), throwable);
    return resolve(throwable);
  }

  @ServerExceptionMapper
  public RestResponse<ErrorDto> toResponse(MismatchedInputException throwable) {
    log.error(throwable.getOriginalMessage(), throwable);
    return resolve(throwable);
  }

  private RestResponse<ErrorDto> resolve(Throwable throwable) {
    Locale locale = requestLocaleResolver.resolve();
    ErrorDto error = defaultMapper.defaultError(locale);
    Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;

    Optional<ExceptionResponseMapper> responseMapperOpt = responseMapperStrategy.selectStrategy(throwable);
    if (responseMapperOpt.isPresent()) {
      ExceptionResponseMapper responseMapper = responseMapperOpt.get();
      error = responseMapper.map(throwable, locale);
      status = responseMapper.selectStatus(throwable);
    }

    log.error(jsonSerializer.toJson(error));
    return RestResponse.status(status, error);
  }
}
