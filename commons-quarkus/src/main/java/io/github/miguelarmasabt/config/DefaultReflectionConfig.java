package io.github.miguelarmasabt.config;

import io.github.miguelarmasabt.error.dto.ErrorDto;
import io.github.miguelarmasabt.error.dto.ErrorOrigin;
import io.quarkus.runtime.annotations.RegisterForReflection;

//POJOs not referenced by JPA entities or JAX-RS endpoints
@RegisterForReflection(targets = {
    ErrorDto.class,
    ErrorOrigin.class,
})
public class DefaultReflectionConfig {
}
