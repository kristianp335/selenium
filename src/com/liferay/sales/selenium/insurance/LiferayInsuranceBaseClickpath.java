package com.liferay.sales.selenium.insurance;

import com.liferay.sales.selenium.api.ClickpathBase;
import com.liferay.sales.selenium.api.DriverInitializer;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
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
	 * @param level1 menu level 1
	 * @param level2 can be null, to navigate to level 1 menu item
	 */
	public void navigateTo(String level1, String level2) {
		WebDriver driver = getDriver();
		Actions builder = new Actions(driver);
		WebElement l1element = driver.findElement(By.xpath("//div[@id='navbarmain']//a[normalize-space()='"+level1+"']"));
		builder.moveToElement(l1element).build().perform();
		sleep(1000);
		if(level2 == null) {
			l1element.click();
		} else {
			WebElement l2element = driver.findElement(By.xpath("//div[@id='navbarmain']/ul/li/ul/li/a[normalize-space()='"+level2+"']"));
			l2element.click();
		}
		sleep(defaultSleep);
	}

	/**
	 * Navigate to menu item l1/l2 (or just l1 if l2==null). The array provides
	 * different language versions of the menus. 
	 * @param level1
	 * @param level2
	 */
	protected void navigateTo(String[] level1, String[] level2) {
		WebDriver driver = getDriver();
		Actions builder = new Actions(driver);
		WebElement l1element = null;
		for (int i = 0; i < level1.length; i++) {
			try {
				l1element = driver.findElement(By.xpath("//div[@id='navbarmain']//a[normalize-space()='"+level1[i]+"']"));
			} catch (NoSuchElementException ignore) {
			}
			System.out.println("Navigation " + level1[i] + ": " + (l1element==null? "not ":"") + "found");
			if(l1element != null) break;
		}
		builder.moveToElement(l1element).build().perform();
		sleep(1000);
		if(level2 == null) {
			l1element.click();
		} else {
			WebElement l2element = null;
			for (int i = 0; i < level2.length; i++) {
				try {
					System.out.println("Searching for L2 " + level2[i]);
					l2element = driver.findElement(By.xpath("//div[@id='navbarmain']/ul/li/ul/li/a[normalize-space()='"+level2[i]+"']"));
				} catch (NoSuchElementException ignore) {
				}
				if(l2element!=null) break;
			}
			l2element.click();
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
