package com.signavio.uploadclient.service;

import java.io.File;
import java.util.function.Function;

import com.google.inject.ImplementedBy;

@ImplementedBy(RetryStrategyImpl.class)
public interface RetryStrategy {
	
	UploadResult retry(File file, Integer count, Function<File, UploadResult> function);
}
