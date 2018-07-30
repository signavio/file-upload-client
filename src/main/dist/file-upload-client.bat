@echo off
SETLOCAL ENABLEEXTENSIONS

set SERVICE_NAME=FileUploadService
set PR_DISPLAYNAME=Signavio File Upload Service
set PR_DESCRIPTION=This services watches a given folder and uploads any new files that match a given pattern to a specific endpoint.
set PR_INSTALL=%~dp0%prunsrv.exe

REM Service log configuration
set PR_LOGPREFIX=%SERVICE_NAME%
set PR_LOGPATH=%~dp0%logs
set PR_STDOUTPUT=%~dp0%logs\stdout.txt
set PR_STDERROR=%~dp0%logs\stderr.txt
set PR_LOGLEVEL=INFO

REM Path to java installation
set PR_JVM=%JAVA_HOME%\bin\server\jvm.dll
set PR_CLASSPATH=file-upload-client.jar

REM Startup configuration
set PR_STARTUP=auto
set PR_STARTMODE=jvm
set PR_STARTCLASS=com.signavio.uploadclient.App
set PR_STARTMETHOD=main



if [%1]==[] goto displayUsage
if %1 == install goto doInstall
if %1 == remove goto doRemove

:displayUsage
echo.
ECHO Usage: file-upload-client.bat install/remove
GOTO end


:doInstall
REM Install service
prunsrv.exe //IS//%SERVICE_NAME%
ECHO %PR_DISPLAYNAME% has been installed

REM Start service
prunsrv.exe //ES//%SERVICE_NAME%
ECHO %PR_DISPLAYNAME% has been started
GOTO end

:doRemove
REM Remove service
prunsrv.exe //DS//%SERVICE_NAME%
ECHO %PR_DISPLAYNAME% has been removed
GOTO end

:end