package com.liferay.sales.selenium.liferaycity;
import com.liferay.sales.selenium.api.DriverInitializer;

import org.openqa.selenium.WebElement;

public class LiferayCityClickpath5 extends LiferayCityBaseClickpath {
	
	public LiferayCityClickpath5(DriverInitializer di, String baseUrl) {
		super(di, baseUrl);
	}
	
	public void run(String username, String password) {
		resizeBrowser(1536, 835);
	  	deleteAllCookies();	
	    doGoTo(baseUrl);
	    doLogin(username, password);
	    
	    doClickText(oneOf("CITIZEN AREA", "REA DEL CIUDADANO"));
	    sleep(1000);
	    
	    doClickText(oneOf("FAQs","Preguntas"));
	    WebElement control = getElementById("ab-control");
	    WebElement horizontal = getElementById("ab-horizontal");
	    WebElement vertical = getElementById("ab-vertical");
	    boolean fire = false;
	    
	    if(control != null) {
	    	System.out.print("CONTROL ");
	    	fire = Math.random() < 0.5; 
	    }
	    if(horizontal != null) {
	    	System.out.print("HORIZONTAL ");
	    	fire = Math.random() > 0.1;
	    }
	    if(vertical != null) {
	    	System.out.print("VERTICAL ");
	    	fire = Math.random() > 0.9;
	    }
	    if(fire) {
	    	System.out.println("firing A/B Test action");
    		WebElement faq = getElementById("faqarticle-company2");
    		doClick(faq);
	    } else {
	    	System.out.println("NOP for A/B Test action");
	    }
	    
		quit();
	}
}