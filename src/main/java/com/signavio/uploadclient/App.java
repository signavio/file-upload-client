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
		
		if ("start".equals(args[0])) {
			start(args);
		} else if ("stop".equals(args[0])) {
			stop(args);
		}
		
	}
	
	
	public static void start(String[] args) {
		
		log.info("Signavio File Upload Service started...");
		
		Injector injector = Guice.createInjector(new AppModule());
		FileScanService fileScanService = injector.getInstance(FileScanService.class);
		
		fileScanService.initialScan();
		fileScanService.watch();
	}
	
	
	public static void stop(String[] args) {
		log.info("Service is being shut down...");
		System.exit(0);
	}
}
