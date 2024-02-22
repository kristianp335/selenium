package com.liferay.sales.selenium.uow;

import com.liferay.sales.selenium.api.DriverInitializer;
import org.openqa.selenium.WebElement;

/**
 * This class represents a clickpath that views the homepage
 */
public class UoWClickpath1 extends UoWBaseClickpath {
    public UoWClickpath1(DriverInitializer di, String baseUrl) {
        super(di, baseUrl, new UoWUTMGenerator());
    }

    public void run(String username, String password) {
        resizeBrowser(1536, 835);
        deleteAllCookies();

        if (username != null & password != null) {
            log("Running clickpath with " + username);
            doLogin(username, password);
        } else {
            log("Running clickpath with an anonymous user" );
        }

        final String pageUrl = "/";
        doGoTo(pageUrl);

        sleep(3000);

        final WebElement statContainer = getElementById("fragment-0a935270-6263-9f83-531d-c739fe0ce14b" );
        if (statContainer != null) {
            scrollTo(statContainer);
            sleep(1000, false);
        } else {
            log("Unable to scroll to stat container" );
        }

        final WebElement heroVideoContainer = getElementByCSS(".lfr-layout-structure-item-hero-video" );
        if (heroVideoContainer != null) {
            scrollTo(heroVideoContainer);
            sleep(1000, false);
        } else {
            log("Unable to scroll to hero video container" );
        }

        final WebElement vidButton = getElementById("vidbutton" );
        if (vidButton != null) {
            doClick(vidButton);
            sleep(1000, false);
            doClick(vidButton);
            sleep(1000, false);
        } else {
            log("Unable to click the video button" );
        }

        sleep(2000);
        quit();
    }
}
