package com.liferay.sales.selenium.api;

public class ScrollIntoViewOptions extends ScrollOptions {
    private final ScrollLogicalPosition block;
    private final ScrollLogicalPosition inline;

    public ScrollIntoViewOptions(final ScrollBehavior scrollBehavior, final ScrollLogicalPosition block, final ScrollLogicalPosition inline) {
        super(scrollBehavior);
        this.block = block;
        this.inline = inline;
    }

    public ScrollLogicalPosition getBlock() {
        return block;
    }

    public ScrollLogicalPosition getInline() {
        return inline;
    }
}
