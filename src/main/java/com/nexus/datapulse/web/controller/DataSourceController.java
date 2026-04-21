package com.nexus.datapulse.web.controller;

import com.nexus.datapulse.application.service.datasource.DataSourceService;
import com.nexus.datapulse.domain.datasource.model.DataSource;
import com.nexus.datapulse.web.dto.datasource.CreateDataSourceRequest;
import com.nexus.datapulse.web.dto.datasource.UpdateDataSourceRequest;
import com.nexus.datapulse.web.dto.datasource.DataSourceResponse;
import com.nexus.datapulse.web.mapper.DataSourceWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/data-sources")
@RequiredArgsConstructor
public class DataSourceController {
    private final DataSourceService dataSourceService;

    @PostMapping
    public ResponseEntity<DataSourceResponse> create(@Valid @RequestBody CreateDataSourceRequest request) {
        DataSource created = dataSourceService.create(DataSourceWebMapper.toModel(request));
        DataSourceResponse response = DataSourceWebMapper.toResponse(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataSourceResponse> getById(@PathVariable UUID id) {
        DataSourceResponse response = DataSourceWebMapper.toResponse(dataSourceService.getById(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DataSourceResponse>> getAllByUserId(@RequestParam UUID userId) {
        List<DataSourceResponse> response = dataSourceService.getAllByUserId(userId)
                .stream()
                .map(DataSourceWebMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-source-key/{sourceKey}")
    public ResponseEntity<DataSourceResponse> getBySourceKey(@PathVariable String sourceKey) {
        DataSourceResponse response = DataSourceWebMapper.toResponse(dataSourceService.getBySourceKey(sourceKey));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataSourceResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDataSourceRequest request
    ) {
        DataSource updated = dataSourceService.update(
                id,
                request.name(),
                request.type(),
                request.description(),
                request.host(),
                request.port(),
                request.protocol()
        );

        return ResponseEntity.ok(DataSourceWebMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/mark-online")
    public ResponseEntity<Void> markOnline(@PathVariable UUID id) {
        dataSourceService.markOnline(id, Instant.now());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/mark-offline")
    public ResponseEntity<Void> markOffline(@PathVariable UUID id) {
        dataSourceService.markOffline(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/mark-warning")
    public ResponseEntity<Void> markWarning(@PathVariable UUID id) {
        dataSourceService.markWarning(id);
        return ResponseEntity.noContent().build();
    }
}
