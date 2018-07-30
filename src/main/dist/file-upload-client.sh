#!/bin/sh
SERVICE_NAME=FileUploadService
PATH_TO_JAR = "${PWD}/file-upload-client.jar"
case $1 in
    install)
        echo "installing $SERVICE_NAME ..."
        sudo cp ./signavio-file-upload-client.sh /etc/init.d/signavio-file-upload-client.sh
        sudo chmod +x /etc/init.d/signavio-file-upload-client.sh
        sudo ln -s /etc/init.d/signavio-file-upload-client.sh /etc/rc0.d/K60signavio-file-upload-client
        sudo ln -s /etc/init.d/signavio-file-upload-client.sh /etc/rc3.d/S60signavio-file-upload-client
        sudo ln -s /etc/init.d/signavio-file-upload-client.sh /etc/rc6.d/K60signavio-file-upload-client
        sudo $PATH_TO_JAR > /etc/opt/signavio-file-upload-jar-location.txt
        sudo /etc/init.d/signavio-file-upload-client.sh start
        echo "$SERVICE_NAME started"
    ;;
    remove)
        sudo /etc/init.d/signavio-file-upload-client.sh stop
        sudo rm -f /etc/opt/signavio-file-upload-jar-location.txt
        sudo rm -f /etc/init.d/signavio-file-upload-client.sh
        sudo rm -f /etc/rc0.d/K60signavio-file-upload-client
        sudo rm -f /etc/rc3.d/S60signavio-file-upload-client
        sudo rm -f /etc/rc6.d/K60signavio-file-upload-client
        echo "$SERVICE_NAME has been removed"
    ;;
    *)
        echo "Usage: file-upload-client.sh install/remove"
    ;;
esac
