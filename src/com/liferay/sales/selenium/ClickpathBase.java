package com.liferay.sales.selenium;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ClickpathBase {

	private WebDriver driver;
	private JavascriptExecutor js;
	int defaultSleep = 2000;
	protected String baseUrl;
	
	public ClickpathBase(WebDriver driver, JavascriptExecutor js, String baseUrl) {
		this.driver = driver;
		this.js = js;
		this.baseUrl = baseUrl;
	}
	
	protected void deleteAllCookies() {
		driver.manage().deleteAllCookies();
	}
	
	/**
	 * Navigate to URL, then sleep for default interval
	 * @param url
	 */
	
	protected void doGoTo(String url) {
	    driver.get(url);
	    sleep(defaultSleep);
	}

	/**
	 * Click link with given text, then sleep for default interval
	 * @param url
	 */

	protected void doClickText(String text) {
		driver.findElement(By.linkText(text)).click();
		sleep(defaultSleep);
	}

	/**
	 * Click one of the links with given text, then sleep for default interval
	 * @param url
	 */

	protected void doClickRandomText(String[] text) {
		int pos = (int) (Math.random()*text.length);
		driver.findElement(By.linkText(text[pos])).click();
		sleep(defaultSleep);
	}

	protected WebElement getLoginField(String field) {
		return driver.findElement(By.id("_com_liferay_login_web_portlet_LoginPortlet_" + field));
	}

	/**
	 * sleep for given interval
	 * @param url
	 */

	protected void sleep(int millis) {
		millis = millis + (int)(Math.random()*200);
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignore) {
		}
	}

}
