package com.liferay.sales.selenium.api;

public enum WebDriverType {
    CHROME("/opt/homebrew/bin/chromedriver", "--remote-allow-origins=*"),
    FIREFOX("/opt/homebrew/bin/geckodriver", "");

    private String webDriverPathname;
    private String webDriverArguments;

    WebDriverType(final String webDriverPathname, final String webDriverArguments) {
        this.webDriverPathname = webDriverPathname;
        this.webDriverArguments = webDriverArguments;
    }

    public String getDefaultWebDriverPathname() {
        return this.webDriverPathname;
    }

    public String getDefaultWebDriverArguments() {
        return this.webDriverArguments;
    }
}
