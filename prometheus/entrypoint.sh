#!/bin/sh
echo ${YC_API_KEY} > /tmp/token_file
/bin/prometheus --config.file=/prometheus/prometheus.yml "$@"