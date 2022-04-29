package com.teledentistry.admin.tests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.teledentistry.admin.pages.AdminCommonForms;
import com.teledentistry.admin.pages.AdminHomePage;
import com.teledentistry.admin.pages.AdminLoginPage;
import com.teledentistry.util.DataDrivenManager;
import com.teledentistry.util.ExtentManager;
import com.teledentistry.util.ThreadLocalDriver;
import com.teledentistry.util.WebDriverManagers;

public class AdminTestBase {

	WebDriver driver;
	AdminHomePage adminHomePG;
	AdminLoginPage loginPage;
	AdminCommonForms commonForms;
	// Make testConfig static so that it can be initialized only once in
	// beforeSuite()
	// and it can be shared across all test classes/ cases
	static Properties testConfig;
	Logger logger;
	private static final ThreadLocalDriver  threadLocalDriver= new ThreadLocalDriver();
	protected static ExtentReports extent;
	protected static ThreadLocal parentTestThread = new ThreadLocal();
	protected static ThreadLocal testThread = new ThreadLocal();
	protected ExtentTest erParentTest; // for test class
	protected ExtentTest testReport; // for test method

	String testFailureScreenshotPath;

	@BeforeSuite
	public void beforeSuite() throws FileNotFoundException, IOException {
		PropertyConfigurator.configure("log4j.properties");

		// Get Test Config
		testConfig = new Properties();

		testConfig.load(new FileInputStream("testconfig.properties"));

		String extentReportFilePath = "AdminExtentHtmlReport.html";
		extent = ExtentManager.createInstance(extentReportFilePath);
		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(extentReportFilePath);
		extent.attachReporter(htmlReporter);
	}

	@BeforeMethod
//	@Parameters("browser")
	public void testSetup(Method method /* , String browser */) throws InterruptedException {
		logger = Logger.getLogger(this.getClass().getSimpleName());
		logger.info("################# Starting " + method.getName() + " Test Case #################");

	
	
		
		 threadLocalDriver.setTLDriver(driver);
	     driver = threadLocalDriver.getTLDriver();
	     
	     
	     
//		driver = WebDriverManagers.createDriver(browser);
		driver = WebDriverManagers.createDriver(testConfig.getProperty("browser"));

		// Launch website
		driver.get(testConfig.getProperty("adminBaseURL"));

		commonForms = new AdminCommonForms(driver);
		adminHomePG = new AdminHomePage(driver);
		loginPage = new AdminLoginPage(driver);
		adminHomePG = loginPage.login(testConfig.getProperty("adminusername"), testConfig.getProperty("adminpassword"));

		logger.info("Verifying the login for account: " + testConfig.getProperty("adminusername") + ","
				+ testConfig.getProperty("adminpassword"));
	}

	@AfterMethod
	public void testCleanUp(ITestResult result) throws InterruptedException, IOException {
		// Capture screenshot when test fails
		captureTestFailureScreenshot(result);

		WebDriverManagers.quitDriver(driver);

		logger.info("################# Ending " + result.getName() + " Test Case #################");
	}

	public void captureTestFailureScreenshot(ITestResult result) throws IOException {

		if (result.getStatus() == ITestResult.FAILURE) {
			// Gives path like
			// TestFailureScreenshots\com.teledentistry.tests.AdminAddPatientPageTest.verifyAddFormTitle.png
			testFailureScreenshotPath = "TestFailureScreenshots/" + getClass().getName() // full class name -
																							// com.teledentistry.tests.AdminAddPatientPageTest
					+ "." + result.getName() // test method name - testPageTitle
					+ ".png";

			// Files, Paths classes are provided by java.nio.file package
			// Create the directory if doesn't exist
			if (Files.notExists(Paths.get("TestFailureScreenshots"))) {
				Files.createDirectory(Paths.get("TestFailureScreenshots"));
			}

			// Delete the old file if exists
			Files.deleteIfExists(Paths.get(testFailureScreenshotPath));

			// Create new test failure screenshot file
			WebDriverManagers.getScreenshot(driver, testFailureScreenshotPath);
		}
	}

	@DataProvider
	public Object[][] dataProvider(Method method) {
		DataDrivenManager ddm = new DataDrivenManager(testConfig.getProperty("testdatafile"));
		Object[][] TestData = ddm.getTestCaseDataSets(testConfig.getProperty("testdatasheet"), method.getName());

		return TestData;
	}

	@BeforeClass
	public synchronized void extentReportBeforeClass() {
		// Creating extent reports test class for every TestNG test class
		erParentTest = extent.createTest(getClass().getSimpleName());
		parentTestThread.set(erParentTest);
	}

	@BeforeMethod
	public synchronized void extentReportBeforeMethod(Method method) {
		// In ER test class, create test node based TestNG test method
		testReport = erParentTest.createNode(method.getName());
		testThread.set(testReport);
	}

	@AfterMethod(dependsOnMethods = "testCleanUp")
	public synchronized void extentReportAfterMethod(ITestResult result) throws IOException {

		if (result.getStatus() == ITestResult.FAILURE)
			// Fail the testReport when TestNG test is failed
			testReport.fail(result.getThrowable(),
					MediaEntityBuilder.createScreenCaptureFromPath(testFailureScreenshotPath).build());
		else if (result.getStatus() == ITestResult.SKIP)
			// Skip the testReport when TestNG test is skipped
			testReport.skip(result.getThrowable(),
					MediaEntityBuilder.createScreenCaptureFromPath(testFailureScreenshotPath).build());
		else
			// Pass the testReport when TestNG test is passed
			testReport.pass("Test passed");

		extent.flush();
	}

}
