FROM openjdk:11

RUN apt update \
 && apt install -yq --no-install-recommends jq \
 && rm -rf /var/lib/apt/lists/*

ARG JAR_FILE="target/flo-test-user*.jar"
COPY ${JAR_FILE} /opt/flo-test-user/flo-test-user.jar

ARG APP_CFG_OVERRIDE_FILE="config/application-prod.yaml"
COPY ${APP_CFG_OVERRIDE_FILE} "/etc/flo-test-user/application.yaml"

ARG APP_ENTRYPOINT_FILE="docker-entrypoint.sh"
COPY ${APP_ENTRYPOINT_FILE} "/docker-entrypoint.sh"

ENTRYPOINT ["/docker-entrypoint.sh"]

EXPOSE 4200