#!/bin/bash

# turn on bash's job control
set -m

# Application
java -Duser.timezone=GMT \
  -jar /opt/flo-test-user/flo-test-user.jar \
  --spring.config.location=file:/etc/flo-test-user/application.yaml
