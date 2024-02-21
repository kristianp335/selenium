package com.liferay.sales.selenium.uow;

import com.liferay.sales.selenium.api.ClickpathBase;
import com.liferay.sales.selenium.api.DriverInitializer;
import com.liferay.sales.selenium.util.UTMGenerator;

public abstract class UoWBaseClickpath extends ClickpathBase {

    public UoWBaseClickpath(DriverInitializer di, String baseUrl) {
        super(di, baseUrl);
    }
}
