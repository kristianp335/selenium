package com.liferay.sales.selenium.insurance;

import com.liferay.sales.selenium.api.DriverInitializer;
import org.openqa.selenium.WebElement;

public class LiferayInsuranceClickpathLifeLandingABTest extends LiferayInsuranceBaseClickpath {

    static int countControl;
    static int countControlFired;
    static int countInformal;
    static int countInformalFired;

    public LiferayInsuranceClickpathLifeLandingABTest(DriverInitializer di, String baseUrl) {
        super(di, baseUrl);
    }

    public void run(String username, String password) {
        resizeBrowser(1536, 835);
        deleteAllCookies();
        doGoTo(baseUrl);
//	    navigateTo(MENU1_CONTACT_US, null);
        doClickText("Click here to learn more");

        WebElement control = getElementById("ab-control");
        WebElement informal = getElementById("ab-informal");
        boolean fire = false;
        String formId = "unknown";

        if (control != null) {
            System.out.print("Seeing A/B Test CONTROL variant, ");
            fire = Math.random() < 0.7;
            countControl++;
            countControlFired = countControlFired + (fire ? 1 : 0);
        }
        if (informal != null) {
            System.out.print("Seeing A/B Test INFORMAL variant, ");
            fire = Math.random() < 0.3;
            countInformal++;
            countInformalFired = countInformalFired + (fire ? 1 : 0);
        }

        if (fire) {
            WebElement button = getElementById("fragment-oadh-link");
            button.click();
            sleep(3000);
        } else {
            System.out.println("NOP ");
        }
        System.out.println("Stats: Control " + countControl + "(" + countControlFired + "), "
                + "Informal " + countInformal + "(" + countInformalFired + ")");

        navigateTo(MENU1_HOME, null);
        quit();
    }
}