@echo off

rem for /f "delims=" %%i in ('findstr /b version pom.xml') do (set V_NUM=%%i)
rem set V_NUM=%V_NUM:~18,-10%
set V_NUM=

IF NOT "%V_NUM%"=="" GOTO start

IF "%~1"=="" GOTO stop

set V_NUM=%1


:start
echo VERSION_NUMBER = %V_NUM%

rem From project source root

rem Build api jar
call mvn clean package -DskipTests

rem Deploy dependences in docker-compose
call docker-compose -f docker-compose-dev.yml up -d --build

GOTO end

:stop
echo please provide import version!
echo ex.: deploy.bat 0.0.1-SNAPSHOT

:end