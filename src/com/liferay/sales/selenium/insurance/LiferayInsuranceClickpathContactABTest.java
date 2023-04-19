package com.liferay.sales.selenium.insurance;
import com.liferay.sales.selenium.api.DriverInitializer;

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
	    WebElement left = getElementById("ab-left");
	    boolean fire = false;
	    
	    if(control != null) {
	    	System.out.print("Seeing A/B Test CONTROL variant, ");
	    	fire = Math.random() < 0.2; 
	    	countControl++;
	    	countControlFired = countControlFired + (fire?1:0);
	    }
	    if(left != null) {
	    	System.out.print("Seeing A/B Test LEFT variant, ");
	    	fire = Math.random() < 0.7;
	    	countLeft++;
	    	countLeftFired = countLeftFired + (fire?1:0);
	    }
	    if(fire) {
	    	System.out.println("clicking link");
    		WebElement mailLink = getElementById("link-icofont-support-faq");
    		doClick(mailLink);
	    } else {
	    	System.out.println("NOP ");
	    }
	    System.out.println("Stats: Control " + countControl + "(" + countControlFired + "), "
	    		+ "Left " + countLeft + "(" + countLeftFired + ")");

	    navigateTo(MENU1_HOME, null);
	    quit();
	}
	
	static int countControl = 0;
	static int countLeft = 0;
	static int countControlFired = 0;
	static int countLeftFired = 0;
}