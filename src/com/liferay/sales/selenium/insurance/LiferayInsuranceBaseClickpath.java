package com.liferay.sales.selenium.insurance;

import com.liferay.sales.selenium.api.ClickpathBase;
import com.liferay.sales.selenium.api.DriverInitializer;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

public abstract class LiferayInsuranceBaseClickpath extends ClickpathBase {

	protected static final String[][] HEALTH_PLANS = new String[][] { 
			new String[] {"Family Insurance Plan", "Familienversicherungsplan" },
			new String[] {"Self Insurance Plan", "Selbstversicherungsplan"},
			new String[] {"Domestic Help Insurance Plan", "Versicherungsplan für Haushaltshilfe"}};
	protected static final String[] DOWNLOAD = new String[] {"Download", "herunterladen"};
	// Add other languages here, as soon as menus/page titles are translated
	protected String[] MENU1_HOME = new String[] {"Home", "Start"};
	protected String[] MENU1_PRESSRELEASE = new String[] {"Press Release", "Presse"};
	protected String[] MENU1_INSURANCE_PLANS = new String[] {"Insurance Plans", "Versicherungen"};
	protected String[] MENU2_IP_MOBILE_INSURANCE = new String[] {"Mobile Insurance", "Handyversicherung"};
	protected String[] MENU2_IP_AUTO_INSURANCE = new String[] {"Auto Insurance", "Autoversicherung"};
	protected String[] MENU2_IP_HEALTH_INSURANCE = new String[] {"Health Insurance", "Krankenversicherung"};
	protected String[] MENU2_IP_HOME_INSURANCE = new String[] {"Home Insurance", "Hausratversicherung"};
	protected String[] MENU2_IP_TRAVEL_INSURANCE = new String[] {"Travel Insurance", "Reiseversicherung"};
	protected String[] MENU2_IP_INSURANCE_ORDERS = new String[] {"Insurance Orders" };
	protected String[][] MENU2_IP_ALL = new String[][] { 
		MENU2_IP_AUTO_INSURANCE, 
		MENU2_IP_HEALTH_INSURANCE, 
		MENU2_IP_HOME_INSURANCE, 
		MENU2_IP_TRAVEL_INSURANCE, 
		MENU2_IP_MOBILE_INSURANCE, 
		MENU2_IP_INSURANCE_ORDERS };
	protected String[] MENU1_ABOUT_US = new String[] {"About Us", "Über uns"};
	protected String[] MENU1_AGENT_PORTAL = new String[] {"Agent Portal"};
	protected String[] MENU1_CUSTOMER_PORTAL = new String[] {"Customer Portal"};
	protected String[] MENU1_CONTACT_US = new String[] {"Contact us", "Kontakt"};

	// languages are weighted by available content: At the time of writing, 
	// mostly english content is available, few fragments are available in german.
	protected String[] LANGUAGES = new String[] {
			"english", "english", "english", "english", "english", "english", 
			"deutsch", "deutsch", "deutsch", 
			"català", 
			"العربية",   
			"français", 
			"español", 
			"português [beta]" 
	};
	
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

	protected void fillOutContactForm(int hint) {
		String formRoot = "//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_xlzy_fm']";
		List<WebElement> textInputs = getElementsByXPath(formRoot + "//input[@type='text']");
	    // List<WebElement> radioInputs = getElementsByXPath(formRoot + "//input[@type='radio']");
	    List<WebElement> textareas = getElementsByXPath(formRoot + "//textarea");
	    List<WebElement> selectInputs = getElementsByXPath(formRoot + "//div[contains(@class,'form-builder-select-field')]");
	    List<WebElement> submits = getElementsByXPath(formRoot + "//button[@type='submit']");
	
	    WebElement name = textInputs.get(0);
	    WebElement surname = textInputs.get(1);
	    WebElement telephone = textInputs.get(2);
	    WebElement mail = textInputs.get(3);
	    WebElement policy = textInputs.get(4);
	    WebElement choices = selectInputs.get(0);
	    WebElement message = textareas.get(0);
	    WebElement submit = submits.get(0);
	    
	    scrollTo(name);
	    
	    type(name, "Bloggs (" + hint + ")");
	    type(surname, "Joe");
	    type(telephone, ""+hint+"-1234567");
	    type(mail, "test+" +hint + "@liferay.com");
	    type(policy, ""+hint+"-"+hint);
	    
	    choices.click();
	    sleep(1000);
	
	    int chosenItem = (int) (Math.random()*4+1); 
	    WebElement choice = getElementByXPath("//button[@data-testid='dropdownItem-"+chosenItem+"']");
	    choice.click();
	    sleep(500);
	
	    scrollTo(message);
	    type(message, "This is a random comment with hint " + hint);
	    if(Math.random() < 0.8 ) {
	    	doClick(submit);
	    } else {
	    	System.out.println("Abandoning Form, it's filled out but not submitted");
	    }
	}
}
