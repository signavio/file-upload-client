package com.signavio.uploadclient;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.signavio.uploadclient.guice.AppModule;
import com.signavio.uploadclient.service.FileScanService;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class App {
	
	private static final Logger log = getLogger(App.class);
	
	
	public static void main(String[] args) {
		
		log.info("Signavio File Upload Service started...");
		
		Injector injector = Guice.createInjector(new AppModule());
		FileScanService fileScanService = injector.getInstance(FileScanService.class);
		
		fileScanService.initialScan();
		log.info("watching directory ");
		while (true) {
			fileScanService.watch();
		}
		
	}
}
