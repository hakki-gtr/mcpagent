# Product image built on the base image
ARG BASE_IMAGE=admingentoro/base:latest
FROM ${BASE_IMAGE}

ARG APP_JAR
ENV APP_JAR_PATH=/opt/app/mcpagent.jar
RUN mkdir -p /opt/app /var/foundation

# Copy app jar
COPY ${APP_JAR} ${APP_JAR_PATH}

# Copy startup scripts and default otel config
COPY scripts/docker/otel-collector-config.yaml /etc/otel-collector-config.yaml
COPY scripts/docker/supervisord.conf /etc/supervisor/conf.d/supervisord.conf
COPY scripts/docker/run-app.sh /opt/bin/run-app.sh
COPY scripts/docker/run-otel.sh /opt/bin/run-otel.sh
COPY scripts/docker/run-ts.sh /opt/bin/run-ts.sh
COPY scripts/docker/run-mock.sh /opt/bin/run-mock.sh
RUN chmod +x /opt/bin/*.sh

EXPOSE 8080
ENTRYPOINT ["/opt/bin/entrypoint.sh"]
