@echo off
setlocal
set "GRADLE_VERSION=9.3.1"
if "%GRADLE_USER_HOME%"=="" set "GRADLE_USER_HOME=%USERPROFILE%\.gradle"
set "DIST_DIR=%GRADLE_USER_HOME%\wrapper\dists\gradle-%GRADLE_VERSION%-bin"
set "INSTALL_DIR=%DIST_DIR%\gradle-%GRADLE_VERSION%"
set "GRADLE_BIN=%INSTALL_DIR%\bin\gradle.bat"

if exist "%GRADLE_BIN%" goto runGradle

if not exist "%DIST_DIR%" mkdir "%DIST_DIR%"
set "ZIP=%DIST_DIR%\gradle-%GRADLE_VERSION%-bin.zip"
if not exist "%ZIP%" (
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -UseBasicParsing -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%ZIP%'"
  if errorlevel 1 exit /b 1
)

powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Force '%ZIP%' '%DIST_DIR%'"
if errorlevel 1 exit /b 1

:runGradle
call "%GRADLE_BIN%" %*
exit /b %ERRORLEVEL%
