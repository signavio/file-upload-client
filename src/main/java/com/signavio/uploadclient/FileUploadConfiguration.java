package com.signavio.uploadclient;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class FileUploadConfiguration {
	
	private String folder = "./";
	private String pattern = "*.xes.gz";
	private String endpoint;
	private String token;
	
	
	public static FileUploadConfiguration fromProperties(Properties props) throws FileNotFoundException {
		FileUploadConfiguration config = new FileUploadConfiguration();
		
		String folder = props.getProperty("folder", "./");
		config.setFolder(folder);
		
		String pattern = props.getProperty("pattern", "*.xes.gz");
		config.setPattern(pattern);
		
		String endpoint = props.getProperty("endpoint");
		if (StringUtils.isEmpty(endpoint)) {
			throw new IllegalArgumentException("parameter 'endpoint' is missing or empty!");
		}
		config.setEndpoint(endpoint);
		
		String token = props.getProperty("token");
		if (StringUtils.isEmpty(token)) {
			throw new IllegalArgumentException("parameter 'token' is missing or empty!");
		}
		config.setToken(token);
		
		return config;
	}
	
	
	public String getFolder() {
		return folder;
	}
	
	
	private void setFolder(String folder) throws FileNotFoundException {
		
		System.out.println(folder);
		File dir = new File(folder);
		if (!dir.exists()) {
			throw new FileNotFoundException();
		}
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException(folder + " is not a directory");
		}
		this.folder = folder;
	}
	
	
	public String getPattern() {
		return pattern;
	}
	
	
	private void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	
	public String getEndpoint() {
		return endpoint;
	}
	
	
	private void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	
	public String getToken() {
		return token;
	}
	
	
	private void setToken(String token) {
		this.token = token;
	}
	
	
}
