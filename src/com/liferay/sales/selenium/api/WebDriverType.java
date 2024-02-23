package com.liferay.sales.selenium.api;

public enum WebDriverType {
    CHROME("/opt/homebrew/bin/chromedriver", "--remote-allow-origins=*"),
    FIREFOX("/opt/homebrew/bin/geckodriver", "");

    private final String webDriverArguments;
    private final String webDriverPathname;

    WebDriverType(final String webDriverPathname, final String webDriverArguments) {
        this.webDriverPathname = webDriverPathname;
        this.webDriverArguments = webDriverArguments;
    }

    public String getDefaultWebDriverArguments() {
        return this.webDriverArguments;
    }

    public String getDefaultWebDriverPathname() {
        return this.webDriverPathname;
    }
}
