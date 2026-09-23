@echo off
call compile.bat
if %errorlevel% neq 0 exit /b 1
echo.
echo === TESTS sin Main (sin dependencias) ===
java -cp out com.tienda.TestRunner
if %errorlevel% neq 0 (
  echo TESTS FAIL
  exit /b 1
)