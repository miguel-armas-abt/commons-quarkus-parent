package io.github.miguelarmasabt.error.handler;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.error.mapper.DefaultErrorMapper;
import io.github.miguelarmasabt.error.mapper.ExceptionResponseMapper;
import io.github.miguelarmasabt.error.mapper.ExceptionResponseMapperStrategy;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.Optional;

@RequiredArgsConstructor
public class ExceptionHandler {

  private final ExceptionResponseMapperStrategy responseMapperStrategy;
  private final DefaultErrorMapper defaultMapper;

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
    ErrorDto error = defaultMapper.defaultError();
    Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;

    Optional<ExceptionResponseMapper> responseMapperOpt = responseMapperStrategy.selectStrategy(throwable);
    if (responseMapperOpt.isPresent()) {
      ExceptionResponseMapper responseMapper = responseMapperOpt.get();
      error = responseMapper.map(throwable);
      status = responseMapper.status(throwable);
    }

    return RestResponse.status(status, error);
  }
}
