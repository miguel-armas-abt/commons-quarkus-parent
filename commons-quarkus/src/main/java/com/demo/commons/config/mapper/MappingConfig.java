package com.demo.commons.config.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
    componentModel = "cdi",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public class MappingConfig {
}
