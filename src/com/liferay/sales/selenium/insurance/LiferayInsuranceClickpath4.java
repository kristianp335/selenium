package com.liferay.sales.selenium.insurance;

import com.liferay.sales.selenium.api.DriverInitializer;

public class LiferayInsuranceClickpath4 extends LiferayInsuranceBaseClickpath {

    private final String password;
    private final String user;

    public LiferayInsuranceClickpath4(DriverInitializer di, String baseUrl, String user, String password) {
        super(di, baseUrl);
        this.user = user;
        this.password = password;
    }

    @Override
    public void run(String ignoredUsername, String ignoredPassword) {
        doGoTo(baseUrl);
        doLogin(user, password);

        navigateTo(MENU1_INSURANCE_PLANS, MENU2_IP_AUTO_INSURANCE);
        doClickRandomText(AUTO_PLANS);
        if (Math.random() < 0.2) {
            doClickText(DOWNLOAD);
        }
        navigateTo(MENU1_ABOUT_US, null);
//		navigateTo(MENU1_CONTACT_US, null);
//		fillOutContactForm((int)(Math.random()*1e6));
//		sleep(2000);
    }
}
