package com.liferay.sales.selenium.api;

import java.util.Arrays;
import java.util.List;

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
		
	public void deleteAllCookies() {
		getDriver().manage().deleteAllCookies();
	}

	/**
	 * Navigate to URL, then sleep for default interval
	 * 
	 * @param url
	 */

	public void doGoTo(String url) {
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

	public void doClickText(String text) {
		String match = "exact";
		List<WebElement> elements = getElementsByExactLinkText(text);
		if (elements.size() == 0) {
			match = "partial";
			elements = getDriver().findElements(By.partialLinkText(text));
		}  

		if(elements.size() == 0) {
			log("WARN (doClickText): No match found on " + match + " matching for text to click: " + text);
			return;
		} else if(elements.size() > 1) {
			log("WARN (doClickText): found more than 1 occurrence on " + match + " matching of text to click, clicking first: " + text);
		}
		
		elements.get(0).click();
		sleep(defaultSleep);
	}

	/**
	 * Click a link with one of the passed texts. Multiple texts are given for multi-language-support,
	 * they'll be tried until a match is found.
	 * 
	 * Note: The texts are tried in order. First, all elements are checked for an exact match. 
	 * If no exact match was found for either of the parameters, a partial match is performed. 
	 * 
	 * Error handling: If the element is not found, an error is logged, otherwise
	 * this condition is silently ignored.
	 * 
	 * @param texts Link texts in order to be tried.
	 */
	public void doClickText(String[] texts) {
		WebElement elementToClick = null;
		String text = null;
		
		// try exact match first
		for(int i = 0; i < texts.length; i++) {
			text = texts[i];
			List<WebElement> elements = getElementsByExactLinkText(text);
			if(elements.size() > 1) {
				log("WARN (doClickText[]): found more than 1 occurrence of text to click: " + text);
				elementToClick = elements.get(0);
				break;
			} else if (elements.size() == 1) {
				elementToClick = elements.get(0);
				break;
			}
		}
		
		// try partial match if necessary
		if(elementToClick == null) {
			for(int i = 0; i < texts.length; i++) {
				text = texts[i];
				List<WebElement> elements = getElementsByPartialLinkText(text);
				if(elements.size() > 1) {
					log("WARN (doClickText[]): found more than 1 occurrence of (partial match) text to click: " + text);
					elementToClick = elements.get(0);
					break;
				} else if (elements.size() == 1) {
					elementToClick = elements.get(0);
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

	public void doClickRandomText(String[] strings) {
		doClickText(pickRandom(strings));
	}

	/**
	 * Click a link with one of the passed alternatives. Two dimensional array is passed in for multi-lingual support
	 * 
	 * Note: Once a random element is chosen, the texts are tried in order. See doClickText(String[]) for semantics
	 * 
	 * Error handling: If the element is not found, an error is logged, otherwise
	 * this condition is silently ignored.
	 * 
	 * @param texts Link texts in order to be tried.
	 */
	
	public void doClickRandomText(String[][] strings) {
		doClickText(pickRandom(strings));
	}

	/**
	 * Resize the browser window
	 * @param width
	 * @param height
	 */
	
	public void doResize(int width, int height) {
		getDriver().manage().window().setSize(new Dimension(width, height));
	}

	public void execute(String javascript) {
		js.executeScript(javascript);
	}

	public WebElement getElementByCSS(String cssSelector) {
		List<WebElement> allElements = getElementsByCSS(cssSelector);
		if(allElements.size() > 1) {
			log("WARN: getElementByCSS - there are multiple elements matching this CSS selector: " + cssSelector);
		} else {
			log("WARN: getElementByCSS - there is no element matching this CSS selector: " + cssSelector);
		}
		return getDriver().findElement(By.cssSelector(cssSelector));
	}

	public List<WebElement> getElementsByCSS(String cssSelector) {
		return getDriver().findElements(By.cssSelector(cssSelector));
	}

	public WebElement getElementById(String id) {
		return getDriver().findElement(By.id(id));
	}

	public WebElement getElementByExactLinkText(String linkText) {
		List<WebElement> allElements = getElementsByExactLinkText(linkText);
		if(allElements.size() > 1) {
			log("WARN: getElementByExactLinkText - there are multiple elements matching this link text: " + linkText);
			return allElements.get(0); 
		} else {
			log("WARN: getElementByExactLinkText - there is no element matching this link text: " + linkText);
		}
		return null;
	}

	public List<WebElement> getElementsByExactLinkText(String linkText) {
		return getDriver().findElements(By.linkText(linkText));
	}

	public WebElement getElementByName(String name) {
		List<WebElement> allElements = getElementsByName(name);
		if(allElements.size() > 1) {
			log("WARN: getElementByName - there are multiple elements matching this name: " + name);
		} else {
			log("WARN: getElementByName - there is no element matching this name: " + name);
			return null;
		}
		return allElements.get(0);
	}

	public List<WebElement> getElementsByName(String name) {
		return driver.findElements(By.name(name));
	}

	public WebElement getElementByPartialLinkText(String linkText) {
		List<WebElement> allElements = getElementsByPartialLinkText(linkText);
		if(allElements.size() == 0) {
			log("WARN: getElementByPartialLinkText - there is no element matching this link text: " + linkText);
			return null;
		} 
		
		if(allElements.size() > 1) {
			log("WARN: getElementByPartialLinkText - there are multiple elements matching this link text: " + linkText);
		} 
		return allElements.get(0); 
	}

	public List<WebElement> getElementsByPartialLinkText(String linkText) {
		return getDriver().findElements(By.partialLinkText(linkText));
	}

	public WebElement getElementByXPath(String xPath) {
		List<WebElement> allElements = getElementsByXPath(xPath);
		if(allElements.size() > 1) {
			log("WARN: getElementByXPath - there are multiple elements matching this xpath: " + xPath);
		} else if(allElements.isEmpty()) {
			log("WARN: getElementByXPath - there is no element matching this xpath: " + xPath);
			return null;
		}
		return allElements.get(0);
	}

	public List<WebElement> getElementsByXPath(String xPath) {
		return getDriver().findElements(By.xpath(xPath));
	}

	public WebElement getFirstVisibleElementByXPath(String xPath) {
		List<WebElement> elements = getDriver().findElements(By.xpath(xPath));
		for (WebElement webElement : elements) {
			if (webElement.isDisplayed()) {
				return webElement;
			}
		}
		return null;
	}

	public WebElement getLoginField(String field) {
		return getDriver().findElement(By.id("_com_liferay_login_web_portlet_LoginPortlet_" + field));
	}

	public String pickRandom(String[] strings) {
		int pos = (int) (Math.random() * strings.length);
		return strings[pos];
	}

	public String[] pickRandom(String[][] strings) {
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

	public void scrollTo(WebElement element) {
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

	public WebDriver getDriver() {
		if (driver == null) {
			setDriver(driverInitializer.getDriver());
		}
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
		js = (JavascriptExecutor) driver;
	}
	
	public void log(String message) {
		System.out.println(message);
	}

	/**
	 * convert parameters to String[], just as syntactic sugar, because 
	 * constructing String[] is otherwise ugly. This makes calls like
	 * doClick(oneOf("this", "or this")) more descriptive
	 * @param text
	 * @return
	 */
	public String[] oneOf(String... text) {
		return text;
	}
}
