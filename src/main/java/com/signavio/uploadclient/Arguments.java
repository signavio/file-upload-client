package com.signavio.uploadclient;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.jankroken.commandline.annotations.LongSwitch;
import com.github.jankroken.commandline.annotations.Option;
import com.github.jankroken.commandline.annotations.Required;
import com.github.jankroken.commandline.annotations.ShortSwitch;
import com.github.jankroken.commandline.annotations.SingleArgument;

public class Arguments {
	
	private String folder = "./";
	private String pattern = "*.xes.gz";
	private String endpoint;
	private String token;
	
	
	public String getFolder() {
		return folder;
	}
	
	
	@Option
	@LongSwitch("folder")
	@ShortSwitch("f")
	@SingleArgument
	public void setFolder(String folder) throws FileNotFoundException {
		
		File dir = new File(folder);
		if (!dir.exists()) {
			throw new FileNotFoundException();
		}
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException(folder + " is not a directory");
		}
		this.folder = folder;
	}
	
	
	public String getPattern() {
		return pattern;
	}
	
	
	@Option
	@LongSwitch("pattern")
	@ShortSwitch("p")
	@SingleArgument
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	
	public String getEndpoint() {
		return endpoint;
	}
	
	
	@Option
	@LongSwitch("endpoint")
	@ShortSwitch("e")
	@SingleArgument
	@Required
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	
	public String getToken() {
		return token;
	}
	
	
	@Option
	@LongSwitch("token")
	@ShortSwitch("t")
	@SingleArgument
	@Required
	public void setToken(String token) {
		this.token = token;
	}
	
	
}
