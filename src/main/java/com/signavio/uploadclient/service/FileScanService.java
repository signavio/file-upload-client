package com.signavio.uploadclient.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.signavio.uploadclient.Arguments;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public class FileScanService {
	
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileScanService.class);
	
	private final Arguments arguments;
	private final String pattern;
	private final FileUploadService fileUploadService;
	private final WatchKey watchKey;
	private final Path watchedDirectory;
	
	
	public FileScanService(Arguments arguments) {
		this.arguments = arguments;
		this.pattern = arguments.getPattern();
		this.fileUploadService = new FileUploadService(arguments);
		
		WatchService watcher;
		try {
			watcher = FileSystems.getDefault().newWatchService();
			watchedDirectory = FileSystems.getDefault().getPath(arguments.getFolder());
			this.watchKey = watchedDirectory.register(watcher, ENTRY_CREATE);
		} catch (IOException e) {
			log.error("failed to start the watch service.");
			throw new RuntimeException(e);
		}
	}
	
	
	public void initialScan() {
		
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(watchedDirectory, pattern)) {
			
			for (Path path : dirStream) {
				String fileName = path.toString();
				
				if (!new File(fileName + ".uploaded").exists() && !new File(fileName + ".failed").exists()) {
					
					fileUploadService.upload(path.toFile());
				}
			}
			
		} catch (IOException e) {
			log.error("failed to scan the directory: ", e);
			throw new RuntimeException(e);
		}
	}
	
	
	public void watch() {
		
		while (true) {
			for (WatchEvent<?> event : watchKey.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
				
				// This key is registered only
				// for ENTRY_CREATE events,
				// but an OVERFLOW event can
				// occur regardless if events
				// are lost or discarded.
				if (kind == OVERFLOW) {
					continue;
				}
				
				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path filename = ev.context();
				
				if (FileSystems.getDefault().getPathMatcher("glob:" + pattern).matches(filename)) {
					File file = new File(watchedDirectory.toString() + "/" + filename.toString());
					fileUploadService.upload(file);
				}
			}
			
			
			// Reset the key -- this step is critical if you want to
			// receive further watch events.  If the key is no longer valid,
			// the directory is inaccessible so exit the loop.
			boolean valid = watchKey.reset();
			if (!valid) {
				log.error("unable to continue watching folder " + arguments.getFolder());
				System.exit(1);
			}
		}
	}
	
}
