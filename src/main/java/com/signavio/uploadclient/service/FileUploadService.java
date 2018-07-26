package com.signavio.uploadclient.service;

import java.io.File;

import com.google.inject.ImplementedBy;

@ImplementedBy(FileUploadServiceImpl.class)
public interface FileUploadService {
	
	void upload(File file);
}
