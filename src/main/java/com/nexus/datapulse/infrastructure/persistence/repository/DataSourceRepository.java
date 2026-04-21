package com.nexus.datapulse.infrastructure.persistence.repository;

import com.nexus.datapulse.infrastructure.persistence.entity.DataSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataSourceRepository extends JpaRepository<DataSourceEntity, UUID> {
    Optional<DataSourceEntity> findBySourceKey(String sourceKey);
    List<DataSourceEntity> findAllByUserId(UUID userId);
    boolean existsBySourceKey(String sourceKey);
}
