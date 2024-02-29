package com.liferay.sales.selenium.api;

public class ClickpathConfig<C extends ClickpathBase> {
    private final Class<C> clickpathClass;
    private final String key;
    private final Type type;

    public ClickpathConfig(final Class<C> clickpathClass, final String key, final Type type) {
        this.clickpathClass = clickpathClass;
        this.key = key;
        this.type = type;
    }

    public Class<C> getClickpathClass() {
        return clickpathClass;
    }

    public String getKey() {
        return key;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        STANDARD,
        AB
    }
}
