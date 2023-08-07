package com.liferay.sales.selenium.liferaycity;
import com.liferay.sales.selenium.api.DriverInitializer;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

public class LiferayCityClickpath1 extends LiferayCityBaseClickpath {
	
	private String[] SEARCH_TERMS = {"Veranstaltung","Bibliothek", "City", "Karneval", "Parkticket", "Altres Disposicions", "School"};
	private String[][] NEWS_LINKS_TEXTS = {
    		{"Vorbereitung auf Karneval", "Carnival is coming", "El Carnaval se acerca" },
    		{"Entschlossen, Technologie", "Commitment to improve technology", "Compromiso de mejorar la tecnolog" },
    		{"eine neue Bibliothek", "LiferayCity opens a new library", "LiferayCity abre una nueva biblioteca" },
    		{"haben bereits an Seminaren zur Bedienung ihres Telefons", "More than 200 older people", "200 personas mayores" }
    };;

	public LiferayCityClickpath1(DriverInitializer di, String baseUrl) {
		super(di, baseUrl);
	}
	
	public void run(String username, String password) {
		resizeBrowser(1536, 835);
	  	deleteAllCookies();	
	    doGoTo(utmGenerator.decorateUrl(baseUrl));
	    sleep(2000);
	    
	    mark("input[data-qa-id='searchInput']");
	    
	    WebElement searchField = getFirstVisibleElementByXPath("//input[@data-qa-id='searchInput']");
		searchField.click();
		searchField.sendKeys(pickRandom(SEARCH_TERMS));
		searchField.sendKeys(Keys.ENTER);
		sleep(2000);
	    
	    doLogin(username, password);
	    
	    doClickText(oneOf("AKTUELLES", "NEWS", "NOTICIAS"));
		doClickRandomText(NEWS_LINKS_TEXTS);
		
	    sleep(4000);
	    
	    quit(); 
	}
}