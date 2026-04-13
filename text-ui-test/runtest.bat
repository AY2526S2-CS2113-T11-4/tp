@echo off
setlocal enableextensions
pushd %~dp0

cd ..
call gradlew clean shadowJar

cd text-ui-test

REM Clean up data from previous test runs
if exist "data" rmdir /s /q data

REM Find the jar file in the build directory
for /f "tokens=*" %%a in ('dir /b ..\build\libs\*.jar') do (
    set jarloc=%%a
)

REM Run the program from within the text-ui-test directory
java -jar ..\build\libs\%jarloc% < input.txt > ACTUAL.TXT

REM Compare the output
FC ACTUAL.TXT EXPECTED.TXT >NUL && ECHO Test passed! || Echo Test failed!
