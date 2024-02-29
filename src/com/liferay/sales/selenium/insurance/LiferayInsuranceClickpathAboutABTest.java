package com.liferay.sales.selenium.insurance;

import com.liferay.sales.selenium.api.DriverInitializer;
import org.openqa.selenium.WebElement;

public class LiferayInsuranceClickpathAboutABTest extends LiferayInsuranceBaseClickpath {

    static int countControl;
    static int countControlFired;
    static int countTop;
    static int countTopFired;

    public LiferayInsuranceClickpathAboutABTest(DriverInitializer di, String baseUrl) {
        super(di, baseUrl);
    }

    public void run(String username, String password) {
        resizeBrowser(1536, 835);
        deleteAllCookies();
        doGoTo(baseUrl);
        navigateTo(MENU1_ABOUT_US, null);

        WebElement control = getElementById("ab-control");
        WebElement left = getElementById("ab-top");
        boolean fire = false;

        if (control != null) {
            System.out.print("Seeing A/B Test CONTROL variant, ");
            fire = Math.random() < 0.2;
            countControl++;
            countControlFired = countControlFired + (fire ? 1 : 0);
        }
        if (left != null) {
            System.out.print("Seeing A/B Test TOP variant, ");
            fire = Math.random() < 0.7;
            countTop++;
            countTopFired = countTopFired + (fire ? 1 : 0);
        }
        if (fire) {
            WebElement link = getElementById("learn-more");
            scrollTo(link);
            sleep(defaultSleep);
            System.out.println("clicking link");
            doClickText("learn more");
//    		WebElement mailLink = getElementById("link-icofont-support-faq");
//    		doClick(mailLink);
        } else {
            System.out.println("NOP ");
        }
        System.out.println("Stats: Control " + countControl + "(" + countControlFired + "), "
                + "Top " + countTop + "(" + countTopFired + ")");

        navigateTo(MENU1_HOME, null);
        quit();
    }
}