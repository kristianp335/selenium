package com.liferay.sales.selenium.api;

public class ScrollOptions {
    private final ScrollBehavior scrollBehavior;

    public ScrollOptions(final ScrollBehavior scrollBehavior) {
        this.scrollBehavior = scrollBehavior;
    }

    public ScrollBehavior getScrollBehavior() {
        return scrollBehavior;
    }
}
