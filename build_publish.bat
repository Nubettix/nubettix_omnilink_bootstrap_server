@echo off
setlocal

set "env=dev"

:loop
if "%~1"=="" goto :endloop
set "key=%~1"
set "val=%~2"
if "%key%"=="--env" (
    set "env=%val%"
)
shift
shift
goto :loop

:endloop

echo Publishing to environment: %env%

if "%env%"=="prd" (
    echo Executing PRD commands
    .\gradlew buildDependents && .\build_publish_prd.bat
) else if "%env%"=="dev" (
    echo Executing DEV commands
    .\gradlew buildDependents && .\build_publish_dev.bat
) else (
    echo Unknown environment: %env%
)

endlocal