package com.teledentistry.admin.tests;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ThreadGuard;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.util.TimeUtils;

import io.github.bonigarcia.wdm.WebDriverManager;

public class RemoteDriver

{
	protected static final ThreadLocal<WebDriver>  driver= new ThreadLocal<WebDriver>();
	
	
	
	
	@BeforeMethod
	public void verifyThread()
	{
		
		WebDriverManager.chromedriver().setup();
		 
		  driver.set(ThreadGuard.protect(new ChromeDriver()));
		  getDriver().manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);
	      getDriver().manage().window().maximize();
		  
	}

	
	public static WebDriver getDriver()
	{
		return driver.get();
	}
	
	@AfterMethod
	public void verifyQuit()
	{
		getDriver().quit();
		driver.remove();
	}
}
