package com.liferay.sales.selenium.liferaycity;

import com.liferay.sales.selenium.api.ClickpathBase;
import com.liferay.sales.selenium.api.DriverInitializer;

import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;

public abstract class LiferayCityBaseClickpath extends ClickpathBase {

	public LiferayCityBaseClickpath(DriverInitializer di, String baseUrl) {
		super(di, baseUrl);
	}

	protected void doLogin(String username, String password) {
		doGoTo(baseUrl + "welcome?p_p_id=com_liferay_login_web_portlet_LoginPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_login_web_portlet_LoginPortlet_mvcRenderCommandName=%2Flogin%2Flogin&saveLastPath=false");

		sleep(1500);

		try {
			// sometimes this fails for unknown reasons. Try again...
			getLoginField("login").sendKeys(Keys.chord(Keys.CONTROL, "a"));;
		} catch (NoSuchElementException e) {
			log("INFO: Retry navigate to log in page!");
			doGoTo(baseUrl + "welcome?p_p_id=com_liferay_login_web_portlet_LoginPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_login_web_portlet_LoginPortlet_mvcRenderCommandName=%2Flogin%2Flogin&saveLastPath=false");
			sleep(3000);

			getLoginField("login").sendKeys(Keys.chord(Keys.CONTROL, "a"));;
		}
		getLoginField("login").sendKeys(Keys.DELETE);
		getLoginField("login").sendKeys(username);
		sleep(500);

		getLoginField("password").sendKeys(password); 
		sleep(500);
		
		log("INFO (doLogin): Logging in as "+ username);
		
		getLoginField("login").sendKeys(Keys.ENTER);
		sleep(8000);
	}
}
