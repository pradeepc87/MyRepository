package com.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class CommonUtils {

	public String getProperty(String fileName, String property) {

		String fileSeparator = String.valueOf(File.separatorChar);
		String propertyPackagePath = "com" + fileSeparator + "properties" + fileSeparator;
		File propertyFile = new File(getClass().getClassLoader().getResource(propertyPackagePath+fileName).getFile());
		BufferedReader propertyBufferedReader;
		Properties properties = null;
		try {
			propertyBufferedReader = new BufferedReader(new FileReader(propertyFile.getAbsolutePath()));
			properties = new Properties();
			properties.load(propertyBufferedReader);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return properties.getProperty(property);
	}

}
