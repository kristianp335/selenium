package com.liferay.sales.selenium.insurance;

import com.liferay.sales.selenium.api.DriverInitializer;

public class LiferayInsuranceClickpath2 extends LiferayInsuranceBaseClickpath {

    public LiferayInsuranceClickpath2(DriverInitializer di, String baseUrl) {
        super(di, baseUrl);
    }

    @Override
    public void run(String username, String password) {
        double startPageRandomization = Math.random();
        if (startPageRandomization < 0.1 && REFERRERS.length > 0) {
            doGoTo(pickRandom(REFERRERS));
            doClickText("IDC Demo");
        } else {
            doGoTo(baseUrl);
        }
        selectLanguage(pickRandom(LANGUAGES));

        navigateTo(MENU1_INSURANCE_PLANS, MENU2_IP_HEALTH_INSURANCE);
        doClickRandomText(HEALTH_PLANS);
        if (Math.random() < 0.3) {
            doClickText(DOWNLOAD);
        }
        navigateTo(MENU1_ABOUT_US, null);
//		navigateTo(MENU1_CONTACT_US, null);
//		fillOutContactForm((int)(Math.random()*1e6));
//		sleep(2000);
    }
}
