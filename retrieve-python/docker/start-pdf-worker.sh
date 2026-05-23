#!/usr/bin/env bash
set -euo pipefail
mkdir -p /app/retrieve-python/rs/save
python /app/retrieve-python/kafka/pdf2json.py
