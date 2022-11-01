package com.liferay.sales.selenium.liferaycity;
import com.liferay.sales.selenium.chrome.ChromeDriverInitializer;

import java.util.HashMap;

public class LiferayCity {
	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
		
		HashMap<String,String> cityUsers = new HashMap<String, String>();
		cityUsers.put("wwilliams", "liferay$"); 
		cityUsers.put("ssmith", "liferay$");   

		LiferayCityClickpath1 path1 = new LiferayCityClickpath1(new ChromeDriverInitializer(), "https://webserver-lctcity-prd.lfr.cloud/");
		
		path1.run(cityUsers);
	}
}
