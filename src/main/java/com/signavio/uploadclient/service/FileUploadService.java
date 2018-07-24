package com.signavio.uploadclient.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.signavio.uploadclient.Arguments;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpHeaders;

class FileUploadService {
	
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileUploadService.class);
	
	private final String token;
	private final String endpoint;
	private final Map<Integer, Pair<Long, String>> retryStratgies = Map.of(
			1, Pair.of(10_000L, "10 seconds"),
			2, Pair.of(18_000_000L, "5 minutes"),
			3, Pair.of(108_000_000L, "30 minutes"));
	
	
	FileUploadService(Arguments arguments) {
		this.endpoint = arguments.getEndpoint();
		this.token = arguments.getToken();
		
	}
	
	
	void upload(File file) {
		UploadResult result = doUpload(file);
		
		if (result.equals(UploadResult.RETRY)) {
			int count = 1;
			
			while (result.equals(UploadResult.RETRY) && count <= 3) {
				result = retryUpload(file, count);
				count++;
			}
		}
		
		String confirmFileName = "";
		try {
			confirmFileName = result.equals(UploadResult.SUCCESS)
					? file.getAbsolutePath() + ".uploaded"
					: file.getAbsolutePath() + ".failed";
			
			boolean created = new File(confirmFileName).createNewFile();
			
			if (!created) {
				log.info("creation of upload confirmation file " + confirmFileName
						+ " failed. The file already exists.");
			}
		} catch (IOException e) {
			log.info("creation of upload confirmation file " + confirmFileName
					+ " failed with an exception: ", e);
		}
	}
	
	
	private UploadResult doUpload(File file) {
		
		try {
			log.info("start uploading file " + file.getAbsolutePath());
			HttpResponse<JsonNode> response = executeUploadRequest(file);
			
			if (response.getStatus() == 204) {
				return UploadResult.SUCCESS;
			} else if (response.getStatus() / 100 == 5) {
				logRequestFailure(file, response);
				return UploadResult.RETRY;
			} else {
				logRequestFailure(file, response);
				return UploadResult.FAILED;
			}
			
		} catch (UnirestException e) {
			log.info("upload of file " + file.getAbsolutePath() + " failed with an exception: ", e);
			return UploadResult.RETRY;
		}
		
	}
	
	
	private void logRequestFailure(File file, HttpResponse<JsonNode> response) {
		log.info("upload of " + file.getAbsolutePath() + " failed! Got response " + response.getStatus()
				+ " with message '" + response.getBody().toString() + "'");
	}
	
	
	private UploadResult retryUpload(File file, int count) {
		try {
			log.info("scheduling retry in " + retryStratgies.get(count).getRight() + " for file " + file
					.getAbsolutePath());
			Thread.sleep(retryStratgies.get(count).getLeft());
			log.info("retry no. " + count + " for file " + file.getAbsolutePath());
			return doUpload(file);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	
	private HttpResponse<JsonNode> executeUploadRequest(File file) throws UnirestException {
		try {
			return Unirest.post(endpoint)
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.field("file", new FileInputStream(file), file.getAbsolutePath())
					.asJson();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}
