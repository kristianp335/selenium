package com.liferay.sales.selenium.insurance;

import com.liferay.sales.selenium.api.DriverInitializer;

public class LiferayInsuranceClickpath1 extends LiferayInsuranceBaseClickpath {

	/**
	 * Set up these referrers in your /etc/hosts and make sure they have a link 
	 * containing the text "IDC Demo" that can be clicked.
	 */
	private String[] REFERRERS = new String[] {
			"http://twitter.example.com/links.html", 
			"http://facebook.example.com/links.html"
			};
	
	
	public LiferayInsuranceClickpath1(DriverInitializer di, String baseUrl) {
		super(di, baseUrl);
	}
	
	@Override
	public void run(String username, String password) {
		double startPageRandomization = Math.random();
		if(startPageRandomization<0.3 && REFERRERS.length>0) {
			doGoTo(pickRandom(REFERRERS));
			doClickText("IDC Demo");
		} else {
			doGoTo(baseUrl);
		}
		selectLanguage(pickRandom(LANGUAGES));

		navigateTo(MENU1_INSURANCE_PLANS, pickRandom(MENU2_IP_ALL));
		navigateTo(MENU1_HOME, null);
		selectLanguage("english");
		navigateTo(MENU1_CONTACT_US, null);
		fillOutContactForm((int)(Math.random()*1e6));
	}
}
