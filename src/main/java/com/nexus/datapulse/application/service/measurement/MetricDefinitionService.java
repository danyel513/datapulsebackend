package com.nexus.datapulse.application.service.measurement;

import com.nexus.datapulse.domain.measurement.MetricDataType;
import com.nexus.datapulse.domain.measurement.MetricDefinition;
import com.nexus.datapulse.infrastructure.persistence.entity.MetricDefinitionEntity;
import com.nexus.datapulse.infrastructure.persistence.mapper.MetricDefinitionMapper;
import com.nexus.datapulse.infrastructure.persistence.repository.MetricDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MetricDefinitionService {

    private final MetricDefinitionRepository metricDefinitionRepository;

    public MetricDefinition getOrCreate(
            String metricKey,
            String displayName,
            String unit,
            MetricDataType dataType,
            Double minValidValue,
            Double maxValidValue
    ) {
        return metricDefinitionRepository.findByMetricKey(metricKey)
                .map(MetricDefinitionMapper::toModel)
                .orElseGet(() -> create(
                        metricKey,
                        displayName,
                        unit,
                        dataType,
                        minValidValue,
                        maxValidValue
                ));
    }

    public MetricDefinition create(
            String metricKey,
            String displayName,
            String unit,
            MetricDataType dataType,
            Double minValidValue,
            Double maxValidValue
    ) {
        MetricDefinition model = new MetricDefinition(
                null,
                metricKey,
                displayName,
                unit,
                dataType,
                minValidValue,
                maxValidValue,
                true
        );

        MetricDefinitionEntity savedEntity =
                metricDefinitionRepository.save(MetricDefinitionMapper.toEntity(model));

        return MetricDefinitionMapper.toModel(savedEntity);
    }

    public Optional<MetricDefinition> findById(UUID id) {
        return metricDefinitionRepository.findById(id)
                .map(MetricDefinitionMapper::toModel);
    }

    public Optional<MetricDefinition> findByMetricKey(String metricKey) {
        return metricDefinitionRepository.findByMetricKey(metricKey)
                .map(MetricDefinitionMapper::toModel);
    }

    public List<MetricDefinition> findAllEnabled() {
        return metricDefinitionRepository.findAllByEnabledTrue()
                .stream()
                .map(MetricDefinitionMapper::toModel)
                .toList();
    }

    public boolean existsByMetricKey(String metricKey) {
        return metricDefinitionRepository.existsByMetricKey(metricKey);
    }

    public MetricDefinition update(MetricDefinition model) {
        MetricDefinitionEntity entity = metricDefinitionRepository.findById(model.id())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Metric definition not found: " + model.id()
                ));

        MetricDefinitionMapper.updateEntity(model, entity);
        MetricDefinitionEntity savedEntity = metricDefinitionRepository.save(entity);

        return MetricDefinitionMapper.toModel(savedEntity);
    }
}
