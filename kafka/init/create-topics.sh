#!/usr/bin/env bash
set -euo pipefail
BOOTSTRAP_SERVER="kafka:29092"
TOPICS=(pdf json canal vector)
for TOPIC in "${TOPICS[@]}"; do
  /opt/kafka/bin/kafka-topics.sh     --bootstrap-server "${BOOTSTRAP_SERVER}"     --create     --if-not-exists     --topic "${TOPIC}"     --partitions 1     --replication-factor 1
 done
/opt/kafka/bin/kafka-topics.sh --bootstrap-server "${BOOTSTRAP_SERVER}" --list
