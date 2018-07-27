package com.signavio.uploadclient.service;

import java.io.File;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class RetryStrategyImpl implements RetryStrategy {
	
	private static final Logger log = getLogger(RetryStrategyImpl.class);
	
	private final List<Pair<Long, String>> retryStrategies = List.of(
			Pair.of(10_000L, "10 seconds"),
			Pair.of(300_000L, "5 minutes"),
			Pair.of(1_800_000L, "30 minutes"));
	
	
	public UploadResult retry(File file, Integer count, Function<File, UploadResult> function) {
		try {
			log.info("scheduling retry in " + retryStrategies.get(count - 1).getRight() + " for file " + file
					.getAbsolutePath());
			Thread.sleep(retryStrategies.get(count - 1).getLeft());
			log.info("retry no. " + count + " for file " + file.getAbsolutePath());
			return function.apply(file);
			
		} catch (InterruptedException e) {
			log.error("Retry upload of " + file.getAbsolutePath() + " failed with an exception: ", e);
			return UploadResult.FAILED;
		}
	}
	
}
