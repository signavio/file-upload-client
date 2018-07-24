package com.signavio.uploadclient.exception;

import java.text.MessageFormat;

public class MissingPropertyException extends RuntimeException {
	
	public MissingPropertyException(String missingKey){
		super(MessageFormat.format("environment variable ''{0}'' is not set", missingKey));
	}
}
