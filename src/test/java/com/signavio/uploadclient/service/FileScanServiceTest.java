package com.signavio.uploadclient.service;

import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Provider;

import com.google.inject.testing.fieldbinder.Bind;
import com.signavio.uploadclient.FileUploadConfiguration;
import com.signavio.uploadclient.junit5.TestUnitExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.slf4j.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;

@ExtendWith(TestUnitExtension.class)
class FileScanServiceTest {
	
	private static final Logger log = getLogger(FileScanServiceTest.class);
	private static final String TESTFOLDER = "./testfolder";
	
	private static File folder = new File(TESTFOLDER);
	
	@Mock
	@Bind
	private static FileUploadConfiguration configuration;
	
	@Mock
	@Bind
	private FileUploadService fileUploadService;
	
	@Inject
	private Provider<FileScanService> fileScanServiceProvider;
	
	private FileScanService fileScanService;
	
	
	@BeforeEach
	void setup() {
		folder = new File(TESTFOLDER);
		if (folder.mkdir()) {
			log.info(TESTFOLDER + " created");
		}
		when(configuration.getFolder()).thenReturn(TESTFOLDER);
		when(configuration.getPattern()).thenReturn("*.test");
		
		fileScanService = fileScanServiceProvider.get();
	}
	
	
	@AfterEach
	void cleanup() {
		if (folder.exists()) {
			
			for (File file : folder.listFiles()) {
				if (file.delete()) {
					log.info(file.getAbsolutePath() + " deleted");
				}
			}
			if (folder.delete()) {
				log.info(TESTFOLDER + " deleted");
			} else {
				log.info(TESTFOLDER + " could not be deleted!");
			}
		}
	}
	
	
	@Test
	void testInitialScanWithEmptyFolder() {
		
		// Arrange
		// Act
		fileScanService.initialScan();
		
		// Assert
		verify(fileUploadService, never()).upload(any());
		
	}
	
	
	@Test
	void testInitialScanWithFilesToUpload() throws IOException {
		
		// Arrange
		new File(TESTFOLDER, "file_1.test").createNewFile();
		new File(TESTFOLDER, "file_1.test.uploaded").createNewFile();
		new File(TESTFOLDER, "something.else").createNewFile();
		new File(TESTFOLDER, "file_2.test").createNewFile();
		new File(TESTFOLDER, "file_2.test.failed").createNewFile();
		File file3 = new File(TESTFOLDER, "file_3.test");
		file3.createNewFile();
		File file4 = new File(TESTFOLDER, "file_4.test");
		file4.createNewFile();
		
		// Act
		fileScanService.initialScan();
		
		// Assert
		verify(fileUploadService, times(1)).upload(file3);
		verify(fileUploadService, times(1)).upload(file4);
		verifyNoMoreInteractions(fileUploadService);
		
	}
	
	@Test
	void testWatching() throws IOException, InterruptedException {
		
		// Arrange
		File file1 = new File(TESTFOLDER, "file_1.test");
		file1.createNewFile();
		Thread.sleep(1000);
		
		// Act
		fileScanService.watch();
		
		// Assert
		verify(fileUploadService, times(1)).upload(file1);
		verifyNoMoreInteractions(fileUploadService);
	}
	
}
