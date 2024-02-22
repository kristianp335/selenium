package com.liferay.sales.selenium.insurance;

import com.liferay.sales.selenium.api.DriverInitializer;

public class LiferayInsuranceClickpath3 extends LiferayInsuranceBaseClickpath {

    public LiferayInsuranceClickpath3(DriverInitializer di, String baseUrl) {
        super(di, baseUrl);
    }

    @Override
    public void run(String username, String password) {
        double startPageRandomization = Math.random();
        if (startPageRandomization < 0.1 && REFERRERS.length > 0) {
            doGoTo(pickRandom(REFERRERS));
            doClickText("IDC Demo" );
        } else {
            doGoTo(baseUrl);
        }
        selectLanguage(pickRandom(LANGUAGES));

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
