package com.signavio.uploadclient.service;

import java.io.File;
import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.testing.fieldbinder.Bind;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.signavio.uploadclient.junit5.TestUnitExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;

@ExtendWith(TestUnitExtension.class)
class FileUploadServiceTest {
	
	private static final Logger log = getLogger(FileUploadServiceTest.class);
	
	@Mock
	@Bind
	private CommunicationService communicationService;
	
	@Mock
	@Bind
	private RetryStrategy retryStrategy;
	
	@Inject
	private FileUploadService fileUploadService;
	private File file = new File("abc.test");
	
	
	@AfterEach
	void cleanup() {
		
		delete(".uploaded");
		delete(".failed");
		
	}
	
	
	private void delete(String extension) {
		File uploadConfirmation = getConfirmationFile(extension);
		if (uploadConfirmation.exists()) {
			if (uploadConfirmation.delete()) {
				log.info(uploadConfirmation.getAbsolutePath() + " deleted");
			}
		}
	}
	
	
	private File getConfirmationFile(String extension) {
		return new File(this.file.getAbsolutePath() + extension);
	}
	
	
	@Test
	void testSuccess() throws IOException, UnirestException {
		
		// Arrange
		HttpResponse<String> httpResponse = mock(HttpResponse.class);
		when(httpResponse.getStatus()).thenReturn(204);
		
		when(communicationService.executeUploadRequest(any())).thenReturn(httpResponse);
		
		when(retryStrategy.retry(any(), any(), any())).thenReturn(UploadResult.SUCCESS);
		
		// Act
		fileUploadService.upload(file);
		
		// Assert
		assertThat(getConfirmationFile(".uploaded").exists()).isTrue();
		assertThat(getConfirmationFile(".failed").exists()).isFalse();
	}
	
	
	@Test
	void testFailed() throws IOException, UnirestException {
		
		// Arrange
		HttpResponse<String> httpResponse = mock(HttpResponse.class);
		when(httpResponse.getStatus()).thenReturn(401);
		when(httpResponse.getBody()).thenReturn("{}");
		
		when(communicationService.executeUploadRequest(any())).thenReturn(httpResponse);
		
		when(retryStrategy.retry(any(), any(), any())).thenReturn(UploadResult.FAILED);
		
		// Act
		fileUploadService.upload(file);
		
		// Assert
		assertThat(getConfirmationFile(".uploaded").exists()).isFalse();
		assertThat(getConfirmationFile(".failed").exists()).isTrue();
	}
	
	
	@Test
	void testRetry() throws IOException, UnirestException {
		
		// Arrange
		HttpResponse<String> httpResponse = mock(HttpResponse.class);
		when(httpResponse.getStatus()).thenReturn(500, 500, 204);
		when(httpResponse.getBody()).thenReturn("<>");
		
		when(communicationService.executeUploadRequest(any())).thenReturn(httpResponse);
		
		when(retryStrategy.retry(any(), any(), any())).thenReturn(
				UploadResult.RETRY,
				UploadResult.RETRY,
				UploadResult.SUCCESS);
		
		// Act
		fileUploadService.upload(file);
		
		// Assert
		assertThat(getConfirmationFile(".uploaded").exists()).isTrue();
		assertThat(getConfirmationFile(".failed").exists()).isFalse();
	}
	
}
