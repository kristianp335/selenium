package com.liferay.sales.selenium;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ClickpathBase {

	private WebDriver driver = null;
	private JavascriptExecutor js = null;
	protected String baseUrl;
	
	int defaultSleep = 2000;

	private DriverInitializer driverInitializer;
	
	public ClickpathBase(DriverInitializer di, String baseUrl) {
		this.driverInitializer = di;
		this.baseUrl = baseUrl;
	}
	
	protected void deleteAllCookies() {
		getDriver().manage().deleteAllCookies();
	}
	
	/**
	 * Navigate to URL, then sleep for default interval
	 * @param url
	 */
	
	protected void doGoTo(String url) {
	    getDriver().get(url);
	    sleep(defaultSleep);
	}

	/**
	 * Click link with given text, then sleep for default interval
	 * @param url
	 */

	protected void doClickText(String text) {
		getDriver().findElement(By.linkText(text)).click();
		sleep(defaultSleep);
	}

	/**
	 * Click one of the links with given text, then sleep for default interval
	 * @param url
	 */

	protected void doClickRandomText(String[] text) {
		int pos = (int) (Math.random()*text.length);
		getDriver().findElement(By.linkText(text[pos])).click();
		sleep(defaultSleep);
	}

	protected void doResize(int width, int height) {
		getDriver().manage().window().setSize(new Dimension(width, height));
	}
	
	protected WebElement getFieldByCSS(String cssSelector) {
		return getDriver().findElement(By.cssSelector(cssSelector));
	}
	
	protected WebElement getFieldByName(String name) {
		return driver.findElement(By.name(name));
	}
	
	protected WebElement getFieldByXPath(String xPath) {
		return getDriver().findElement(By.xpath(xPath));
	}
	
	
	

	protected WebElement getLoginField(String field) {
		return getDriver().findElement(By.id("_com_liferay_login_web_portlet_LoginPortlet_" + field));
	}

	
	/**
	 * quit a session (closes browser), optionally keep this instance working 
	 * by immediately creating an identical one again.
	 * @param reopen set to true when this instance should still have a valid driver after quitting
	 */
	
	protected void quit(boolean reopen) {
		driver.quit();
		driver = null;
		js = null;
	}
	
	protected void setDefaultSleep(int millis) {
		this.defaultSleep = millis;
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

	private WebDriver getDriver() {
		if(driver==null) {
			setDriver(driverInitializer.getDriver());
		}
		return driver;
	}

	private void setDriver(WebDriver driver) {
		this.driver = driver;
		js = (JavascriptExecutor) driver;
	}

}
