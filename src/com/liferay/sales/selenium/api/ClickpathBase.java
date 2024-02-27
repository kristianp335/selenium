package com.liferay.sales.selenium.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.liferay.sales.selenium.util.UTMGenerator;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Utility class to use as baseclass for clickpath implementations.
 * <p>
 * Every doXXX method sleeps for the default sleep time after execution
 *
 * @author Olaf Kock
 * @author Peter Richards
 */

public abstract class ClickpathBase {
    private static final ScrollIntoViewOptions DEFAULT_SCROLL_INTO_VIEW_OPTIONS = new ScrollIntoViewOptions(ScrollBehavior.SMOOTH, ScrollLogicalPosition.CENTER, ScrollLogicalPosition.NEAREST);
    protected final String baseUrl;
    protected final ObjectWriter objectWriter = new ObjectMapper().writer();
    private final DriverInitializer driverInitializer;
    protected int defaultSleep = 2000;
    protected UTMGenerator utmGenerator;
    private WebDriver driver;
    private JavascriptExecutor js;

    public ClickpathBase(final DriverInitializer di, final String baseUrl) {
        this(di, baseUrl, new UTMGenerator());
    }

    public ClickpathBase(final DriverInitializer di, final String baseUrl, final UTMGenerator utmGenerator) {
        this.driverInitializer = di;
        this.baseUrl = baseUrl;
        this.utmGenerator = utmGenerator;
    }

    private String buildFilename(String hint, FileType fileType) {
        final String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss", Locale.ENGLISH).format(LocalDateTime.now());
        return getClass().getSimpleName() + "-" + hint + "-Selenium-" + getDriver().getClass().getSimpleName() + "-" + timestamp + "." + fileType.getFileExtension();
    }

    /**
     * Deletes all cookies
     */
    protected void deleteAllCookies() {
        getDriver().manage().deleteAllCookies();
    }

    /**
     * Returns an initialized web driver
     *
     * @return the web driver
     */
    public WebDriver getDriver() {
        if (driver == null) {
            setDriver(driverInitializer.getDriver());
        }
        return driver;
    }

    /**
     * Sets the web driver to be used, including the JavaScript executor
     *
     * @param driver the web driver
     */
    protected void setDriver(WebDriver driver) {
        this.driver = driver;
        js = (JavascriptExecutor) driver;
    }

    protected void doClick(WebElement element) {
        log("INFO (doClick(element)): Clicking " + element.getText());
        element.click();
        sleep(defaultSleep);
    }

    /**
     * Syntactic sugar for System.out.println
     *
     * @param message the message text
     */
    protected void log(String message) {
        System.out.println(message);
    }

    /**
     * Sleep for at least the interval
     * The exact period could be extended by to allow for AC's interest calculation
     *
     * @param millis number of milliseconds
     */
    protected void sleep(int millis) {
        sleep(millis, true);
    }

    /**
     * Sleep for at least the interval
     * The exact period could be extended by to allow for AC's interest calculation,
     * if includeInterestDelay is set to true
     *
     * @param millis               number of milliseconds
     * @param includeInterestDelay include interest delay
     */
    protected void sleep(int millis, boolean includeInterestDelay) {
        millis = millis + (int) (Math.random() * 200);
        if (includeInterestDelay && Math.random() > 0.96) {
            millis += 15000;
            // Interest calculation on AC requires reading a particular page
            // for more than 10 seconds. See
            // https://liferay.slack.com/archives/C014BCDN8MU/p1695044060052069?thread_ts=1695042617.609449&cid=C014BCDN8MU
            log("Extending sleep/think/read time by 15sec for simulating interest");
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {
        }
    }

    /**
     * Click one of the links with given text, then sleep for default interval If
     * the exact link text is found, it is clicked. If no exact match is found, this
     * method tries to find a partial match.
     *
     * @param linkTexts the array of link texts to chose from
     */
    protected void doClickRandomText(String[] linkTexts) {
        doClickText(pickRandom(linkTexts));
    }

    /**
     * Click a link with one of the passed alternatives. Two-dimensional array is passed in for multilingual support
     * <p>
     * Note: Once a random element is chosen, the texts are tried in order. See doClickText(String[]) for semantics
     * <p>
     * Error handling: If the element is not found, an error is logged, otherwise
     * this condition is silently ignored.
     *
     * @param linkTexts the link texts in order to be tried.
     */

    protected void doClickRandomText(String[][] linkTexts) {
        doClickText(pickRandom(linkTexts));
    }

    /**
     * Click link with given text, then sleep for default interval If the exact link
     * text is found, it is clicked. If no exact match is found, this method tries
     * to find a partial match.
     *
     * @param linkText the link text
     */

    protected void doClickText(String linkText) {
        String match = "exact";
        List<WebElement> elements = getElementsByExactLinkText(linkText);
        if (elements.size() == 0) {
            match = "partial";
            elements = getDriver().findElements(By.partialLinkText(linkText));
        }

        if (elements.size() == 0) {
            log("WARN (doClickText): No match found on " + match + " matching for text to click: " + linkText);
            return;
        } else if (elements.size() > 1) {
            log("WARN (doClickText): found more than 1 occurrence on " + match + " matching of text to click, clicking first: " + linkText);
        } else {
            log("INFO (doClickText): Clicking " + match + " match " + linkText);
        }

        elements.get(0).click();
        sleep(defaultSleep);
    }

    /**
     * Click a link with one of the passed texts. Multiple texts are given for multi-language-support,
     * they'll be tried until a match is found.
     * <p>
     * Note: The texts are tried in order. First, all elements are checked for an exact match.
     * If no exact match was found for either of the parameters, a partial match is performed.
     * <p>
     * Error handling: If the element is not found, an error is logged, otherwise
     * this condition is silently ignored.
     *
     * @param texts Link texts in order to be tried.
     */
    protected void doClickText(String[] texts) {
        WebElement elementToClick = null;
        String text = null;

        // try exact match first
        for (final String s : texts) {
            text = s;
            List<WebElement> elements = getElementsByExactLinkText(text);
            if (elements.size() > 1) {
                log("WARN (doClickText[]): found more than 1 occurrence of text to click: " + text);
                elementToClick = elements.get(0);
                break;
            } else if (elements.size() == 1) {
                elementToClick = elements.get(0);
                break;
            }
        }

        // try partial match if necessary
        if (elementToClick == null) {
            for (final String s : texts) {
                text = s;
                List<WebElement> elements = getElementsByPartialLinkText(text);
                if (elements.size() > 1) {
                    log("WARN (doClickText[]): found more than 1 occurrence of (partial match) text to click: " + text);
                    elementToClick = elements.get(0);
                    break;
                } else if (elements.size() == 1) {
                    elementToClick = elements.get(0);
                    break;
                }
            }
        }

        if (elementToClick != null) {
            log("INFO (doClickText[]): Clicking " + text);
            elementToClick.click();
            sleep(defaultSleep);
        } else {
            log("ERROR (doClickText[]: No element found for " + Arrays.toString(texts) + " last try: " + text);
        }
    }

    /**
     * Navigate to URL, then sleep for default interval
     * This using the default of 3 tries with a 2-second pause between each
     *
     * @param url the url to navigate to
     */
    protected void doGoTo(final String url) {
        doGoTo(url, 3, 2000);
    }

    /**
     * Navigate to URL, then sleep for default interval
     *
     * @param url the url to navigate to
     * @param numberOfRetries the number of retires to attempt before reporting exception
     * @param millis the time in milliseconds to wait before retrying
     */
    protected void doGoTo(final String url, final int numberOfRetries, final int millis) {
        final String absoluteUrl;
        if (!url.contains("http")) {
            absoluteUrl = (baseUrl + url).replaceAll("//", "/");
        } else {
            absoluteUrl = url;
        }

        log("INFO (doGoTo): Navigating to " + absoluteUrl);

        // We want to report the connection exceptions, if they continue after the number of retries.
        boolean pass = false;
        WebDriverException lastException = null;
        int retryCount = numberOfRetries;

        do {
            try {
                getDriver().get(absoluteUrl);
                pass = true;
            } catch (WebDriverException e) {
                if (e.getMessage().contains("ERR_NAME_NOT_RESOLVED")) {
                    lastException = e;
                    log("INFO (goGoTo): Navigating to " + absoluteUrl + " [retries remaining : " + retryCount + "]");
                } else {
                    throw e;
                }
            }
            sleep(millis, false);
        } while (!pass && retryCount-- > 0);

        if (!pass && retryCount == 0 && lastException != null) {
            throw lastException;
        } else {
            log("INFO (doGoTo): Navigated to " + absoluteUrl + " after " + ((numberOfRetries - retryCount) + 1) + " attempts");
        }

        sleep(defaultSleep);
        List<WebElement> dntAlerts = getElementsByCSS(".dnt-alert");
        if (dntAlerts.size() > 0) {
            log("WARNING: Found DNT Detection - AC might refuse to register stats!");
            this.writePageToDisk("WARNING", "dnt-found");
        } else {
            log("INFO (doGoTo): No DNT alert found");
        }
    }

    protected void doLogin(String username, String password) {
        doGoTo(baseUrl + "welcome?p_p_id=com_liferay_login_web_portlet_LoginPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_login_web_portlet_LoginPortlet_mvcRenderCommandName=%2Flogin%2Flogin&saveLastPath=false");

        sleep(1500);

        try {
            // sometimes this fails for unknown reasons. Try again...
            getLoginField("login").sendKeys(Keys.chord(Keys.CONTROL, "a"));
        } catch (NoSuchElementException e) {
            log("INFO: Retry navigate to log in page!");
            doGoTo(baseUrl + "welcome?p_p_id=com_liferay_login_web_portlet_LoginPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_login_web_portlet_LoginPortlet_mvcRenderCommandName=%2Flogin%2Flogin&saveLastPath=false");
            sleep(3000);

            getLoginField("login").sendKeys(Keys.chord(Keys.CONTROL, "a"));
        }
        getLoginField("login").sendKeys(Keys.DELETE);
        getLoginField("login").sendKeys(username);
        sleep(500);

        getLoginField("password").sendKeys(password);
        sleep(500);

        log("INFO (doLogin): Logging in as " + username);

        getLoginField("login").sendKeys(Keys.ENTER);
        sleep(8000);
    }

    /**
     * Returns the first item in a list
     *
     * @param values the list of values
     * @param <T>    the type within the list
     * @return the first item, or null, if the list is null or empty
     */
    protected <T> T firstOf(List<T> values) {
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    /**
     * Returns a web element based on a CSS selector.
     * If more than one element matches the CSS selector then the first one will be returned.
     *
     * @param cssSelector the CSS selector
     * @return the web element, or null if not found
     */
    protected WebElement getElementByCSS(String cssSelector) {
        List<WebElement> allElements = getElementsByCSS(cssSelector);
        if (allElements.size() == 0) {
            log("WARN: getElementByCSS - there is no element matching this CSS selector: " + cssSelector);
            return null;
        } else if (allElements.size() > 1) {
            log("WARN: getElementByCSS - there are multiple elements matching this CSS selector: " + cssSelector);
        }
        return getDriver().findElement(By.cssSelector(cssSelector));
    }

    /**
     * Returns an anchor element based on its link text.
     * If more than one anchor matches the link text then the first one will be returned.
     *
     * @param linkText the link text
     * @return the web element, or null if not found
     */
    protected WebElement getElementByExactLinkText(String linkText) {
        List<WebElement> allElements = getElementsByExactLinkText(linkText);
        if (allElements.size() == 0) {
            log("WARN: getElementByExactLinkText - there is no element matching this link text: " + linkText);
            return null;
        } else if (allElements.size() > 1) {
            log("WARN: getElementByExactLinkText - there are multiple elements matching this link text: " + linkText);
        }
        return allElements.get(0);
    }

    /**
     * Uses the driver to returns a list of web elements which match the link text
     *
     * @param linkText the link text
     * @return the List of web elements
     */
    protected List<WebElement> getElementsByExactLinkText(String linkText) {
        return getDriver().findElements(By.linkText(linkText));
    }

    /**
     * Returns a web element based on its id attribute.
     *
     * @param id the id of the element
     * @return the web element, or null if not found
     */
    protected WebElement getElementById(String id) {
        try {
            return getDriver().findElement(By.id(id));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Returns a web element based on its name attribute.
     * If more than one web element matches the link text then the first one will be returned.
     *
     * @param name the name of the element
     * @return the web element, or null if not found
     */
    protected WebElement getElementByName(String name) {
        List<WebElement> allElements = getElementsByName(name);
        if (allElements.size() == 0) {
            log("WARN: getElementByName - there is no element matching this name: " + name);
            return null;
        } else if (allElements.size() > 1) {
            log("WARN: getElementByName - there are multiple elements matching this name: " + name);
        }
        return allElements.get(0);
    }

    /**
     * Uses the driver to returns a list of web elements using the name attribute
     *
     * @param name the name
     * @return the List of web elements
     */
    protected List<WebElement> getElementsByName(String name) {
        return driver.findElements(By.name(name));
    }

    /**
     * Returns an anchor element based on a partial match of its link text.
     * If more than one anchor matches the link text then the first one will be returned.
     *
     * @param linkText the link text
     * @return the web element, or null if not found
     */
    protected WebElement getElementByPartialLinkText(String linkText) {
        List<WebElement> allElements = getElementsByPartialLinkText(linkText);
        if (allElements.size() == 0) {
            log("WARN: getElementByPartialLinkText - there is no element matching this link text: " + linkText);
            return null;
        } else if (allElements.size() > 1) {
            log("WARN: getElementByPartialLinkText - there are multiple elements matching this link text: " + linkText);
        }
        return allElements.get(0);
    }

    /**
     * Uses the driver to returns a list of web elements using a partial match on the link text
     *
     * @param linkText the link text
     * @return the List of web elements
     */
    protected List<WebElement> getElementsByPartialLinkText(String linkText) {
        return getDriver().findElements(By.partialLinkText(linkText));
    }

    /**
     * Returns a web element using the xpath parameter.
     * If more than one anchor matches the link text then the first one will be returned.
     *
     * @param xPath the xpath for the element
     * @return the web element, or null if not found
     */
    protected WebElement getElementByXPath(String xPath) {
        List<WebElement> allElements = getElementsByXPath(xPath);
        if (allElements.size() > 1) {
            log("WARN: getElementByXPath - there are multiple elements matching this xpath: " + xPath);
        } else if (allElements.isEmpty()) {
            log("WARN: getElementByXPath - there is no element matching this xpath: " + xPath);
            return null;
        }
        return allElements.get(0);
    }

    /**
     * Uses the driver to returns a list of web elements using the xpath expression
     *
     * @param xPath the xpath expression
     * @return the List of web elements
     */
    protected List<WebElement> getElementsByXPath(String xPath) {
        return getDriver().findElements(By.xpath(xPath));
    }

    /**
     * Uses the driver to return a list of web elements which match the CSS selector
     *
     * @param cssSelector the CSS selector
     * @return the List of web elements
     */
    protected List<WebElement> getElementsByCSS(String cssSelector) {
        return getDriver().findElements(By.cssSelector(cssSelector));
    }

    /**
     * Returns the first visible web element which matches the xpath expression
     *
     * @param xPath the xpath expression
     * @return the web element
     */
    protected WebElement getFirstVisibleElementByXPath(String xPath) {
        List<WebElement> elements = getDriver().findElements(By.xpath(xPath));
        for (WebElement webElement : elements) {
            if (webElement.isDisplayed()) {
                return webElement;
            }
        }
        return null;
    }

    /**
     * Returns a Liferay login form field based on its name
     *
     * @param field the name of the field
     * @return the web element
     */
    protected WebElement getLoginField(String field) {
        return getDriver().findElement(By.id("_com_liferay_login_web_portlet_LoginPortlet_" + field));
    }

    /**
     * Returns the last item in a list
     *
     * @param values the list of values
     * @param <T>    the type within the list
     * @return the last item, or null, if the list is null or empty
     */
    protected <T> T lastOf(List<T> values) {
        return values == null || values.isEmpty() ? null : values.get(values.size() - 1);
    }

    /**
     * Mark elements matching the selector with background-color:red. Note: To ease
     * the multiple possible escaping traps, selectors MUST NOT contain
     * double-quotes! Use single quotes instead!
     *
     * @param selector the CSS selector
     * @throws IllegalArgumentException when the selector contains double quotes
     */
    protected void mark(String selector) throws IllegalArgumentException {
        mark(selector, MarkMethod.BACKGROUND);
    }

    /**
     * Mark elements matching the selector with the specified method.
     * <p>
     * Note: To ease the multiple possible escaping traps, selectors MUST NOT contain
     * double-quotes! Use single quotes instead!
     *
     * @param selector   the CSS selector
     * @param markMethod the method used to mark the element(s) identified by the selector
     * @throws IllegalArgumentException when the selector contains double quotes
     */
    protected void mark(String selector, MarkMethod markMethod) throws IllegalArgumentException {
        if (selector.contains("\"")) {
            throw new IllegalArgumentException("Selector must not contain double quotes! Rewrite with single quotes! The author of the underlying method was too lazy to deal with all possible escaping options, so he just deals with it this way: You do the work!");
        }
        final String styleString = markMethod.getStyleString();
        execute("document.querySelectorAll(\"" + selector + String.format("\").forEach(function(e){e.style.%s;})", styleString));
    }

    /**
     * Executes javascript within the current page
     *
     * @param javascript the javascript to be executed
     */
    protected void execute(String javascript) {
        js.executeScript(javascript);
    }

    /**
     * Mark the given element with border: 2px solid red
     *
     * @param element the element
     */
    protected void mark(final WebElement element) {
        mark(element, MarkMethod.BORDER);
    }

    /**
     * Mark the given element using the sepcified method
     *
     * @param element    the element
     * @param markMethod the method used to mark the element(s) identified by the selector
     */
    protected void mark(final WebElement element, final MarkMethod markMethod) {
        final String styleString = markMethod.getStyleString();
        js.executeScript(String.format("arguments[0].style.%s;", styleString), element);
    }

    /**
     * Mark elements matching the xpath with background-color:red.
     *
     * @param xPath the xPath expression
     */
    protected void markByXPath(final String xPath) {
        markByXPath(xPath, MarkMethod.BACKGROUND);
    }

    /**
     * Mark elements matching the xpath using the specified method
     *
     * @param xPath      the xPath expression
     * @param markMethod the method used to mark the element(s) identified by the selector
     */
    protected void markByXPath(final String xPath, final MarkMethod markMethod) {
        StringBuilder js = new StringBuilder("const iterator = document.evaluate(");
        if (xPath.contains("\"")) {
            js.append("'");
            js.append(xPath);
            js.append("'");
        } else {
            js.append("\"");
            js.append(xPath);
            js.append("\"");
        }
        String styleString = markMethod.getStyleString();
        js.append(",document,null,XPathResult.UNORDERED_NODE_ITERATOR_TYPE,null);");
        js.append("try { let thisNode = iterator.iterateNext();");
        js.append(String.format("while (thisNode) { thisNode.style.%s; thisNode = iterator.iterateNext(); }", styleString));
        js.append("} catch (e) { console.error(`Error: Document tree modified during iteration ${e}`); }");
        execute(js.toString());
    }

    /**
     * Convert the parameters to an array, just as syntactic sugar, because
     * constructing T[] is otherwise ugly. This makes calls like
     * doClick(oneOf("this", "or this")) more descriptive
     *
     * @param values the values to be included in the array
     * @return the array
     */
    @SafeVarargs
    protected final <T> T[] oneOf(T... values) {
        return values;
    }

    /**
     * Picks and returns a random value from an array
     *
     * @param values the values from which to pick the random value
     * @return the value, or null, if the array was empty
     */
    protected <T> T pickRandom(T[] values) {
        if (values.length == 0) {
            return null;
        }
        int pos = (int) (Math.random() * values.length);
        return values[pos];
    }

    /**
     * Picks and returns a random value from a list
     *
     * @param values the values from which to pick the random value
     * @return the value, or null, if the array was empty
     */
    protected <T> T pickRandom(List<T> values) {
        if (values.isEmpty()) {
            return null;
        }
        int pos = (int) (Math.random() * values.size());
        return values.get(pos);
    }

    /**
     * Picks and returns a random array from a two-dimensional array
     *
     * @param values the two-dimensional array from which to pick the random array
     * @return the value, or null, if the array was empty
     */
    protected <T> T[] pickRandom(T[][] values) {
        if (values.length == 0) {
            return null;
        }
        int pos = (int) (Math.random() * values.length);
        return values[pos];
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

    /**
     * Resize the browser window
     *
     * @param width  the desired width
     * @param height the desired height
     */
    protected void resizeBrowser(int width, int height) {
        getDriver().manage().window().setSize(new Dimension(width, height));
    }

    public abstract void run(String username, String password);

    /**
     * Scroll the page so a specific element is within view
     *
     * @param element the element to scroll to
     */
    protected void scrollTo(WebElement element) {
        scrollTo(element, DEFAULT_SCROLL_INTO_VIEW_OPTIONS);
    }

    /**
     * Scroll the page so a specific element is within view
     *
     * @param element the element to scroll to
     */
    protected void scrollTo(WebElement element, ScrollIntoViewOptions scrollIntoViewOptions) {
        try {
            js.executeScript(String.format("arguments[0].scrollIntoView(%s);", objectWriter.writeValueAsString(scrollIntoViewOptions)), element);
        } catch (JsonProcessingException e) {
            log("WARN: scrollTo - failed to scroll into view for: " + element);
        }
    }

    /**
     * Scroll the page so a specific element is within view
     *
     * @param element the element to scroll to
     */
    protected void scrollTo(WebElement element, boolean alignToTop) {
        js.executeScript(String.format("arguments[0].scrollIntoView(%s);", alignToTop), element);
    }

    /**
     * Sets the default sleep
     *
     * @param millis number of milliseconds
     */
    public void setDefaultSleep(int millis) {
        this.defaultSleep = millis;
    }

    protected void takeScreenshot(String severity, String hint) {
        takeScreenshot(severity, hint, true);
    }

    protected void takeScreenshot(String severity, final String hint, boolean fullscreen) {
        final File destFile = new File(buildFilename(hint, FileType.SCREENSHOT));
        log(severity + ": Writing " + destFile.getAbsolutePath());
        try (OutputStream stream = new FileOutputStream(destFile)) {
            final String imageBase64 = takeScreenshot(fullscreen);
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            stream.write(imageBytes);

        } catch (IOException e) {
            log("ERROR - unable to take screenshot");
            e.printStackTrace();
        }
    }

    private String takeScreenshot(boolean fullscreen) {
        if (getDriver() instanceof ChromeDriver) {
            final ChromeDriver driver = (ChromeDriver) getDriver();
            if (fullscreen) {
                Map<String, Object> layoutMetrics = driver.executeCdpCommand("Page.getLayoutMetrics", Collections.emptyMap());
                Map<String, Object> screenshotConfig = Maps.newHashMap();
                screenshotConfig.put("captureBeyondViewport", true);
                screenshotConfig.put("fromSurface", true);
                Map contentSize = (Map) layoutMetrics.get("contentSize");
                screenshotConfig.put("clip", ImmutableMap.of(
                        "width", contentSize.get("width"),
                        "height", contentSize.get("height"),
                        "x", 0,
                        "y", 0,
                        "scale", 1
                ));
                Map<String, Object> base64PngResult = driver.executeCdpCommand("Page.captureScreenshot", screenshotConfig);
                return (String) base64PngResult.get("data");
            }
            return driver.getScreenshotAs(OutputType.BASE64);
        } else if (getDriver() instanceof FirefoxDriver) {
            final FirefoxDriver driver = (FirefoxDriver) getDriver();
            if (fullscreen) {
                return driver.getFullPageScreenshotAs(OutputType.BASE64);
            }
            return driver.getScreenshotAs(OutputType.BASE64);
        }
        {
            throw new UnsupportedOperationException("This method does not yet support " + getDriver().getClass().getSimpleName());
        }
    }

    /**
     * Sends the text to the web element
     *
     * @param webElement the web element
     * @param text       the text to send to the web element
     */
    protected void type(WebElement webElement, String text) {
        webElement.sendKeys(text);
        sleep(500);
    }

    /**
     * Writes the page source to disk
     *
     * @param severity the severity
     * @param hint     the hint text
     */
    public void writePageToDisk(String severity, String hint) {
        File outFile = new File(buildFilename(hint, FileType.SOURCE));
        try (FileWriter out = new FileWriter(outFile)) {
            log(severity + ": Writing " + outFile.getAbsolutePath());
            out.write(getDriver().getPageSource());
            out.flush();
        } catch (IOException e) {
            log("ERROR - unable to write page source to disk");
            e.printStackTrace();
        }
    }

    public enum FileType {
        SOURCE("html"),
        SCREENSHOT("png");

        private final String fileExtension;

        FileType(final String fileExtension) {
            this.fileExtension = fileExtension;
        }

        public String getFileExtension() {
            return fileExtension;
        }
    }
}
