@echo off
cd /d "%~dp0"
if exist classes rmdir /s /q classes
mkdir classes 2>nul

dir /s /b src\*.java > sources.txt
javac -cp "src;." -d classes @sources.txt 2> compile_error.txt

if %errorlevel% neq 0 (
    type compile_error.txt
    pause
    exit /b 1
)

del sources.txt compile_error.txt 2>nul
java -cp "classes;." Main
pause