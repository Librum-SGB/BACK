@echo off
setlocal

set "COMPOSE_DIR=%~dp0mylibrum"
if not exist "%COMPOSE_DIR%\docker-compose.yml" (
    echo.
    echo Arquivo docker-compose.yml nao encontrado em:
    echo %COMPOSE_DIR%
    exit /b 1
)

cd /d "%COMPOSE_DIR%"

echo Iniciando PostgreSQL, backend e frontend do MyLibrum...
docker compose up --build -d --remove-orphans
if errorlevel 1 (
    echo.
    echo Nao foi possivel iniciar os containers. Verifique se o Docker Desktop esta em execucao.
    exit /b 1
)

echo.
echo MyLibrum iniciado com sucesso.
echo Frontend: http://localhost:4200
echo Backend:  http://localhost:8080
endlocal
