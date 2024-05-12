FROM prom/prometheus:latest

COPY --chmod=755 ./prometheus/entrypoint.sh /entrypoint.sh
COPY ./prometheus/prometheus.yml /prometheus/prometheus.yml
ENTRYPOINT ["/entrypoint.sh"]