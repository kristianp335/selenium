package com.liferay.sales.selenium.liferaycity;
import com.liferay.sales.selenium.firefox.FirefoxDriverInitializer;

import java.util.HashMap;

public class LiferayCity {
	public static void main(String[] args) {
		LiferayCityClickpath1 path1 = null;
		try {
			System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
			
			HashMap<String,String> cityUsers = new HashMap<String, String>();
			cityUsers.put("wwilliams", "liferay$"); 
			cityUsers.put("ssmith", "liferay$");   
	
			path1 = new LiferayCityClickpath1(new FirefoxDriverInitializer(), "https://webserver-lctcity-prd.lfr.cloud/");
			path1.setDefaultSleep(3000);
			path1.run(cityUsers);
		} catch(Exception e) {
			System.out.println("-------");
			System.out.println(e.getClass().getName());
			System.out.println(e.getMessage());
			e.printStackTrace();
			if(path1 != null) path1.quit();
		}
	}
}
