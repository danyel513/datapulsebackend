package com.nexus.datapulse.infrastructure.persistence.mapper;

import com.nexus.datapulse.domain.measurement.MetricDefinition;
import com.nexus.datapulse.infrastructure.persistence.entity.MetricDefinitionEntity;

public final class MetricDefinitionMapper {

    private MetricDefinitionMapper() {
    }

    public static MetricDefinition toModel(MetricDefinitionEntity entity) {
        if (entity == null) {
            return null;
        }

        return new MetricDefinition(
                entity.getId(),
                entity.getMetricKey(),
                entity.getDisplayName(),
                entity.getUnit(),
                entity.getDataType(),
                entity.getMinValidValue(),
                entity.getMaxValidValue(),
                Boolean.TRUE.equals(entity.getEnabled())
        );
    }

    public static MetricDefinitionEntity toEntity(MetricDefinition model) {
        if (model == null) {
            return null;
        }

        MetricDefinitionEntity entity = new MetricDefinitionEntity();
        entity.setId(model.id());
        setCommonFields(model, entity);

        return entity;
    }

    public static void updateEntity(MetricDefinition model, MetricDefinitionEntity entity) {
        if (model == null || entity == null) {
            return;
        }

        setCommonFields(model, entity);
    }

    private static void setCommonFields(MetricDefinition model, MetricDefinitionEntity entity) {
        entity.setMetricKey(model.metricKey());
        entity.setDisplayName(model.displayName());
        entity.setUnit(model.unit());
        entity.setDataType(model.dataType());
        entity.setMinValidValue(model.minValidValue());
        entity.setMaxValidValue(model.maxValidValue());
        entity.setEnabled(model.enabled());
    }
}
