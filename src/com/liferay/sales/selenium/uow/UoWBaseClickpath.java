package com.liferay.sales.selenium.uow;

import com.liferay.sales.selenium.api.ClickpathBase;
import com.liferay.sales.selenium.api.DriverInitializer;
import com.liferay.sales.selenium.util.UTMGenerator;
import org.openqa.selenium.Keys;

public abstract class UoWBaseClickpath extends ClickpathBase {
    protected static final String[] ENTRY_PAGES = {
            "/", "/study", "/research", "/news"
    };
    protected static final String[] MENU_OPTIONS = {
            "Study", "Research", "News"
    };

    public UoWBaseClickpath(DriverInitializer di, String baseUrl, UTMGenerator utmGenerator) {
        super(di, baseUrl, utmGenerator);
    }

    @Override
    protected void doLogin(String username, String password) {
        doGoTo(baseUrl);
        sleep(1500, false);
        doClickText("Sign In" );
        sleep(1500, false);
        getLoginField("login" ).sendKeys(Keys.chord(Keys.CONTROL, "a" ));
        getLoginField("login" ).sendKeys(Keys.DELETE);
        getLoginField("login" ).sendKeys(username);
        sleep(500, false);

        getLoginField("password" ).sendKeys(password);
        sleep(500, false);

        log("INFO (doLogin): Logging in as " + username);

        getLoginField("login" ).sendKeys(Keys.ENTER);
        sleep(10000, false);
    }
}
