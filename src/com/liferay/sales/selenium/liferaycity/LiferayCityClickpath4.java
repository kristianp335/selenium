package com.liferay.sales.selenium.liferaycity;
import com.liferay.sales.selenium.api.DriverInitializer;

import java.util.List;

import org.openqa.selenium.WebElement;

public class LiferayCityClickpath4 extends LiferayCityBaseClickpath {

	public LiferayCityClickpath4(DriverInitializer di, String baseUrl) {
		super(di, baseUrl);
	}
	
	public void run(String username, String password) {
		resizeBrowser(1536, 835);
	  	deleteAllCookies();	
	    doGoTo(utmGenerator.decorateUrl(baseUrl));
	    doLogin(username, password);
	    
	    doClickText(oneOf("CITIZEN AREA", "REA DEL CIUDADANO"));
	    sleep(1000);
	    
	    doClickText(oneOf("My Services","Meine Dienstleistungen", "Mis Servicios"));
	    doClickText(oneOf("Register a business","Eine Firma anmelden", "Registrar una empresa"));
	    doClickText(oneOf("Go to service","Realizar Trámite"));

	    sleep(1000);

		fillOutBusinessRegistrationForm((int)(Math.random()*1e6));

	    quit(); 
	}

	@SuppressWarnings("unused")
	private void fillOutBusinessRegistrationForm(int hint) {
		WebElement commentsBox = getFirstVisibleElementByXPath("//textarea");
		commentsBox.sendKeys("a comment with hint " + hint);
		sleep(2000);
    
	    WebElement submit = getFirstVisibleElementByXPath("//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_stsg_fm']//button[@type='button']");
	    
	    doClick(submit);
		
		List<WebElement> textInputs = getElementsByXPath("//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_stsg_fm']//input[@type='text']");
	    List<WebElement> radioInputs = getElementsByXPath("//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_stsg_fm']//input[@type='radio']");
	    List<WebElement> textareas = getElementsByXPath("//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_stsg_fm']//textarea");
	    List<WebElement> submits = getElementsByXPath("//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_stsg_fm']//button[@type='submit']");

	    WebElement select = getFirstVisibleElementByXPath("//div[@class='form-builder-select-field']");
	    WebElement date = textInputs.get(0);
	    WebElement address = textInputs.get(1);
	    WebElement numId = textInputs.get(2);
	    WebElement otherCard = textInputs.get(3);

	    WebElement yesChoice = radioInputs.get(0);
	    WebElement noChoice = radioInputs.get(1);
	    
	    WebElement comment = getFirstVisibleElementByXPath("//form[@id='_com_liferay_dynamic_data_mapping_form_web_portlet_DDMFormPortlet_INSTANCE_stsg_fm']//textarea");
	    submit = submits.get(0);

	    scrollTo(date);
	    
// ignore selectbox: It doesn't work this way
//	    doClickText(oneOf("Choose an Option", "Wählen Sie eine Option", "Seleccione una opción"));
//	    sleep(500);
//	    doClickText(oneOf("Business type A", "Negocio tipo A"));
//	    sleep(500);

	    date.sendKeys("01011970");
	    sleep(500);
	    if(Math.random()<0.7) {
		    yesChoice.click();
	    } else {
	    	noChoice.click();
	    }
	    sleep(500);
	    address.sendKeys("random address " + hint);
	    sleep(500);
	    numId.sendKeys("id"+hint);
	    sleep(500);
	    otherCard.sendKeys(""+hint+hint);
	    sleep(500);
	    
	    scrollTo(comment);
	    comment.sendKeys("This is a random comment with hint " + hint);
	    sleep(500);
	    doClick(submit);
	}
}