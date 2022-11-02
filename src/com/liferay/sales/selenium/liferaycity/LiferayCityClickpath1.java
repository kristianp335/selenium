package com.liferay.sales.selenium.liferaycity;
import com.liferay.sales.selenium.ClickpathBase;
import com.liferay.sales.selenium.DriverInitializer;

import java.util.Map;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public class LiferayCityClickpath1 extends ClickpathBase {
	
	private String[] SEARCH_TERMS = {"Veranstaltung","Bibliothek", "City", "Karneval", "Parkticket", "Altres Disposicions", "School"};
	private String[] NEWS_LINKS_TEXTS = {
    		"Vorbereitung auf Karneval", 
    		"Entschlossen, Technologie", 
    		"eine neue Bibliothek", 
    		"haben bereits an Seminaren zur Bedienung ihres Telefons"
    };;

	public LiferayCityClickpath1(DriverInitializer di, String baseUrl) {
		super(di, baseUrl);
	}
	
	public void run(Map<String, String> cityUsers) {
		for (Map.Entry<String, String> user : cityUsers.entrySet()) {
			doResize(1536, 835);
		  	deleteAllCookies();	
		    doGoTo(baseUrl);
		    sleep(2000);
		    
		    mark("input[data-qa-id='searchInput']");
		    
		    WebElement searchField = getFirstVisibleElementByXPath("//input[@data-qa-id='searchInput']");
			searchField.click();
			searchField.sendKeys(getOneOf(SEARCH_TERMS));
			searchField.sendKeys(Keys.ENTER);
			sleep(2000);
		    
		    doGoTo(baseUrl + "welcome?p_p_id=com_liferay_login_web_portlet_LoginPortlet&p_p_lifecycle=0&p_p_state=maximized&p_p_mode=view&_com_liferay_login_web_portlet_LoginPortlet_mvcRenderCommandName=%2Flogin%2Flogin&saveLastPath=false");
		    
		    sleep(2000);
		    
		    getLoginField("login").sendKeys(Keys.chord(Keys.CONTROL, "a"));;
		    getLoginField("login").sendKeys(Keys.DELETE);
		    getLoginField("login").sendKeys(user.getKey());
		    sleep(500);
		    getLoginField("password").sendKeys(user.getValue()); 
		    sleep(500);
		    getLoginField("login").sendKeys(Keys.ENTER);
		    
		    sleep(8000);	    
		    
		    doClickText("AKTUELLES");
			doClickRandomText(NEWS_LINKS_TEXTS);
			
		    sleep(4000);
		    
		    quit(); 
		}
	}

	
}