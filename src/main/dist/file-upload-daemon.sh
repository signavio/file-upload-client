#!/bin/sh
# for SuSe:
### BEGIN INIT INFO
# Provides: FileUploadService
# Required-Start: $local_fs $network
# Required-Stop:
# Default-Start: 3 5 6
# Default-Stop: 0 1 2 6
# Description: Start the FileUploadService
# Short-Description: make FileUploadService
### END INIT INFO

SERVICE_NAME=FileUploadService
PATH_TO_JAR=$(cat /etc/opt/signavio-file-upload-jar-location.txt)
JAR_NAME=file-upload-client.jar
PID_PATH_NAME=./FileUploadService-pid
case "$1" in
    start)
        if [ ! -f $PID_PATH_NAME ]; then
            cd $PATH_TO_JAR
            echo "Starting $SERVICE_NAME ..."
            nohup java -jar $PATH_TO_JAR$JAR_NAME >> logs/FileUploadService.out 2>&1 &
            echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is already running ..."
        fi
    ;;
    stop)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stopping ... @ $PID"
            kill $PID;
            echo "$SERVICE_NAME stopped ..."
            rm $PID_PATH_NAME
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
    restart)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stopping ...";
            kill $PID;
            echo "$SERVICE_NAME stopped ...";
            rm $PID_PATH_NAME
            cd $PATH_TO_JAR
            echo "$SERVICE_NAME starting ..."
            nohup java -jar $PATH_TO_JAR$JAR_NAME >> FileUploadService.out 2>&1 &
            echo $! > $PID_PATH_NAME
            echo "$SERVICE_NAME started ..."
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
    status)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stopping ... @ $PID"
            kill $PID;
            echo "$SERVICE_NAME stopped ..."
            rm $PID_PATH_NAME
        else
            echo "$SERVICE_NAME is not running ..."
        fi
    ;;
esac