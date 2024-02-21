package com.liferay.sales.selenium.uow;

import com.liferay.sales.selenium.api.DriverInitializer;

public class UoWClickpath1 extends UoWBaseClickpath {

    public UoWClickpath1(DriverInitializer di, String baseUrl) {
        super(di, baseUrl);
    }

    public void run(String username, String password) {
        resizeBrowser(1536, 835);
        deleteAllCookies();
        doGoTo(utmGenerator.decorateUrl(baseUrl));
        sleep(2000);

        quit();
    }
}
