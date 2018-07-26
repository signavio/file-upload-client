package com.signavio.uploadclient.service;

import com.google.inject.ImplementedBy;

@ImplementedBy(FileScanServiceImpl.class)
public interface FileScanService {
	
	void initialScan();
	
	void watch();
}
