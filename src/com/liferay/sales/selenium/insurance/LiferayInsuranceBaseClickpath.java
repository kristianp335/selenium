package com.liferay.sales.selenium.insurance;

import com.liferay.sales.selenium.api.ClickpathBase;
import com.liferay.sales.selenium.api.DriverInitializer;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

public abstract class LiferayInsuranceBaseClickpath extends ClickpathBase {

	public LiferayInsuranceBaseClickpath(DriverInitializer di, String baseUrl) {
		super(di, baseUrl);
	}

	protected void doLogin(String username, String password) {
		doGoTo(baseUrl);
		sleep(1500);
		doClickText("Sign In");
		sleep(1500);

//		try {
//			// sometimes this fails for unknown reasons. Try again...
//			getLoginField("login").sendKeys(Keys.chord(Keys.CONTROL, "a"));;
//		} catch (NoSuchElementException e) {
//			log("INFO: Retry navigate to log in page!");
//			doGoTo(baseUrl + "welcome?p_p_id=com_liferay_login_web_portlet_LoginPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_login_web_portlet_LoginPortlet_mvcRenderCommandName=%2Flogin%2Flogin&saveLastPath=false");
//			sleep(3000);
//
			getLoginField("login").sendKeys(Keys.chord(Keys.CONTROL, "a"));;
//		}
		getLoginField("login").sendKeys(Keys.DELETE);
		getLoginField("login").sendKeys(username);
		sleep(500);

		getLoginField("password").sendKeys(password); 
		sleep(500);
		
		log("INFO (doLogin): Logging in as "+ username);
		
		getLoginField("login").sendKeys(Keys.ENTER);
		sleep(8000);
	}

	/**
	 * Mouseover trickery to navigate to menu items
	 * (too lazy to make it more elegant with appropriate xpath - at least for now)
	 * @param level1
	 * @param level2
	 */
	public void navigateTo(String level1, String level2) {
		WebDriver driver = getDriver();
		Actions builder = new Actions(driver);
//		WebElement element = driver.findElement(By.linkText(level1));
		WebElement l1element = driver.findElement(By.xpath("//div[@id='navbarmain']//a[text()[contains(., '"+level1+"')]]"));
		builder.moveToElement(l1element).build().perform();
		sleep(1000);
		if(level2 == null) {
			l1element.click();
		} else {
			WebElement l2element = driver.findElement(By.xpath("//div[@id='navbarmain']/ul/li/ul/li/a[text()[contains(., '"+level2+"')]]"));
			l2element.click();
			//doClick(l2element);
		}
		sleep(defaultSleep);
	}
	
	public void selectLanguage(String language) {
		WebElement languageSelector = getElementByXPath("//select[@class='languageSelectorDropDown']");
		Select select = new Select(languageSelector);
		select.selectByVisibleText(language);
		sleep(defaultSleep);
	}
	
}
