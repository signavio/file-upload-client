package com.signavio.uploadclient.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.google.inject.Inject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

class FileUploadServiceImpl implements FileUploadService {
	
	private static final Logger log = getLogger(FileUploadServiceImpl.class);
	
	private final CommunicationService communicationService;
	private final List<Pair<Long, String>> retryStrategies = List.of(
			Pair.of(10_000L, "10 seconds"),
			Pair.of(18_000_000L, "5 minutes"),
			Pair.of(108_000_000L, "30 minutes"));
	
	
	@Inject
	private FileUploadServiceImpl(CommunicationService communicationService) {
		this.communicationService = communicationService;
	}
	
	
	@Override
	public void upload(File file) {
		
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
				log.warn("creation of upload confirmation file " + confirmFileName
						+ " failed. The file already exists.");
			}
		} catch (IOException e) {
			log.warn("creation of upload confirmation file " + confirmFileName
					+ " failed with an exception: ", e);
			//TODO possible cause of inconsistencies!
		}
	}
	
	
	private UploadResult doUpload(File file) {
		
		try {
			log.info("start uploading file " + file.getAbsolutePath());
			HttpResponse<JsonNode> response = communicationService.executeUploadRequest(file);
			
			if (response.getStatus() == 204) {
				log.info("file " + file.getAbsolutePath() + " uploaded successfully.");
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
		} catch (FileNotFoundException e) {
			log.error("upload of file " + file.getAbsolutePath() + " failed with an exception: ", e);
			return UploadResult.FAILED;
		}
		
	}
	
	
	private void logRequestFailure(File file, HttpResponse<JsonNode> response) {
		log.info("upload of " + file.getAbsolutePath() + " failed! Got response " + response.getStatus()
				+ " with message '" + response.getBody().toString() + "'");
	}
	
	
	private UploadResult retryUpload(File file, int count) {
		try {
			log.info("scheduling retry in " + retryStrategies.get(count - 1).getRight() + " for file " + file
					.getAbsolutePath());
			Thread.sleep(retryStrategies.get(count - 1).getLeft());
			log.info("retry no. " + count + " for file " + file.getAbsolutePath());
			return doUpload(file);
			
		} catch (InterruptedException e) {
			log.error("Retry upload of " + file.getAbsolutePath() + " failed with an exception: ", e);
			return UploadResult.FAILED;
		}
	}
	
	
}
