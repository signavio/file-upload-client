package com.signavio.uploadclient;

import java.lang.reflect.InvocationTargetException;

import com.github.jankroken.commandline.CommandLineParser;
import com.github.jankroken.commandline.OptionStyle;
import com.signavio.uploadclient.service.FileScanService;

public class App {
	
	public static void main(String[] args) {
		
		Arguments arguments = null;
		try {
			arguments = CommandLineParser.parse(Arguments.class, args, OptionStyle.LONG_OR_COMPACT);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		FileScanService fileScanService = new FileScanService(arguments);
		
		fileScanService.initialScan();
		fileScanService.watch();
		
	}
	
}
