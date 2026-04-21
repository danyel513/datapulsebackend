package com.nexus.datapulse.web.dto.datasource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateDataSourceRequest(
        @NotNull UUID userId,
        @NotBlank String sourceKey,
        @NotBlank String name,
        @NotBlank String type,
        String description,
        String host,
        Integer port,
        String protocol
) {}
