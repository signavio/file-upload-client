# file-upload-client
The Signavio file upload client is a small Java application that runs in the SAP JVM. It takes as configuration parameters the access info to Signavio landscape and the directory where SNP stores the XES file. It checks for new XES files and takes care of the upload, as well as errors that might occur.

# Usage on Windows
* Download latest release from <https://github.com/signavio/file-upload-client/releases>
* extract the downloaded zip file
* navigate to the extracted folder, eg. `cd file-upload-client-1.0.0/`
* edit the file _config.properties_
    - set the folder to be watched as an absolute path or relative from the location of the _file-upload-client.jar_
    - if necessary set the pattern for file names that the upload client should upload (default is "*.xes.gz" as this is what the SNP BPE creates)
    - configure the endpoint and the API token for the uploads. 
    - save the file
* Open a Powershell console in Administrator mode
* execute `./file-upload-client.bat install` on your Powershell
* the service is then installed and started. It will also autostart on reboot
* inspect the log file _./logs/logFile.html_ that the service is running
* execute `./file-upload-client.bat install` to uninstall the service

# Usage on Linux
* Download latest release from <https://github.com/signavio/file-upload-client/releases>
* unzip downloaded file, e.g. `unzip file-upload-client-1.0.0.zip`
* navigate to folder, eg. `cd file-upload-client-1.0.0/`
* edit the file _config.properties_
    - set the folder to be watched as an absolute path or relative from the location of the _file-upload-client.jar_
    - if necessary set the pattern for file names that the upload client should upload (default is "*.xes.gz" as this is what the SNP BPE creates)
    - configure the endpoint and the API token for the uploads.
    - save the file
* grant permissions: `sudo chmod +x ./file-upload-client.sh`
* execute `sudo ./file-upload-client.sh install` on your terminal
* the service is then installed and started. It will also autostart on reboot
* inspect the log file _./FileUploadService.out_ that the service is running
* execute `sudo ./file-upload-client.sh remove` to uninstall the service