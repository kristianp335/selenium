package com.liferay.sales.selenium.firefox;

import com.liferay.sales.selenium.DriverInitializer;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class FirefoxDriverInitializer implements DriverInitializer {

	@Override
	public WebDriver getDriver() {
		FirefoxOptions options = new FirefoxOptions();
//		options.addArguments("--headless");
		FirefoxDriver driver = new FirefoxDriver(options);
		driver.manage().window().setSize(new Dimension(1536, 835));
		return driver;
	}

}
