package com.liferay.sales.selenium.api;

public enum MarkMethod {
    BORDER("border = '2px solid red'"),
    BACKGROUND("backgroundColor = 'red'");

    public String getStyleString() {
        return styleString;
    }

    private final String styleString;

    MarkMethod(final String styleString) {
        this.styleString = styleString;
    }
}
