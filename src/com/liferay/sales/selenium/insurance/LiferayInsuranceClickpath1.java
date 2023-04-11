package com.liferay.sales.selenium.insurance;

import com.liferay.sales.selenium.api.DriverInitializer;

public class LiferayInsuranceClickpath1 extends LiferayInsuranceBaseClickpath {

	public LiferayInsuranceClickpath1(DriverInitializer di, String baseUrl) {
		super(di, baseUrl);
	}

	
	@Override
	public void run(String username, String password) {
		doGoTo(baseUrl);
		navigateTo("Insurance Plans", "Mobile Insurance");
		navigateTo("Contact us", null);
		selectLanguage("deutsch");
		selectLanguage("english");
		selectLanguage("العربية");
	}

}
