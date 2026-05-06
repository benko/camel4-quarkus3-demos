podman run -d --name otelcol --rm -p 4317:4317 -p 4318:4318 --network otel -v ./otelcol.yaml:/etc/otelcol/config.yaml registry.redhat.io/rhosdt/opentelemetry-collector-rhel9:rhosdt-3.9.2
