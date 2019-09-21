package com.listeners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.xml.XmlSuite;


public class TestReporter implements IReporter {

	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		
		try
		{
			String reportTemplate = getReportTemplate(outputDirectory);
			String customReportTitle = "Execution Report - " + new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Date());
			reportTemplate = reportTemplate.replaceAll("REPORT_TITLE_TO_BE_REPLACED", customReportTitle);
			reportTemplate = updateTestCount(suites,reportTemplate);
			String customTestMethodSummary = getTestSummary(suites);
			
			reportTemplate = reportTemplate.replace("Test_Case_Detail", customTestMethodSummary);
			
			// This report will get generated in target/surefire-reports directory.
			File targetFile = new File(outputDirectory + "/test-report-"+ new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date()) +".html");
			FileWriter fw = new FileWriter(targetFile);
			fw.write(reportTemplate);
			fw.flush();
			fw.close();
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private String getReportTemplate(String outputDirectory)
	{
		StringBuffer reportContent = new StringBuffer();
		
		try {
		
			File file = new File(outputDirectory+String.valueOf(File.separatorChar)+"report-template.html");
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while(line!=null)
			{
				reportContent.append(line+"\n");
				line = br.readLine();
			}
			
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}finally
		{
			return reportContent.toString();
		}
	}
	
	private String getCustomReportTitle(String title)
	{
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(title + " " + this.getDateInStringFormat(new Date()));
		return retBuf.toString();
	}
	
	private String updateTestCount(List<ISuite> suites, String report)
	{
			int totalTestCount = 0;
			int totalTestPassed = 0;
			int totalTestFailed = 0;
			int totalTestSkipped = 0;
			
			for(ISuite tempSuite: suites)
			{
				Map<String, ISuiteResult> testResults = tempSuite.getResults();
				for (ISuiteResult result : testResults.values()) {
					
					ITestContext testObj = result.getTestContext();
					
					totalTestPassed = testObj.getPassedTests().getAllMethods().size();
					totalTestSkipped = testObj.getSkippedTests().getAllMethods().size();
					totalTestFailed = testObj.getFailedTests().getAllMethods().size();
					
					totalTestCount = totalTestPassed + totalTestSkipped + totalTestFailed;
				}
			}
			
			report=report.replace("Total_Tests_To_Be_Replaced", new Integer(totalTestCount).toString());
			report=report.replace("Total_Passed_To_Be_Replaced", new Integer(totalTestPassed).toString());
			report=report.replace("Total_Failed_To_Be_Replaced", new Integer(totalTestFailed).toString());
			report=report.replace("Total_Skipped_To_Be_Replaced", new Integer(totalTestSkipped).toString());
			
			return report;
			
	}

	private String getDateInStringFormat(Date date)
	{
		return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
	}
	
	private String formatTimeStamp(long deltaTime)
	{
		long milli = deltaTime;
		
		long seconds = deltaTime / 1000;
		
		long minutes = seconds / 60;
		
		long hours = minutes / 60;
		
		return (hours + ":" + minutes + ":" + seconds + ":" + milli);
		
	}
	
	@SuppressWarnings("finally")
	private String getTestSummary(List<ISuite> suites)
	{
		StringBuffer testSummaryContents = new StringBuffer();
		
		try
		{
			for(ISuite tempSuite: suites)
			{
				Map<String, ISuiteResult> testResults = tempSuite.getResults();
				
				for (ISuiteResult result : testResults.values()) {
					
					ITestContext testObj = result.getTestContext();

					IResultMap testFailedResult = testObj.getFailedTests();
					String failedTestMethodInfo = this.getTestMethodReport(testFailedResult, false, false);
					testSummaryContents.append(failedTestMethodInfo);
					
					IResultMap testPassedResult = testObj.getPassedTests();
					String passedTestMethodInfo = this.getTestMethodReport(testPassedResult, true, false);
					testSummaryContents.append(passedTestMethodInfo);
				}
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			return testSummaryContents.toString();
		}
	}
	
	private String getTestMethodReport(IResultMap testResultMap, boolean passedReault, boolean skippedResult)
	{
		StringBuffer snippetContent = new StringBuffer();
		
		String resultTitle = "Tests";
		
		String color = "green";
		
		
		if(!passedReault)
		{
			resultTitle += " - Failed [" +  testResultMap.getAllResults().size() + " test(s)]";
			color = "#FBCCC7";
			snippetContent.append("\t<tr border: 3px solid #ddd;" + "  bgcolor=#FC5635><td colspan=6><b>" + resultTitle + "</b></center></td></tr>\n");
			
		}else
		{
			resultTitle += " - Passed [" +  testResultMap.getAllResults().size() + " test(s)]";
			color = "#CBFBC7";
			snippetContent.append("\t<tr border: 3px solid #ddd;" + "  bgcolor=#69C162><td colspan=6><b>" + resultTitle + "</b></center></td></tr>\n");
			
		}
		Set<ITestResult> testResultSet = testResultMap.getAllResults();
			
		for(ITestResult testResult : testResultSet)
		{
			String startDateStr = "";
			String executeTimeStr = "";
			String reporterMessage = "";
			String exceptionMessage = "";
			
			//Get startDateStr
			long startTimeMillis = testResult.getStartMillis();
			startDateStr = this.getDateInStringFormat(new Date(startTimeMillis));
				
			//Get Execute time.
			long deltaMillis = testResult.getEndMillis() - testResult.getStartMillis();
			executeTimeStr = this.formatTimeStamp(deltaMillis);
				
			//Get parameter list.
			
				
			//Get reporter message list.
			List<String> repoterMessageList = Reporter.getOutput(testResult);
			for(String tmpMsg : repoterMessageList)				
			{
				reporterMessage += tmpMsg;
				reporterMessage += " ";
			}
				
			//Get exception message.
			Throwable exception = testResult.getThrowable();
			if(exception!=null)
			{
				exceptionMessage = exception.getMessage();
			}
			
			snippetContent.append("\t<tr bgcolor=" + color + ">\n");
			
			Object paramObjArr[] = testResult.getParameters();
			for(Object paramObj : paramObjArr)
			{
				snippetContent.append("\t\t<td>");
				snippetContent.append(paramObj.toString());
				snippetContent.append("</td>\n");
				
			}
			/* Add start time. */
			snippetContent.append("\t\t<td>");
			snippetContent.append(startDateStr);
			snippetContent.append("</td>\n");
			
			/* Add execution time. */
			snippetContent.append("\t\t<td>");
			snippetContent.append(executeTimeStr);
			snippetContent.append("</td>\n");
			
			/* Add parameter. */
			
			/* Add reporter message. */
			snippetContent.append("\t\t<td>");
			snippetContent.append(reporterMessage);
			snippetContent.append("</td>\n");
			
			/* Add exception message. */
			snippetContent.append("\t\t<td>");
			snippetContent.append(exceptionMessage);
			snippetContent.append("</td>\n");
			
			snippetContent.append("\t</tr>\n");

		}
		
		return snippetContent.toString();
	}
	
	
}
