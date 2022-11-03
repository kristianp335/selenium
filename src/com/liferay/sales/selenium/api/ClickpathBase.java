package com.liferay.sales.selenium.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Utility class to use as baseclass for clickpath implementations.
 * 
 * Every doXXX method sleeps for the default sleep time after execution
 * 
 * @author Olaf Kock
 */

public abstract class ClickpathBase {

	private WebDriver driver = null;
	private JavascriptExecutor js = null;
	protected String baseUrl;
	private int defaultSleep = 2000;
	private DriverInitializer driverInitializer;

	public ClickpathBase(DriverInitializer di, String baseUrl) {
		this.driverInitializer = di;
		this.baseUrl = baseUrl;
	}

	abstract public void run(String username, String password);
		
	protected void deleteAllCookies() {
		getDriver().manage().deleteAllCookies();
	}

	/**
	 * Navigate to URL, then sleep for default interval
	 * 
	 * @param url
	 */

	protected void doGoTo(String url) {
		getDriver().get(url);
		sleep(defaultSleep);
	}

	/**
	 * Click link with given text, then sleep for default interval If the exact link
	 * text is found, it is clicked. If no exact match is found, this method tries
	 * to find a partial match.
	 * 
	 * @param url
	 */

	protected void doClickText(String text) {
		List<WebElement> elements = getElementsByLinkText(text);
		if (elements.size() == 0) {
			log("INFO (doClickText): No match found for text to click, using partial match: " + text);
			getDriver().findElement(By.partialLinkText(text)).click();
		} else {
			if(elements.size() > 1) {
				log("WARN (doClickText): found more than 1 occurrence of text to click, clicking first: " + text);
			}
			elements.get(0).click();
		}
		sleep(defaultSleep);
	}

	protected void doClickText(String[] texts) {
		WebElement elementToClick = null;
		String text = null;
		for (int i = 0; i < texts.length; i++) {
			text = texts[i];
			List<WebElement> elements = getElementsByLinkText(text);
			if(elements.size() > 1) {
				log("WARN (doClickText[]): found more than 1 occurrence of text to click: " + text);
				elementToClick = elements.get(0);
				break;
			} else if (elements.size() == 1) {
				elementToClick = elements.get(0);
				break;
			}
			else if (elements.size() == 0) {
				List<WebElement> partialMatches = getDriver().findElements(By.partialLinkText(text));
				if(partialMatches.size() > 0) {
					log("INFO (doClickText[]): Found " + partialMatches.size() + " partial match(es) for text to click: " + text);
					elementToClick = partialMatches.get(0);
					break;
				}
			}
		}
		if(elementToClick != null) {
			log("INFO (doClickText[]): Clicking " + text);
			elementToClick.click();
			sleep(defaultSleep);
		} else {
			log("ERROR (doClickText[]: No element found for " + Arrays.toString(texts) + " last try: " + text);
		}
	}

	/**
	 * Click one of the links with given text, then sleep for default interval If
	 * the exact link text is found, it is clicked. If no exact match is found, this
	 * method tries to find a partial match.
	 * 
	 * @param url
	 */

	protected void doClickRandomText(String[] strings) {
		doClickText(getOneOf(strings));
	}

	protected void doClickRandomText(String[][] strings) {
		doClickText(getOneOf(strings));
	}

	protected void doResize(int width, int height) {
		getDriver().manage().window().setSize(new Dimension(width, height));
	}

	protected void execute(String javascript) {
		js.executeScript(javascript);
	}

	protected WebElement getElementByCSS(String cssSelector) {
		List<WebElement> allElements = getElementsByCSS(cssSelector);
		if(allElements.size() > 1) {
			log("WARN: getElementByCSS - there are multiple elements matching this CSS selector: " + cssSelector);
		} else {
			log("WARN: getElementByCSS - there is no element matching this CSS selector: " + cssSelector);
		}
		return getDriver().findElement(By.cssSelector(cssSelector));
	}

	protected List<WebElement> getElementsByCSS(String cssSelector) {
		return getDriver().findElements(By.cssSelector(cssSelector));
	}

	protected WebElement getElementById(String id) {
		return getDriver().findElement(By.id(id));
	}

	protected WebElement getElementByLinkText(String linkText) {
		List<WebElement> allElements = getElementsByLinkText(linkText);
		if(allElements.size() > 1) {
			log("WARN: getElementByLinkText - there are multiple elements matching this link text: " + linkText);
			return allElements.get(0); 
		} else {
			log("WARN: getElementByLinkText - there is no element matching this link text: " + linkText);
		}
		return null;
	}

	protected List<WebElement> getElementsByLinkText(String linkText) {
		return getDriver().findElements(By.linkText(linkText));
	}

	protected WebElement getElementByName(String name) {
		List<WebElement> allElements = getElementsByName(name);
		if(allElements.size() > 1) {
			log("WARN: getElementByName - there are multiple elements matching this name: " + name);
		} else {
			log("WARN: getElementByName - there is no element matching this name: " + name);
		}
		return driver.findElement(By.name(name));
	}

	protected List<WebElement> getElementsByName(String name) {
		return driver.findElements(By.name(name));
	}

	protected WebElement getElementByPartialLinkText(String linkText) {
		List<WebElement> allElements = getElementsByPartialLinkText(linkText);
		if(allElements.size() > 1) {
			log("WARN: getElementByPartialLinkText - there are multiple elements matching this link text: " + linkText);
			return allElements.get(0); 
		} else {
			log("WARN: getElementByPartialLinkText - there is no element matching this link text: " + linkText);
		}
		return null;
	}

	protected List<WebElement> getElementsByPartialLinkText(String linkText) {
		return getDriver().findElements(By.partialLinkText(linkText));
	}

	protected WebElement getElementByXPath(String xPath) {
		List<WebElement> allElements = getElementsByXPath(xPath);
		if(allElements.size() > 1) {
			log("WARN: getElementByXPath - there are multiple elements matching this xpath: " + xPath);
		} else if(allElements.isEmpty()) {
			log("WARN: getElementByXPath - there is no element matching this xpath: " + xPath);
			return null;
		}
		return allElements.get(0);
	}

	protected List<WebElement> getElementsByXPath(String xPath) {
		return getDriver().findElements(By.xpath(xPath));
	}

	protected WebElement getFirstVisibleElementByXPath(String xPath) {
		List<WebElement> elements = getDriver().findElements(By.xpath(xPath));
		for (WebElement webElement : elements) {
			if (webElement.isDisplayed()) {
				return webElement;
			}
		}
		return null;
	}

	protected WebElement getLoginField(String field) {
		return getDriver().findElement(By.id("_com_liferay_login_web_portlet_LoginPortlet_" + field));
	}

	protected String getOneOf(String[] strings) {
		int pos = (int) (Math.random() * strings.length);
		return strings[pos];
	}

	protected String[] getOneOf(String[][] strings) {
		int pos = (int) (Math.random() * strings.length);
		return strings[pos];
	}

	/**
	 * Mark elements matching the selector with background-color:red. Note: To ease
	 * the multiple possible escaping traps, selectors MUST NOT contain
	 * double-quotes! Use single quotes instead!
	 * 
	 * @throws IllegalArgumentException when the selector contains double quotes
	 * @param selector
	 */
	public void mark(String selector) throws IllegalArgumentException {
		if (selector.contains("\"")) {
			throw new IllegalArgumentException(
					"Selector must not contain double quotes! Rewrite with single quotes! The author of the underlying method was too lazy to deal with all possible escaping options, so he just deals with it this way: You do the work!");
		}
		execute("document.querySelectorAll(\"" + selector
				+ "\").forEach(function(e){e.style.backgroundColor = 'red';})");
	}

	/**
	 * quit a session (closes browser)
	 */

	public void quit() {
		if (driver != null) {
			driver.quit();
		}
		driver = null;
		js = null;
	}

	protected void scrollTo(WebElement element) {
		js.executeScript("arguments[0].scrollIntoView(true);", element);
	}
	
	public void setDefaultSleep(int millis) {
		this.defaultSleep = millis;
	}

	/**
	 * sleep for given interval
	 * 
	 * @param url
	 */

	public void sleep(int millis) {
		millis = millis + (int) (Math.random() * 200);
		try {
			Thread.sleep(millis);
		} catch (InterruptedException ignore) {
		}
	}

	protected WebDriver getDriver() {
		if (driver == null) {
			setDriver(driverInitializer.getDriver());
		}
		return driver;
	}

	private void setDriver(WebDriver driver) {
		this.driver = driver;
		js = (JavascriptExecutor) driver;
	}
	
	protected void log(String message) {
		System.err.println(message);
	}

	protected String[] oneOf(String... text) {
		return text;
	}

}
