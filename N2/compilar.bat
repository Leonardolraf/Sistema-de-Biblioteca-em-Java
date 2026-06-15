@echo off
REM ===========================================================================
REM Compila o Sistema de Biblioteca (N2) com o driver sqlite-jdbc no classpath.
REM Os .class sao gerados na pasta "out".
REM ===========================================================================
chcp 65001 >nul
setlocal

set JAR=lib\sqlite-jdbc-3.53.2.0.jar

echo Compilando...
javac -encoding UTF-8 -cp "%JAR%" -d out ^
    src\biblioteca\model\*.java ^
    src\biblioteca\database\*.java ^
    src\biblioteca\factory\*.java ^
    src\biblioteca\repository\*.java ^
    src\biblioteca\service\*.java ^
    src\biblioteca\ui\*.java ^
    src\biblioteca\main\*.java

if %errorlevel% neq 0 (
    echo.
    echo Falha na compilacao.
    exit /b %errorlevel%
)

echo Compilacao concluida com sucesso. Classes em .\out
endlocal
