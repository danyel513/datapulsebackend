package com.nexus.datapulse.web.mapper;

import com.nexus.datapulse.domain.datasource.model.DataSource;
import com.nexus.datapulse.domain.datasource.model.DataSourceStatus;
import com.nexus.datapulse.web.dto.datasource.CreateDataSourceRequest;
import com.nexus.datapulse.web.dto.datasource.DataSourceResponse;

public class DataSourceWebMapper {

    private DataSourceWebMapper() {
    }

    public static DataSource toModel(CreateDataSourceRequest request) {
        return new DataSource(
                null,
                request.userId(),
                request.sourceKey(),
                request.name(),
                request.type(),
                request.description(),
                request.host(),
                request.port(),
                request.protocol(),
                DataSourceStatus.OFFLINE,
                null
        );
    }

    public static DataSourceResponse toResponse(DataSource dataSource) {
        return new DataSourceResponse(
                dataSource.getId(),
                dataSource.getUserId(),
                dataSource.getSourceKey(),
                dataSource.getName(),
                dataSource.getType(),
                dataSource.getDescription(),
                dataSource.getHost(),
                dataSource.getPort(),
                dataSource.getProtocol(),
                dataSource.getStatus(),
                dataSource.getLastSeenAt()
        );
    }
}
