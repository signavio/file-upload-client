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
set PR_CLASSPATH=file-upload-client-1.0.jar

REM Startup configuration
set PR_STARTUP=auto
set PR_STARTMODE=jvm
set PR_STARTCLASS=com.signavio.uploadclient.App
set PR_STARTMETHOD=start

REM Shutdown configuration
set PR_STOPMODE=jvm
set PR_STOPCLASS=com.signavio.uploadclient.App
set PR_STOPMETHOD=stop

REM JVM configuration
REM set PR_JVMMS=256
REM set PR_JVMMX=1024
REM set PR_JVMSS=4000
REM set PR_JVMOPTIONS=-Duser.language=DE;-Duser.region=de

ECHO Install service
prunsrv.exe //IS//%SERVICE_NAME%

ECHO Start service
prunsrv.exe //ES//%SERVICE_NAME%