package com.liferay.sales.selenium.liferaycity;

import com.liferay.sales.selenium.api.DriverInitializer;
import org.openqa.selenium.WebElement;

public class LiferayCityClickpathNewsABTest extends LiferayCityBaseClickpath {

    static int countControl;
    static int countControlFired;
    static int countTop;
    static int countTopFired;
    public LiferayCityClickpathNewsABTest(DriverInitializer di, String baseUrl) {
        super(di, baseUrl);
    }

    public void run(String username, String password) {
        resizeBrowser(1536, 835);
        deleteAllCookies();
        doGoTo(utmGenerator.decorateUrl(baseUrl));

        WebElement control = getElementById("ab-control");
        WebElement top = getElementById("ab-top");
        boolean fire = false;

        if (control != null) {
            System.out.print("CONTROL VARIANT ");
            fire = Math.random() < 0.2;
            countControl++;
            countControlFired = countControlFired + (fire ? 1 : 0);
        }
        if (top != null) {
            System.out.print("TOP VARIANT ");
            fire = Math.random() > 0.1;
            countTop++;
            countTopFired = countTopFired + (fire ? 1 : 0);
        }
        if (fire) {
            System.out.println("firing A/B Test action");
            WebElement newsLink = getElementById("the-news-link");
            doClick(newsLink);
        } else {
            System.out.println("NOP for A/B Test action");
        }
        System.out.println("Stats: Control " + countControl + "(" + countControlFired + "), "
                + "Top " + countTop + "(" + countTopFired + ")");
        quit();
    }
}