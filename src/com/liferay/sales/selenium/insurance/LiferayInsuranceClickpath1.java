package com.liferay.sales.selenium.insurance;

import com.liferay.sales.selenium.api.DriverInitializer;

import java.util.List;

import org.openqa.selenium.WebElement;

public class LiferayInsuranceClickpath1 extends LiferayInsuranceBaseClickpath {

	public LiferayInsuranceClickpath1(DriverInitializer di, String baseUrl) {
		super(di, baseUrl);
	}
	
	@Override
	public void run(String username, String password) {
		double startPageRandomization = Math.random();
		if(startPageRandomization<0.2) {
			doGoTo("http://twitter.example.com/links.html");
			doClickText("IDC Demo");
		} else if (startPageRandomization < 0.4) {
			doGoTo("http://facebook.example.com/links.html");
			doClickText("IDC Demo");
		} else {
			doGoTo(baseUrl);
		}
		selectRandomLanguage();
		int randomL2Choice = (int) (Math.random()*MENU2_IP_ALL.length);
		String[] randomL2Menu = MENU2_IP_ALL[randomL2Choice];

		navigateTo(MENU1_INSURANCE_PLANS, randomL2Menu);
		navigateTo(MENU1_HOME, null);
		selectLanguage("english");
		navigateTo(MENU1_CONTACT_US, null);
		fillOutContactForm((int)(Math.random()*1e6));
	}

	private void fillOutContactForm(int hint) {
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
