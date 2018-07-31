#!/bin/sh
SERVICE_NAME=FileUploadService
PATH_TO_JAR="${PWD}/file-upload-client.jar"
case $1 in
    install)
        echo "installing $SERVICE_NAME ..."
        sudo cp ./signavio-file-upload-client.sh /etc/init.d/$SERVICE_NAME
        sudo chmod +x /etc/init.d/$SERVICE_NAME
        sudo chmod +x ./file-upload-client.jar
        sudo ln -s /etc/init.d/$SERVICE_NAME /etc/rc0.d/K60$SERVICE_NAME
        sudo ln -s /etc/init.d/$SERVICE_NAME /etc/rc3.d/S60$SERVICE_NAME
        sudo ln -s /etc/init.d/$SERVICE_NAME /etc/rc6.d/K60$SERVICE_NAME
        sudo touch /etc/opt/signavio-file-upload-jar-location.txt
        sudo chmod +x /etc/opt/signavio-file-upload-jar-location.txt
        sudo echo "$PATH_TO_JAR" > "/etc/opt/signavio-file-upload-jar-location.txt"
        sudo service $SERVICE_NAME start
    ;;
    remove)
        sudo /etc/init.d/$SERVICE_NAME stop
        sudo rm -f /etc/opt/signavio-file-upload-jar-location.txt
        sudo rm -f /etc/init.d/$SERVICE_NAME
        sudo rm -f /etc/rc0.d/K60$SERVICE_NAME
        sudo rm -f /etc/rc3.d/S60$SERVICE_NAME
        sudo rm -f /etc/rc6.d/K60$SERVICE_NAME
        echo "$SERVICE_NAME has been removed"
    ;;
    *)
        echo "Usage: file-upload-client.sh install/remove"
    ;;
esac
