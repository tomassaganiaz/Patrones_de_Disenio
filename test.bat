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
echo.
echo === Para JUnit (requiere Maven) ===
where mvn >nul 2>nul
if %errorlevel% equ 0 (
  mvn test
) else (
  echo Maven no encontrado - tests JUnit omitidos (TestRunner ya paso^)
)
