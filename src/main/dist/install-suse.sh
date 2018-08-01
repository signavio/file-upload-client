#!/bin/sh
SERVICE_NAME=FileUploadService
PATH_TO_JAR="${PWD}/"
case $1 in
    install)
        echo "installing $SERVICE_NAME ..."
        sudo cp ./file-upload-daemon.sh /etc/init.d/$SERVICE_NAME
        sudo chmod +x /etc/init.d/$SERVICE_NAME
        sudo chmod +x ./file-upload-client.jar
        sudo mkdir -p logs
        cd /etc/init.d
        sudo insserv $SERVICE_NAME
        sudo touch /etc/opt/signavio-file-upload-jar-location.txt
        sudo chmod +x /etc/opt/signavio-file-upload-jar-location.txt
        sudo echo "$PATH_TO_JAR" > "/etc/opt/signavio-file-upload-jar-location.txt"
        sudo ./$SERVICE_NAME start
    ;;
    remove)
        cd /etc/init.d
        sudo insserv -r $SERVICE_NAME stop
        sudo rm -f /etc/opt/signavio-file-upload-jar-location.txt
        sudo rm -f /etc/init.d/$SERVICE_NAME
        echo "$SERVICE_NAME has been removed"
    ;;
    *)
        echo "Usage: file-upload-client.sh install/remove"
    ;;
esac
