@echo off

rem Build api jar
call mvn clean package -DskipTests

rem Deploy dependences in docker-compose
call docker-compose -f docker-compose-dev.yml up -d --build
