@echo off
call compile.bat
if %errorlevel% neq 0 exit /b 1
echo.
echo === Ejecutando MAIN (MVC) ===
echo.
java -cp out com.tienda.Main
