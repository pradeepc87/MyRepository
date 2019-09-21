package com.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.*;
import org.testng.Assert;

import com.constants.TestConstants;
import com.utils.CommonUtils;
import com.utils.JsonUtils;
import com.utils.RestUtils;

@Listeners(com.listeners.TestReporter.class)
public class TestApplication {
	
	private CommonUtils commonUtils;
	private JsonUtils jsonUtils;
	private RestUtils restUtils;
	private JSONObject applicationResponse;
	private static Logger log = Logger.getLogger(TestApplication.class);
	String testCasePackagePath = TestConstants.testCasePackagePath ;
	
	
	@BeforeTest
	private void setup() {
		log.info("Intializing Utils");
		this.commonUtils=new CommonUtils();
		this.jsonUtils=new JsonUtils();
		this.restUtils=new RestUtils();
		setUpLogger();	
	}
	
	@BeforeClass
	private void getApplicationResponse() {
		String applicationUrl=commonUtils.getProperty("config.properties","application.url");
		log.info("Application url : " + applicationUrl);
		applicationResponse=restUtils.getJsonReponseFromRestApi(applicationUrl);
		log.info("Application response:" + applicationResponse.toString());
	}
	
	@DataProvider(name="Test1DataProvider")
    public Object[][] getDataForTest(){
		//This method will feed test data to testApplicationResponse as multidimensional array. In each row the first element will be key and second element will be value and so the test will execute the number of times as the number of elements in the test case
		String testCaseFilePath = testCasePackagePath + "TestCase1.json";
		File testCaseFile = new File(getClass().getClassLoader().getResource(testCaseFilePath).getFile());
		String testCaseContents="";
		try {
			testCaseContents = new Scanner(new BufferedReader(new FileReader(testCaseFile))).useDelimiter("\\A").next();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject testCaseJson=jsonUtils.getJsonFromString(testCaseContents);
		Object[][] testDataOutput=new Object[testCaseJson.keySet().size()][2];
		int i=0;
		for (Iterator<String> itr=testCaseJson.keys();itr.hasNext();)
		{
			String key=itr.next();
			System.out.println(key + "-->" + testCaseJson.get(key) + "-->" + testCaseJson.get(key).getClass().getSimpleName());
			testDataOutput[i][0]=key;
			testDataOutput[i][1]=testCaseJson.get(key);
			i=i+1;
		}
		return testDataOutput; 
		
    }
	
	@Test(dataProvider="Test1DataProvider")//This test will run for every field mentioned in TestCase1.json
	public void testApplicationResponse(String field, Object value)
	{
		log.info("Starting Test for " +field + "-->" + value);
		//Checking if the field is available
		Assert.assertTrue(applicationResponse.has(field),"Key " + field + " is not available in application response");
		if (applicationResponse.has(field)) {
			//Checking if the value type matches
			Boolean typeMatch=value.getClass().getSimpleName().equals(applicationResponse.get(field).getClass().getSimpleName());
			Assert.assertTrue(typeMatch,"Key " + field + " if not expected type in application response. Expected type - " + value.getClass().getSimpleName() + ". Actual - " +applicationResponse.get(field).getClass().getSimpleName() );
			if (typeMatch) {
				if (value.getClass().getSimpleName().equals("JSONArray")) {
					for (int j = 0; j < ((JSONArray)value).length(); j++) {
						JSONObject currentExpectedObject=((JSONArray) value).getJSONObject(j);
						Boolean foundObjectMatch=false;
						JSONArray objects=(JSONArray) applicationResponse.get(field);
						for (int i = 0, size = objects.length(); i < size; i++) {
							JSONObject currentObject=objects.getJSONObject(i);
							Boolean valueMatch=true;
							if (((JSONObject)currentExpectedObject).keySet().size()==0) {
								log.info("Expected Json is empty");
								foundObjectMatch=true;
								break;
							}
							if (currentObject.keySet().size()==0) {
								log.info("Actual Json is empty");
								foundObjectMatch=false;
								continue;
							}
							for (Iterator<String> itr=((JSONObject)currentExpectedObject).keys();itr.hasNext();)
							{
								String key=itr.next();
								if (!(currentObject.has(key) && currentObject.get(key).equals(((JSONObject)currentExpectedObject).get(key)))) {
									valueMatch=false;
								}
							}
							if (valueMatch) {
								foundObjectMatch=true;
								break;
							}
						}
						Assert.assertTrue(foundObjectMatch, "Could not find the matching object inside \"" + field + "\" in the application repsonse with value - " + ((JSONObject)currentExpectedObject).toString());
					}
				}else if(value.getClass().getSimpleName().equals("JSONObject")){
					//To be implemented
				}else {
					Assert.assertEquals(applicationResponse.get(field), value, "Value for Key " + field + " is not matching in application response.");
				}
			}
			
		}
	}
	
	private void setUpLogger() {
		ConsoleAppender console = new ConsoleAppender(); 
		  
		  String PATTERN = TestConstants.logConsolePattern;
		  console.setLayout(new PatternLayout(PATTERN)); 
		  console.activateOptions();
		  log.addAppender(console);

		  FileAppender fa = new FileAppender();
		  fa.setName("FileLogger");
		  fa.setFile(TestConstants.logFileName);
		  fa.setLayout(new PatternLayout(TestConstants.logFilePattern));
		  fa.setAppend(false);
		  fa.activateOptions();

		  log.setLevel(Level.ALL);
		  log.addAppender(fa);  
	}

	

}
