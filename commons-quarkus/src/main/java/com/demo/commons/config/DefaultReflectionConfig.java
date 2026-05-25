package com.demo.commons.config;

import com.demo.commons.error.dto.ErrorDto;
import com.demo.commons.error.dto.ErrorOrigin;
import io.quarkus.runtime.annotations.RegisterForReflection;

//POJOs not referenced by JPA entities or JAX-RS endpoints
@RegisterForReflection(targets = {
    ErrorDto.class,
    ErrorOrigin.class,
})
public class DefaultReflectionConfig {
}
