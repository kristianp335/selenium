package com.liferay.sales.selenium.liferaycity;

import com.liferay.sales.selenium.api.DriverInitializer;
import org.openqa.selenium.WebElement;

public class LiferayCityClickpathABTest extends LiferayCityBaseClickpath {

    static int countControl;
    static int countControlFired;
    static int countHorizontal;
    static int countHorizontalFired;
    public LiferayCityClickpathABTest(DriverInitializer di, String baseUrl) {
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
        WebElement control = getElementById("ab-control");
        WebElement horizontal = getElementById("ab-horizontal");
        boolean fire = false;

        if (control != null) {
            System.out.print("CONTROL ");
            fire = Math.random() < 0.25;
            countControl++;
            countControlFired = countControlFired + (fire ? 1 : 0);
        }
        if (horizontal != null) {
            System.out.print("HORIZONTAL ");
            fire = Math.random() > 0.15;
            countHorizontal++;
            countHorizontalFired = countHorizontalFired + (fire ? 1 : 0);
        }
        if (fire) {
            System.out.println("firing A/B Test action");
            WebElement faq = getElementById("faqarticle-company2");
            doClick(faq);
        } else {
            System.out.println("NOP for A/B Test");
        }
        System.out.println("Stats: Control " + countControl + "(" + countControlFired + "), "
                + "Horizontal: " + countHorizontal + "(" + countHorizontalFired + ")");

        quit();
    }
}