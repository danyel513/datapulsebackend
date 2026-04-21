package com.nexus.datapulse.infrastructure.persistence.mapper;

import com.nexus.datapulse.domain.datasource.model.DataSource;
import com.nexus.datapulse.infrastructure.persistence.entity.DataSourceEntity;
import com.nexus.datapulse.infrastructure.persistence.entity.UserEntity;

public class DataSourceMapper {

    private DataSourceMapper() {}

    public static DataSource toModel(DataSourceEntity entity) {
        if (entity == null) {
            return null;
        }

        return new DataSource(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getSourceKey(),
                entity.getName(),
                entity.getType(),
                entity.getDescription(),
                entity.getHost(),
                entity.getPort(),
                entity.getProtocol(),
                entity.getStatus(),
                entity.getLastSeenAt()
        );
    }

    public static DataSourceEntity toEntity(DataSource model) {
        if (model == null) {
            return null;
        }

        DataSourceEntity entity = new DataSourceEntity();
        entity.setId(model.getId());
        setCommonFields(model, entity);

        return entity;
    }

    public static void updateEntity(DataSource model, DataSourceEntity entity) {
        setCommonFields(model, entity);
    }

    private static void setCommonFields(DataSource model, DataSourceEntity entity) {
        entity.setSourceKey(model.getSourceKey());
        entity.setName(model.getName());
        entity.setType(model.getType());
        entity.setDescription(model.getDescription());
        entity.setHost(model.getHost());
        entity.setPort(model.getPort());
        entity.setProtocol(model.getProtocol());
        entity.setStatus(model.getStatus());
        entity.setLastSeenAt(model.getLastSeenAt());

        if (model.getUserId() != null) {
            UserEntity user = new UserEntity();
            user.setId(model.getUserId());
            entity.setUser(user);
        }
    }
}
