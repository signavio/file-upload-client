package com.signavio.uploadclient.guice;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.signavio.uploadclient.FileUploadConfiguration;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class AppModule extends AbstractModule {
	
	private static final Logger log = getLogger(AppModule.class);
	
	
	@Override
	protected void configure() {
		super.configure();
	}
	
	
	@Provides
	@Singleton
	public FileUploadConfiguration provideConfig() throws FileNotFoundException {
		
		Properties props = new Properties();
		try {
			props.load(new FileInputStream("config.properties"));
		} catch (IOException e) {
			log.error("unable to load config.properties file.", e);
			System.exit(1);
		}
		return FileUploadConfiguration.fromProperties(props);
	}
}
