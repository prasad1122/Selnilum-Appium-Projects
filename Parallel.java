package com.teledentistry.admin.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Parallel {
	
	WebDriver driver;
	
	
	@Test
	
	public void Threadsafe() throws InterruptedException
	{
		WebDriverManager.chromedriver().setup();
		driver=new ChromeDriver();
		driver.get("https://opensource-demo.orangehrmlive.com/");
		
	     Thread.sleep(2000);
		driver.findElement(By.xpath("//span[normalize-space()='Username']")).sendKeys("Admin");
		   
	}

}
