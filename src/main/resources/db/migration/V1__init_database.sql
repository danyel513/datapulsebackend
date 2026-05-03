CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
       first_name VARCHAR(100) NOT NULL,
       last_name VARCHAR(100) NOT NULL,
       email VARCHAR(255) NOT NULL UNIQUE,
       password_hash VARCHAR(255) NOT NULL,
       enabled BOOLEAN NOT NULL DEFAULT TRUE,

       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
       updated_at TIMESTAMPTZ,
       created_by VARCHAR(100),
       updated_by VARCHAR(100)
);

CREATE TABLE data_sources (
          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
          user_id UUID NOT NULL,
          source_key VARCHAR(100) NOT NULL UNIQUE,
          name VARCHAR(150) NOT NULL,
          type VARCHAR(100) NOT NULL,
          description VARCHAR(500),
          host VARCHAR(255),
          port INTEGER,
          protocol VARCHAR(50),
          status VARCHAR(50) NOT NULL,
          last_seen_at TIMESTAMPTZ,

          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
          updated_at TIMESTAMPTZ,
          created_by VARCHAR(100),
          updated_by VARCHAR(100),

          CONSTRAINT fk_data_sources_user
              FOREIGN KEY (user_id) REFERENCES users(id)
    );

CREATE TABLE metric_definitions (
            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
            metric_key VARCHAR(100) NOT NULL UNIQUE,
            display_name VARCHAR(150) NOT NULL,
            unit VARCHAR(50) NOT NULL,
            data_type VARCHAR(50) NOT NULL,
            min_valid_value DOUBLE PRECISION,
            max_valid_value DOUBLE PRECISION,
            enabled BOOLEAN NOT NULL DEFAULT TRUE,

            created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
            updated_at TIMESTAMPTZ,
            created_by VARCHAR(100),
            updated_by VARCHAR(100)
);

CREATE TABLE measurement_aggregates (
        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        data_source_id UUID NOT NULL,
        metric_definition_id UUID NOT NULL,
        aggregation_level VARCHAR(50) NOT NULL,
        window_start TIMESTAMPTZ NOT NULL,
        window_end TIMESTAMPTZ NOT NULL,

        sample_count INTEGER NOT NULL,
        valid_sample_count INTEGER NOT NULL,
        invalid_sample_count INTEGER NOT NULL,

        min_value DOUBLE PRECISION NOT NULL,
        max_value DOUBLE PRECISION NOT NULL,
        avg_value DOUBLE PRECISION NOT NULL,
        last_value DOUBLE PRECISION,

        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
        updated_at TIMESTAMPTZ,
        created_by VARCHAR(100),
        updated_by VARCHAR(100),

        CONSTRAINT fk_measurement_aggregates_data_source
            FOREIGN KEY (data_source_id) REFERENCES data_sources(id),

        CONSTRAINT fk_measurement_aggregates_metric_definition
            FOREIGN KEY (metric_definition_id) REFERENCES metric_definitions(id),

        CONSTRAINT chk_measurement_aggregates_counts
            CHECK (
                sample_count > 0
                    AND valid_sample_count >= 0
                    AND invalid_sample_count >= 0
                    AND valid_sample_count + invalid_sample_count = sample_count
                ),

        CONSTRAINT chk_measurement_aggregates_window
            CHECK (window_end > window_start),

        CONSTRAINT uq_measurement_aggregate_window
            UNIQUE (data_source_id, metric_definition_id, aggregation_level, window_start, window_end)
);

CREATE INDEX idx_data_sources_user_id
ON data_sources(user_id);

CREATE INDEX idx_measurement_aggregates_source_metric_window
ON measurement_aggregates(data_source_id, metric_definition_id, window_start DESC);

CREATE INDEX idx_measurement_aggregates_level_window
ON measurement_aggregates(aggregation_level, window_start DESC);
