package com.constants;

import java.io.File;

public class TestConstants {
	
	public static final String logFileName="execution-info.log";
	
	public static final String fileSeparator = String.valueOf(File.separatorChar);
	
	public static final String testCasePackagePath="com" + fileSeparator + "testdata" + fileSeparator ;

	public static final String logConsolePattern="%d [%p|%c|%C{1}] %m%n";
	
	public static final String logFilePattern="%d %5p [%c{1}] %m%n";
	
}
