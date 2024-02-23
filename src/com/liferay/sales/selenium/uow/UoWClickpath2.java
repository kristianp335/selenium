package com.liferay.sales.selenium.uow;

import com.liferay.sales.selenium.api.DriverInitializer;

/**
 * This class represents a clickpath that targets a random landing page with or without UTM parameters
 */
public class UoWClickpath2 extends UoWBaseClickpath {
    public UoWClickpath2(DriverInitializer di, String baseUrl) {
        super(di, baseUrl, new UoWUTMGenerator());
    }

    public void run(String username, String password) {
        resizeBrowser(1536, 835);
        deleteAllCookies();

        if (username != null & password != null) {
            log("Running clickpath with " + username);
            doLogin(username, password);
        } else {
            log("Running clickpath with an anonymous user");
        }

        final String pageUrl = pickRandom(ENTRY_PAGES);
        doGoTo(utmGenerator.decorateUrl(pageUrl));

        quit();
    }
}
