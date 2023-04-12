package com.liferay.sales.selenium.insurance;

import com.liferay.sales.selenium.api.DriverInitializer;

import java.util.List;

import org.openqa.selenium.WebElement;

public class LiferayInsuranceClickpath1 extends LiferayInsuranceBaseClickpath {

	private String[] MENU1_HOME = new String[] {"Home", "Start"};
	private String[] MENU1_INSURANCE_PLANS = new String[] {"Insurance Plans", "Versicherungen"};
	private String[] MENU2_IP_MOBILE_INSURANCE = new String[] {"Mobile Insurance", "Handyversicherung"};
	private String[] MENU1_CONTACT_US = new String[] {"Contact us", "Kontakt"};
	
	public LiferayInsuranceClickpath1(DriverInitializer di, String baseUrl) {
		super(di, baseUrl);
	}
	
	@Override
	public void run(String username, String password) {
		doGoTo(baseUrl);
		selectLanguage("deutsch");
//		navigateTo("Insurance Plans", "Mobile Insurance");
		navigateTo(MENU1_INSURANCE_PLANS, MENU2_IP_MOBILE_INSURANCE);
		navigateTo(MENU1_HOME, null);
		navigateTo(MENU1_CONTACT_US, null);
//		selectLanguage("english");
		fillOutContactForm(42);
	}

	private void fillOutContactForm(int hint) {
		List<WebElement> textInputs = getElementsByXPath("//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_xlzy_fm']//input[@type='text']");
	    List<WebElement> radioInputs = getElementsByXPath("//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_xlzy_fm']//input[@type='radio']");
	    List<WebElement> textareas = getElementsByXPath("//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_xlzy_fm']//textarea");
	    List<WebElement> selectInputs = getElementsByXPath("//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_xlzy_fm']//div[contains(@class,'form-builder-select-field')]");
	    List<WebElement> submits = getElementsByXPath("//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_xlzy_fm']//button[@type='submit']");
	    System.out.println("Found Inputs: ");
	    System.out.println("  TextInput: " + textInputs.size());
	    System.out.println("  RadioInput: " + radioInputs.size());
	    System.out.println("  Selects: " + selectInputs.size());
	    System.out.println("  Textareas: " + textareas.size());
	    System.out.println("  Submits: " + submits.size());
	    WebElement name = textInputs.get(0);
	    WebElement surname = textInputs.get(1);
	    WebElement telephone = textInputs.get(2);
	    WebElement mail = textInputs.get(3);
	    WebElement policy = textInputs.get(4);
	    WebElement choices = selectInputs.get(0);

	    WebElement message = textareas.get(0);
	    WebElement submit = submits.get(0);
	    
	    scrollTo(name);
	    name.sendKeys("Bloggs (" + hint + ")");
	    sleep(100);
	    surname.sendKeys("Joe");
	    sleep(100);
	    telephone.sendKeys(""+hint+"-1234567");
	    sleep(100);
	    mail.sendKeys("test+" +hint + "@liferay.com");
	    sleep(100);
	    policy.sendKeys(""+hint+"-"+hint);
	    sleep(100);
	    choices.click();
	    sleep(1000);

	    int chosenItem = (int) (Math.random()*4+1); 
	    WebElement choice = getElementByXPath("//button[@data-testid='dropdownItem-"+chosenItem+"']");
	    choice.click();
	    sleep(500);

	    scrollTo(message);
	    message.sendKeys("This is a random comment with hint " + hint);
	    sleep(500);
	    if(Math.random() < 0.9 ) {
	    	doClick(submit);
	    } else {
	    	System.out.println("Abandoning Form, it's filled out but not submitted");
	    }
	}
}
