@echo off
setlocal enabledelayedexpansion
if exist out rmdir /s /q out
mkdir out
echo Compilando MAIN (MVC + correcciones + futuras)...
set FILES=
for /R src\main\java %%f in (*.java) do set FILES=!FILES! "%%f"
javac -d out -encoding UTF-8 -sourcepath "src\main\java" !FILES!
if %errorlevel% neq 0 (
  echo ERROR compilacion MAIN
  exit /b 1
)
echo MAIN OK.

echo Compilando TESTS sin dependencias...
javac -d out -encoding UTF-8 -cp out -sourcepath "src\test\java" "src\test\java\com\tienda\TestRunner.java"
if %errorlevel% neq 0 (
  echo ERROR compilacion TestRunner
  exit /b 1
)
echo TESTS OK -> out\
echo Para JUnit: mvn test  (requiere Maven)
