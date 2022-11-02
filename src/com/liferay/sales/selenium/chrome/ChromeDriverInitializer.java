package com.liferay.sales.selenium.chrome;

import com.liferay.sales.selenium.api.DriverInitializer;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDriverInitializer implements DriverInitializer {

	@Override
	public WebDriver getDriver() {
		ChromeOptions options = new ChromeOptions();
//		options.addArguments("--headless");
		ChromeDriver driver = new ChromeDriver(options);
		driver.manage().window().setSize(new Dimension(1536, 835));
		return driver;
	}

}
