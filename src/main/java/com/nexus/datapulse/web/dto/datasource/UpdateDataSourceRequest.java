package com.nexus.datapulse.web.dto.datasource;

import jakarta.validation.constraints.NotBlank;

public record UpdateDataSourceRequest(
        @NotBlank String name,
        @NotBlank String type,
        String description,
        String host,
        Integer port,
        String protocol
) {}
