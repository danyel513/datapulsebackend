package com.nexus.datapulse.web.dto.datasource;

import com.nexus.datapulse.domain.datasource.DataSourceStatus;

import java.time.Instant;
import java.util.UUID;

public record DataSourceResponse(
        UUID id,
        UUID userId,
        String sourceKey,
        String name,
        String type,
        String description,
        String host,
        Integer port,
        String protocol,
        DataSourceStatus status,
        Instant lastSeenAt
) {}
