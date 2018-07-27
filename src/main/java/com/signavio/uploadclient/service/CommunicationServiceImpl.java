package com.signavio.uploadclient.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.google.inject.Inject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.signavio.uploadclient.FileUploadConfiguration;
import org.apache.http.HttpHeaders;

public class CommunicationServiceImpl implements CommunicationService {
	
	
	private final String endpoint;
	private final String token;
	
	
	@Inject
	private CommunicationServiceImpl(FileUploadConfiguration config) {
		this.endpoint = config.getEndpoint();
		this.token = config.getToken();
		
	}
	
	
	@Override
	public HttpResponse<String> executeUploadRequest(File file) throws UnirestException, FileNotFoundException {
		return Unirest.post(endpoint)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.field("file", new FileInputStream(file), file.getAbsolutePath())
				.asString();
	}
	
}
