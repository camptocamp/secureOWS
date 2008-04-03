package com.camptocamp.owsproxy;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.LogManager;

import com.camptocamp.owsproxy.logging.OWSLogHandler;
import com.camptocamp.owsproxy.logging.OWSLogger;
import com.camptocamp.owsproxy.parameters.DevLogFileParameter;

import junit.framework.TestCase;

public class OWSHeadlessClientTest extends TestCase {

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		LogManager.getLogManager().reset();
		LogManager.getLogManager().readConfiguration();
	}

	public void testParseProgramArgsNoUserName() {
		try {
			OWSHeadlessClient.parseProgramArgs("http://someurl.com");
			fail("Should throw exception");
		} catch (IllegalArgumentException e) {
			// good
		}
		try {
			OWSHeadlessClient.parseProgramArgs("-proxyHost",
					"http://anotherURL.com", "-proxyPort", "3128",
					"http://someurl.com");
			fail("Should throw exception");
		} catch (IllegalArgumentException e) {
			// good
		}
	}

	public void testParseProgramArgsHelp() {
		try {
			OWSHeadlessClient.parseProgramArgs("-h");
			fail("Should throw exception");
		} catch (IllegalArgumentException e) {
			// good
		}
	}

	public void testParseProgramArgsNoURL() {
		try {
			OWSHeadlessClient.parseProgramArgs("username:password");
			fail("Should throw exception");
		} catch (IllegalArgumentException e) {
			// good
		}
		try {
			OWSHeadlessClient.parseProgramArgs("-proxyHost",
					"http://anotherURL.com", "-proxyPort", "3128",
					"username:password");
			fail("Should throw exception");
		} catch (IllegalArgumentException e) {
			// good
		}
	}

	public void testParseProgramArgsMultipleColons() {
		try {
			// a restriction is that password cannot have a colon
			OWSHeadlessClient.parseProgramArgs("username:password:doda",
					"http://someurl.com");
			fail("Should throw exception");
		} catch (IllegalArgumentException e) {
			// good
		}
	}

	public void testParseProgramArgsProxyHostNoPort() {
		try {
			OWSHeadlessClient.parseProgramArgs("-proxyHost",
					"http://anotherURL.com", "username:password",
					"http://someurl.com");
			fail("Should throw exception");
		} catch (IllegalArgumentException e) {
			// good
		}
	}

	public void testParseProgramArgsProxyPortNoHost() {
		try {
			OWSHeadlessClient.parseProgramArgs("-proxyPort", "3128",
					"username:password", "http://someurl.com");
			fail("Should throw exception");
		} catch (IllegalArgumentException e) {
			// good
		}
	}

	public void testParseProgramArgsTwoOptionsTogether() {
		try {
			OWSHeadlessClient.parseProgramArgs("-proxyHost", "-proxyPort",
					"http://anotherURL.com", "3128", "username:password",
					"http://someurl.com");
			fail("Should throw exception");
		} catch (IllegalArgumentException e) {
			// good
		}
	}

	public void testParseProgramArgsIllegalURL() {
		try {
			OWSHeadlessClient.parseProgramArgs("username:password",
					"someurl.com");
			fail("Should throw exception");
		} catch (IllegalArgumentException e) {
			// good
		}
	}

	public void testParseProgramArgsProxyUserNoProxyHost() {
		try {
			OWSHeadlessClient.parseProgramArgs("-proxyUser", "user",
					"username:password", "http://someurl.com");
			fail("Should throw exception");
		} catch (IllegalArgumentException e) {
			// good
		}
	}

	public void testParseProgramArgsProxyUser() {
		OWSHeadlessClient.parseProgramArgs("-proxyHost",
				"http://anotherURL.com", "-proxyPort", "3128", "-proxyUser",
				"user:password", "username:password", "http://someurl.com");
	}

	public void testParseProgramArgsNoPassword() {
		assertNotNull(OWSHeadlessClient.parseProgramArgs("username",
				"http://someurl.com"));
	}

	public void testParseProgramArgsWithPassword() {
		assertNotNull(OWSHeadlessClient.parseProgramArgs("username:password",
				"http://someurl.com"));
	}

	public void testParseProgramArgsAllOptions() {
		assertNotNull(OWSHeadlessClient.parseProgramArgs("-proxyHost",
				"http://anotherURL.com", "-proxyPort", "3128",
				"username:password", "http://someurl.com"));
	}

	public void testChangeDevLogLevel() throws Exception {
		OWSHeadlessClient.parseProgramArgs("-devLogLevel", "FINER",
				"username:password", "http://someurl.com");

		assertEquals(OWSLogger.DEV.logger().getLevel(), Level.FINER);

		OWSHeadlessClient.parseProgramArgs("-devLogLevel", "severe",
				"username:password", "http://someurl.com");

		assertEquals(OWSLogger.DEV.logger().getLevel(), Level.SEVERE);

		OWSHeadlessClient.parseProgramArgs("-devLogLevel", "400",
				"username:password", "http://someurl.com");

		assertEquals(OWSLogger.DEV.logger().getLevel(), Level.FINER);
	}

	public void testChangeDevLogFile() throws Exception {
		String logFile = "alternativeFile.log";
		OWSHeadlessClient.parseProgramArgs("-devLogFile", logFile,
				"username:password", "http://someurl.com");

		OWSLogHandler owsHandler = DevLogFileParameter.lookupOWSLogHandler();

		assertEquals(logFile, owsHandler.getDevLogFile());

	}

	public void testChangeUserLogFile() throws Exception {
		String logFile = "alternativeFile.log";
		OWSHeadlessClient.parseProgramArgs("-userLogFile", logFile,
				"username:password", "http://someurl.com");

		OWSLogHandler owsHandler = DevLogFileParameter.lookupOWSLogHandler();

		assertEquals(logFile, owsHandler.getUserLogFile());
	}

	public void testChangeLogConfigurationFile() throws Exception {
		URL configurationURL = getClass().getResource("loggingTest.properties");
		String logConfiguration = configurationURL.getFile();
		OWSHeadlessClient.parseProgramArgs("-logConf", logConfiguration,
				"username:password", "http://someurl.com");

		assertNull(DevLogFileParameter.lookupOWSLogHandler());
	}
}
