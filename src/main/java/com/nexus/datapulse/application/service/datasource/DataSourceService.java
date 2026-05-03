package com.nexus.datapulse.application.service.datasource;

import com.nexus.datapulse.common.exception.EntityAlreadyExists;
import com.nexus.datapulse.domain.datasource.DataSource;
import com.nexus.datapulse.infrastructure.persistence.entity.DataSourceEntity;
import com.nexus.datapulse.infrastructure.persistence.mapper.DataSourceMapper;
import com.nexus.datapulse.infrastructure.persistence.repository.DataSourceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataSourceService {

    public static final String DATA_SOURCE_NOT_FOUND = "Data source not found";
    public static final String DATA_SOURCE_ALREADY_EXISTS = "Data source with this source key already exists.";

    private final DataSourceRepository dataSourceRepository;

    @Transactional
    public DataSource create(DataSource dataSource) {
        validateForCreate(dataSource);

        if (dataSourceRepository.existsBySourceKey(dataSource.getSourceKey())) {
            throw new EntityAlreadyExists(DATA_SOURCE_ALREADY_EXISTS);
        }

        DataSourceEntity savedEntity = dataSourceRepository.save(DataSourceMapper.toEntity(dataSource));
        return DataSourceMapper.toModel(savedEntity);
    }

    public DataSource getById(UUID id) {
        return dataSourceRepository.findById(id)
                .map(DataSourceMapper::toModel)
                .orElseThrow(() -> new EntityNotFoundException(DATA_SOURCE_NOT_FOUND));
    }

    public DataSource getBySourceKey(String sourceKey) {
        return dataSourceRepository.findBySourceKey(sourceKey)
                .map(DataSourceMapper::toModel)
                .orElseThrow(() -> new EntityNotFoundException(DATA_SOURCE_NOT_FOUND));
    }

    public List<DataSource> getAllByUserId(UUID userId) {
        return dataSourceRepository.findAllByUserId(userId)
                .stream()
                .map(DataSourceMapper::toModel)
                .toList();
    }

    @Transactional
    public DataSource update(UUID id, String name, String type, String description,
                             String host, Integer port, String protocol) {
        DataSourceEntity existingEntity = dataSourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(DATA_SOURCE_NOT_FOUND));

        DataSource dataSource = DataSourceMapper.toModel(existingEntity);
        dataSource.updateDetails(name, type, description);
        dataSource.updateConnectionDetails(host, port, protocol);

        DataSourceMapper.updateEntity(dataSource, existingEntity);

        DataSourceEntity updatedEntity = dataSourceRepository.save(existingEntity);
        return DataSourceMapper.toModel(updatedEntity);
    }

    @Transactional
    public void markOnline(UUID dataSourceId, Instant seenAt) {
        DataSourceEntity existingEntity = dataSourceRepository.findById(dataSourceId)
                .orElseThrow(() -> new EntityNotFoundException(DATA_SOURCE_NOT_FOUND));

        DataSource dataSource = DataSourceMapper.toModel(existingEntity);
        dataSource.markOnline(seenAt);

        DataSourceMapper.updateEntity(dataSource, existingEntity);
        dataSourceRepository.save(existingEntity);
    }

    @Transactional
    public void markOffline(UUID dataSourceId) {
        DataSourceEntity existingEntity = dataSourceRepository.findById(dataSourceId)
                .orElseThrow(() -> new EntityNotFoundException(DATA_SOURCE_NOT_FOUND));

        DataSource dataSource = DataSourceMapper.toModel(existingEntity);
        dataSource.markOffline();

        DataSourceMapper.updateEntity(dataSource, existingEntity);
        dataSourceRepository.save(existingEntity);
    }

    @Transactional
    public void markWarning(UUID dataSourceId) {
        DataSourceEntity existingEntity = dataSourceRepository.findById(dataSourceId)
                .orElseThrow(() -> new EntityNotFoundException(DATA_SOURCE_NOT_FOUND));

        DataSource dataSource = DataSourceMapper.toModel(existingEntity);
        dataSource.markWarning();

        DataSourceMapper.updateEntity(dataSource, existingEntity);
        dataSourceRepository.save(existingEntity);
    }

    private void validateForCreate(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source must not be null.");
        }
        if (dataSource.getUserId() == null) {
            throw new IllegalArgumentException("User id must not be null.");
        }
        if (dataSource.getSourceKey() == null || dataSource.getSourceKey().isBlank()) {
            throw new IllegalArgumentException("Source key must not be blank.");
        }
        if (dataSource.getName() == null || dataSource.getName().isBlank()) {
            throw new IllegalArgumentException("Name must not be blank.");
        }
        if (dataSource.getType() == null || dataSource.getType().isBlank()) {
            throw new IllegalArgumentException("Type must not be blank.");
        }
        if (dataSource.getStatus() == null) {
            throw new IllegalArgumentException("Status must not be null.");
        }
    }
}
