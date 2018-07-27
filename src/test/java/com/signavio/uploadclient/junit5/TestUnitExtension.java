package com.signavio.uploadclient.junit5;

import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.MockitoAnnotations;

import static java.util.Arrays.asList;

public class TestUnitExtension implements BeforeEachCallback, AfterEachCallback {
	
	@Override
	public void beforeEach(ExtensionContext context) {
		Object testInstance = context.getRequiredTestInstance();
		MockitoAnnotations.initMocks(testInstance);
		
		// Make @Bind annotated fields available for injection
		List<Module> modules = asList(
				BoundFieldModule.of(testInstance));
		
		Injector injector = Guice.createInjector(modules);
		injector.injectMembers(testInstance);
	}
	
	
	@Override
	public void afterEach(ExtensionContext context) {
	}
}
