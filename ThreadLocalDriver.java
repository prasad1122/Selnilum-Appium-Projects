package com.teledentistry.util;

import org.openqa.selenium.WebDriver;

public class ThreadLocalDriver

{
	
    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<WebDriver>();

    public synchronized void setTLDriver(WebDriver driver) {
    	
    	tlDriver.set(driver); 
    	}

    public synchronized WebDriver getTLDriver()
    {
    	
        return tlDriver.get();
    }

}
