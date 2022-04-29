package com.teledentistry.admin.tests;

import org.openqa.selenium.By;


import org.testng.annotations.Test;



public class ParallelExecutionTest extends RemoteDriver

{
	
	@Test
		public void verify() throws InterruptedException
		{
	    	getDriver().get("https://www.saucedemo.com/");
	    	Thread.sleep(3000);
	    	getDriver().findElement(By.xpath("//input[@id='user-name']")).sendKeys("prasad");
	    	Thread.sleep(3000);
	    	getDriver().findElement(By.xpath("(//input[@id='password'])[1]")).sendKeys("durga");
	    	Thread.sleep(3000);
	    	getDriver().findElement(By.xpath("//input[@id='login-button']")).click();
	    	Thread.sleep(3000);
		   
		   		}

	
	}
	
	
	

