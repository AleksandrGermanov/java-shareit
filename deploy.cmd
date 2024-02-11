chcp 65001
pushd %~dp0
call mvn install
call docker compose up -d
pause