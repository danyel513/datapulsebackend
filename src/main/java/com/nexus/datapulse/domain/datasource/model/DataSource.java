package com.nexus.datapulse.domain.datasource.model;

import com.nexus.datapulse.common.exception.SourceKeySetException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DataSource {
    @Setter private UUID id;
    @Setter private UUID userId;
    private String sourceKey;
    private String name;
    private String type;
    private String description;
    private String host;
    private Integer port;
    private String protocol;
    private DataSourceStatus status;
    private Instant lastSeenAt;

    public void updateConnectionDetails(String host, Integer port, String protocol) {
        if (host != null && host.isBlank()) {
            throw new IllegalArgumentException("host must not be blank");
        }
        if (port != null && port <= 0) {
            throw new IllegalArgumentException("port must be greater than 0");
        }
        if (protocol != null && protocol.isBlank()) {
            throw new IllegalArgumentException("protocol must not be blank");
        }

        this.host = host;
        this.port = port;
        this.protocol = protocol;
    }

    public void updateDetails(String name, String type, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be null or blank");
        }
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("type must not be null or blank");
        }

        this.name = name;
        this.type = type;
        this.description = description;
    }

    public void markOnline(Instant seenAt) {
        if (seenAt == null) {
            throw new IllegalArgumentException("seenAt must not be null");
        }

        this.lastSeenAt = seenAt;
        this.status = DataSourceStatus.ONLINE;
    }

    public void markOffline() {
        this.status = DataSourceStatus.OFFLINE;
    }

    public void markWarning() {
        this.status = DataSourceStatus.WARNING;
    }

    public void setLastSeenAt(Instant seenAt) {
        if (seenAt == null) {
            throw new IllegalArgumentException("seenAt must not be null");
        }
        this.lastSeenAt = seenAt;
    }

    public boolean hasConnectionDetails() {
        return host != null
                && !host.isBlank()
                && port != null
                && port > 0;
    }

    public void setSourceKey(String sourceKey) {
        if (sourceKey == null || sourceKey.isBlank()) {
            throw new IllegalArgumentException("sourceKey must not be null or blank");
        }
        if (this.sourceKey != null && !this.sourceKey.isBlank()) {
            throw new SourceKeySetException("Source key cannot be changed.");
        }
        this.sourceKey = sourceKey;
    }

    public boolean isOnline() {
        return DataSourceStatus.ONLINE.equals(this.status);
    }

    public boolean isOffline() {
        return DataSourceStatus.OFFLINE.equals(this.status);
    }
}