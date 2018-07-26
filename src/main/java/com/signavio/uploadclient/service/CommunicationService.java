package com.signavio.uploadclient.service;

import java.io.File;
import java.io.FileNotFoundException;

import com.google.inject.ImplementedBy;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;

@ImplementedBy(CommunicationServiceImpl.class)
public interface CommunicationService {
	
	HttpResponse<JsonNode> executeUploadRequest(File file) throws UnirestException, FileNotFoundException;
}
