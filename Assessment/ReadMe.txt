Executing the Test:
	The test is written as part of maven test phase
	Executing in command-line:
		Navigate to project folder
		Execute the command "mvn test"
	Executing in IDE:
		Right on project->Run As->maven test
		
	After the execution, the report will be available at target/surefire-reports/test-report-<timestamp>.html
		
Configuration:
	Parent package:com.test.java
	Log and other configuration can be specified in com/constants/TestConstants.java
	Application configuration can be specified in com/properties/config.properties
	Test cases must be placed in com/testdata

	Main class
		com/main/TestApplication.java