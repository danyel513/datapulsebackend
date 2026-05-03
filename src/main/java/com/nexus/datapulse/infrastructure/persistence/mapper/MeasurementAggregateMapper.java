package com.nexus.datapulse.infrastructure.persistence.mapper;

import com.nexus.datapulse.domain.measurement.MeasurementAggregate;
import com.nexus.datapulse.infrastructure.persistence.entity.DataSourceEntity;
import com.nexus.datapulse.infrastructure.persistence.entity.MeasurementAggregateEntity;
import com.nexus.datapulse.infrastructure.persistence.entity.MetricDefinitionEntity;

public final class MeasurementAggregateMapper {

    private MeasurementAggregateMapper() {
    }

    public static MeasurementAggregate toModel(MeasurementAggregateEntity entity) {
        if (entity == null) {
            return null;
        }

        return new MeasurementAggregate(
                entity.getId(),
                entity.getDataSource().getId(),
                MetricDefinitionMapper.toModel(entity.getMetricDefinition()),
                entity.getAggregationLevel(),
                entity.getWindowStart(),
                entity.getWindowEnd(),
                entity.getSampleCount(),
                entity.getValidSampleCount(),
                entity.getInvalidSampleCount(),
                entity.getMinValue(),
                entity.getMaxValue(),
                entity.getAvgValue(),
                entity.getLastValue()
        );
    }

    public static MeasurementAggregateEntity toEntity(
            MeasurementAggregate model,
            DataSourceEntity dataSourceEntity,
            MetricDefinitionEntity metricDefinitionEntity
    ) {
        if (model == null) {
            return null;
        }

        MeasurementAggregateEntity entity = new MeasurementAggregateEntity();
        entity.setId(model.id());
        setCommonFields(model, entity, dataSourceEntity, metricDefinitionEntity);

        return entity;
    }

    public static void updateEntity(
            MeasurementAggregate model,
            MeasurementAggregateEntity entity,
            DataSourceEntity dataSourceEntity,
            MetricDefinitionEntity metricDefinitionEntity
    ) {
        if (model == null || entity == null) {
            return;
        }

        setCommonFields(model, entity, dataSourceEntity, metricDefinitionEntity);
    }

    private static void setCommonFields(
            MeasurementAggregate model,
            MeasurementAggregateEntity entity,
            DataSourceEntity dataSourceEntity,
            MetricDefinitionEntity metricDefinitionEntity
    ) {
        entity.setDataSource(dataSourceEntity);
        entity.setMetricDefinition(metricDefinitionEntity);
        entity.setAggregationLevel(model.aggregationLevel());
        entity.setWindowStart(model.windowStart());
        entity.setWindowEnd(model.windowEnd());
        entity.setSampleCount(model.sampleCount());
        entity.setValidSampleCount(model.validSampleCount());
        entity.setInvalidSampleCount(model.invalidSampleCount());
        entity.setMinValue(model.minValue());
        entity.setMaxValue(model.maxValue());
        entity.setAvgValue(model.avgValue());
        entity.setLastValue(model.lastValue());
    }
}
