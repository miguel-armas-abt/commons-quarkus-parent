package com.demo.commons.config.reflection;

import com.demo.commons.errors.dto.ErrorOrigin;
import com.demo.commons.errors.dto.ErrorDto;
import com.demo.commons.validations.headers.DefaultHeaders;
import io.quarkus.runtime.annotations.RegisterForReflection;

//POJOs not referenced by JPA entities or JAX-RS endpoints
@RegisterForReflection(targets = {
    DefaultHeaders.class,
    ErrorDto.class,
    ErrorOrigin.class,
})
public class DefaultReflectionConfig {
}
