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
import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;
import com.signavio.uploadclient.FileUploadConfiguration;
import org.slf4j.Logger;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static org.slf4j.LoggerFactory.getLogger;

public class FileScanServiceImpl implements FileScanService {
	
	private static final Logger log = getLogger(FileScanServiceImpl.class);
	
	private final FileUploadConfiguration config;
	private final String pattern;
	private final FileUploadService fileUploadService;
	private final Path watchedDirectory;
	private WatchService watcher;
	private final Set<Path> createdFiles = new HashSet<>();
	
	
	@Inject
	FileScanServiceImpl(FileUploadConfiguration config, FileUploadService fileUploadService) {
		this.config = config;
		this.pattern = config.getPattern();
		this.fileUploadService = fileUploadService;
		
		try {
			watcher = FileSystems.getDefault().newWatchService();
			watchedDirectory = FileSystems.getDefault().getPath(config.getFolder());
			watchedDirectory.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
		} catch (IOException e) {
			String msg = "failed to start the watch service.";
			log.error(msg);
			throw new RuntimeException(msg, e);
			
		}
	}
	
	
	@Override
	public void initialScan() {
		
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(watchedDirectory, pattern)) {
			
			log.info("scanning directory " + watchedDirectory.toString());
			for (Path path : dirStream) {
				String fileName = path.toString();
				
				if (path.toFile().isFile()
						&& !new File(fileName + ".uploaded").exists()
						&& !new File(fileName + ".failed").exists()) {
					
					fileUploadService.upload(path.toFile());
				}
			}
			
		} catch (IOException e) {
			log.error("failed to scan the directory: ", e);
			// continue watching
		}
	}
	
	
	@Override
	public void watch() {
		
		WatchKey watchKey;
		try {
			watchKey = watcher.take();
		} catch (InterruptedException e) {
			log.info("interrupted while waiting: ", e);
			return;
		}
		for (WatchEvent<?> event : watchKey.pollEvents()) {
			WatchEvent.Kind<?> kind = event.kind();
			
			log.info("event " + event.context() + " of kind " + kind.toString());
			
			// This key is registered only
			// for ENTRY_CREATE events,
			// but an OVERFLOW event can
			// occur regardless if events
			// are lost or discarded.
			if (kind.equals(OVERFLOW)) {
				initialScan();
				continue;
			}
			
			WatchEvent<Path> ev = (WatchEvent<Path>) event;
			Path path = ev.context();
			
			String filename = watchedDirectory.toString() + "/" + path.toString();
			log.debug("checking file " + filename);
			if (FileSystems.getDefault().getPathMatcher("glob:" + pattern).matches(path)
					&& new File(filename).isFile()
					&& !new File(filename + ".uploaded").exists()
					&& !new File(filename + ".failed").exists()) {
				
				if (kind.equals(ENTRY_CREATE)) {
					createdFiles.add(path);
					continue;
				} else if (kind.equals(ENTRY_MODIFY) && createdFiles.contains(path)) {
					
					File file = new File(filename);
					fileUploadService.upload(file);
					createdFiles.remove(path);
				}
				
			}
		}
		
		
		// Reset the key -- this step is critical if you want to
		// receive further watch events.  If the key is no longer valid,
		// the directory is inaccessible so exit the loop.
		boolean valid = watchKey.reset();
		if (!valid) {
			log.error("unable to continue watching folder " + config.getFolder());
			System.exit(1);
		}
	}
	
}
