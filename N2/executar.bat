@echo off
REM ===========================================================================
REM Executa o Sistema de Biblioteca (N2).
REM O classpath inclui as classes compiladas (out) e o driver sqlite-jdbc.
REM A flag --enable-native-access silencia o aviso do Java sobre acesso nativo
REM usado pelo driver SQLite (inofensivo).
REM ===========================================================================
chcp 65001 >nul
setlocal

set JAR=lib\sqlite-jdbc-3.53.2.0.jar

java --enable-native-access=ALL-UNNAMED -cp "out;%JAR%" biblioteca.main.Main

endlocal
