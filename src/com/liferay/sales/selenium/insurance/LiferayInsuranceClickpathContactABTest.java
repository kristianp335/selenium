package com.liferay.sales.selenium.insurance;
import com.liferay.sales.selenium.api.DriverInitializer;

import java.util.List;

import org.openqa.selenium.WebElement;

public class LiferayInsuranceClickpathContactABTest extends LiferayInsuranceBaseClickpath {
	
	public LiferayInsuranceClickpathContactABTest(DriverInitializer di, String baseUrl) {
		super(di, baseUrl);
	}
	
	public void run(String username, String password) {
		resizeBrowser(1536, 835);
	  	deleteAllCookies();	
	    doGoTo(baseUrl);
	    navigateTo(MENU1_CONTACT_US, null);
	    
	    WebElement control = getElementById("ab-control");
	    WebElement left = getElementById("ab-informal");
	    boolean fire = false;
	    String formId = "unknown";
	    
	    if(control != null) {
	    	System.out.print("Seeing A/B Test CONTROL variant, ");
	    	formId = "_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_qwdn_fm";
	    	fire = Math.random() < 0.7; 
	    	countControl++;
	    	countControlFired = countControlFired + (fire?1:0);
	    }
	    if(left != null) {
	    	System.out.print("Seeing A/B Test INFORMAL variant, ");
	    	formId = "_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_kqjn_fm"; 
	    	fire = Math.random() < 0.3;
	    	countInformal++;
	    	countInformalFired = countInformalFired + (fire?1:0);
	    }

	    if(fire) {
	    	System.out.println("Submitting ContactUs form");
	    	
	    	List<WebElement> textInputs = getElementsByXPath("//form[@id='" + formId + "']//input[@type='text']");
	    	List<WebElement> textareas = getElementsByXPath("//form[@id='" + formId + "']//textarea");
		    List<WebElement> selectDropDowns = getElementsByXPath("//form[@id='" + formId + "']//div[@class='form-control results-chosen select-field-trigger']");
		    WebElement firstName = textInputs.get(0);
		    WebElement surName = textInputs.get(1);
		    WebElement telephone = textInputs.get(2);
		    WebElement mail = textInputs.get(3);
		    WebElement policyNumber = textInputs.get(4);

		    WebElement message = textareas.get(0);
		    
		    WebElement selectDropDown = selectDropDowns.get(0);
		    selectDropDown.click();
		    scrollTo(message);
		    WebElement choice = getElementByXPath("//button[@data-testid='dropdownItem-2']");
		    choice.click();
		    
		    firstName.sendKeys("Joe");
		    String hint = ""+ countControlFired + "/" + countInformalFired;
		    surName.sendKeys("Bloggs (" + hint + ")");
		    int phone = (int) (Math.random()*1e10);
	    	int policyNum = (int) (Math.random()*1e10);
	    	telephone.sendKeys(""+phone);
	    	mail.sendKeys("joe.bloggs@example.com");
	    	policyNumber.sendKeys(""+policyNum);
	    	message.sendKeys("Lorem Ipsum sit dolor " + hint);
	    	
    		WebElement submissionLink = getElementById("ddm-form-submit");
    		doClick(submissionLink);
	    } else {
	    	System.out.println("NOP ");
	    }
	    System.out.println("Stats: Control " + countControl + "(" + countControlFired + "), "
	    		+ "Informal " + countInformal + "(" + countInformalFired + ")");

	    navigateTo(MENU1_HOME, null);
	    quit();
	}
	
	static int countControl = 0;
	static int countInformal = 0;
	static int countControlFired = 0;
	static int countInformalFired = 0;
}