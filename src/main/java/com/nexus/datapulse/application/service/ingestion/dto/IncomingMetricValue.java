package com.nexus.datapulse.application.service.ingestion.dto;

public record IncomingMetricValue(
        String name,
        Double value,
        String unit
) {}
