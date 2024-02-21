package com.liferay.sales.selenium.liferaycity;

import com.liferay.sales.selenium.api.DriverInitializer;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;

public class LiferayCityClickpath3 extends LiferayCityBaseClickpath {

    // increase "popularity" by duplicating entries
    private final String[][] CITIZEN_PAGES = {
            {"Wunschliste", "My Wishes", "Mis Favoritos"},
            {"Ankündigungen", "Announcements", "Anuncios"},
            {"Meine Dienstleistungen", "My Services", "Mis Servicios"},
            {"FAQs", "Preguntas Frecuentes"},
            {"FAQs", "Preguntas Frecuentes"},
            {"Stadtplan", "City Map", "Mapa de la Ciudad"}
    };
    // increase "popularity" by duplicating entries
    private final String[] DOCUMENTS = {
            "BOE-A-2021-3982",
            "BOE-A-2021-3983",
            "BOE-A-2021-3986",
            "mapa-metro-barcelona-2021",
            "Metro Subway Map",
            "Turism Map"};

    public LiferayCityClickpath3(DriverInitializer di, String baseUrl) {
        super(di, baseUrl);
    }

    public void run(String username, String password) {
        resizeBrowser(1536, 835);
        deleteAllCookies();
        doGoTo(utmGenerator.decorateUrl(baseUrl));
        doLogin(username, password);

        doClickText(oneOf("CITIZEN AREA", "REA DEL CIUDADANO"));
        sleep(1000);

        doClickText(oneOf("FAQs", "Preguntas"));
        List<WebElement> faqEntries = getElementsByXPath("//a[@class='drop-question']");
        Collections.shuffle(faqEntries);
        WebElement faqEntry = faqEntries.get(0);
        scrollTo(faqEntry);
        sleep(400);
        doClick(faqEntry);

        doClickText("Tickets");
        doClickText(oneOf("Meine Tickets", "My Tickets", "Mis Tickets"));
        doClickRandomText(CITIZEN_PAGES);

        doClickText(oneOf("Dokumente und mehr", "Documents & More", "Documentos y"));
        sleep(2000);
        doClickRandomText(DOCUMENTS);
//		if(Math.random()<0.4) {
//		WebElement infoButton = getElementByXPath("//a[@data-qa-id='infoButton']");
//		infoButton.click();
//		sleep(1000);
        doClickText(oneOf("Informationen", "Información", "Info")); // problematic in spanish: Does not find button "Información", but page "Alertas E Información" (on "Info")
        doClickText(oneOf("Herunterladen", "Download", "Descargar"));
//		}
        sleep(5000);
        quit();
    }
}