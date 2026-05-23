ARG ES_VERSION=8.19.0
FROM docker.elastic.co/elasticsearch/elasticsearch:${ES_VERSION}

USER root
COPY elasticsearch-analysis-ik-8.19.0.zip /tmp/elasticsearch-analysis-ik-8.19.0.zip
RUN /usr/share/elasticsearch/bin/elasticsearch-plugin install --batch file:///tmp/elasticsearch-analysis-ik-8.19.0.zip \
    && rm -f /tmp/elasticsearch-analysis-ik-8.19.0.zip
COPY --chown=elasticsearch:root analysis-ik /usr/share/elasticsearch/config/analysis-ik
RUN chmod -R 755 /usr/share/elasticsearch/config/analysis-ik
USER elasticsearch
