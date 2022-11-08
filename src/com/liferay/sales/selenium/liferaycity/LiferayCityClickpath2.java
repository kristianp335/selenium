package com.liferay.sales.selenium.liferaycity;
import com.liferay.sales.selenium.api.ClickpathBase;
import com.liferay.sales.selenium.api.DriverInitializer;

import java.util.List;

import org.openqa.selenium.WebElement;

public class LiferayCityClickpath2 extends ClickpathBase {

	public LiferayCityClickpath2(DriverInitializer di, String baseUrl) {
		super(di, baseUrl);
	}
	
	public void run(String username, String password) {
		resizeBrowser(1536, 835);
	  	deleteAllCookies();	
	    doGoTo(baseUrl);
	    sleep(2000);
	    
	    doGoTo(baseUrl + "contact");
	    
		doClickText("Contact Us Form");
		
		fillOutContactForm((int)(Math.random()*1e6));

	    quit(); 
	}

	@SuppressWarnings("unused")
	private void fillOutContactForm(int hint) {
		List<WebElement> textInputs = getElementsByXPath("//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_ifua_fm']//input[@type='text']");
	    List<WebElement> radioInputs = getElementsByXPath("//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_ifua_fm']//input[@type='radio']");
	    List<WebElement> textareas = getElementsByXPath("//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_ifua_fm']//textarea");
	    List<WebElement> submits = getElementsByXPath("//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_ifua_fm']//button[@type='submit']");
	    WebElement name = textInputs.get(0);
	    WebElement surname = textInputs.get(1);
	    WebElement telephone = textInputs.get(2);
	    WebElement mail1 = textInputs.get(3);
	    WebElement mail2 = textInputs.get(4);

	    WebElement telephoneChoice = radioInputs.get(0);
	    WebElement mailChoice = radioInputs.get(1);
	    
	    WebElement comment = textareas.get(0);
	    WebElement submit = submits.get(0);
	    
	    name.sendKeys("Bloggs (" + hint + ")");
	    surname.sendKeys("Joe");
	    
	    if(Math.random()<0.5) {
		    doClick(mailChoice);
		    scrollTo(mail1);

		    mail1.sendKeys("test+" + hint + "@example.com");
		    mail2.sendKeys("test+" + hint + "@example.com");
	    } else {
	    	doClick(telephoneChoice);
		    scrollTo(telephone);

		    telephone.sendKeys((""+hint+"01234567890123").substring(0, 12));
	    }
	    scrollTo(comment);
	    comment.sendKeys("This is a random comment with hint " + hint);
	    sleep(500);
	    doClick(submit);
	}

	
}