FROM prom/prometheus:latest

COPY --chmod=755 ./prometheus/entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]