package com.liferay.sales.selenium.liferaycity;
import java.util.HashMap;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class LiferayCity {
	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
		
		HashMap<String,String> cityUsers = new HashMap<String, String>();
		cityUsers.put("wwilliams", "liferay$"); 
		cityUsers.put("ssmith", "liferay$");   

		WebDriver driver = new ChromeDriver();
		JavascriptExecutor js = (JavascriptExecutor) driver;
		LiferayCityClickpath1 path1 = new LiferayCityClickpath1(driver, js, "https://webserver-lctcity-prd.lfr.cloud/");
		
		path1.run(cityUsers);
	}
}
