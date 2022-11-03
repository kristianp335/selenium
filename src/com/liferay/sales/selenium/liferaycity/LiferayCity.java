package com.liferay.sales.selenium.liferaycity;
import com.liferay.sales.selenium.api.ClickpathBase;
import com.liferay.sales.selenium.chrome.ChromeDriverInitializer;
import com.liferay.sales.selenium.firefox.FirefoxDriverInitializer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;

public class LiferayCity {
	public static void main(String[] args) {
		ClickpathBase path = null;
//			System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
			
		String[][] cityUsers = {
			{"wwilliams", "liferay$"}, 
			{"ssmith",    "liferay$"},
			{"aalamo",    "liferay$"},
			{"ijohnson",  "liferay$"},  
			{"jjones",    "liferay$"},
			{"mqueen",    "liferay$"},
			{"gyoung",    "liferay$"}
		};

		String baseUrl = "https://webserver-lctcity-prd.lfr.cloud/";
		
		ClickpathBase[] paths = new ClickpathBase[] {
				new LiferayCityClickpath1(new ChromeDriverInitializer(), baseUrl),
				new LiferayCityClickpath2(new ChromeDriverInitializer(), baseUrl),
				new LiferayCityClickpath3(new ChromeDriverInitializer(), baseUrl),
				new LiferayCityClickpath1(new FirefoxDriverInitializer(), baseUrl),
				new LiferayCityClickpath2(new FirefoxDriverInitializer(), baseUrl),
				new LiferayCityClickpath3(new FirefoxDriverInitializer(), baseUrl),
		};

		LinkedList<String> log = new LinkedList<String>();

		for(int i=0; i<100; i++) {
			int pos = (int)(Math.random()*cityUsers.length);
			String[] user = cityUsers[pos];
			pos = (int)(Math.random()*paths.length);
			path = paths[pos];
			System.out.println("Number of failures so far:" + log.size());
			System.out.println("#" + i + ": Running user " + user[0] + " with path " + pos);
			path.setDefaultSleep(4000);
			try {
				path.run(user[0], user[1]);
			} catch(Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				log.add(
					""+i+", ["+ user[0] + ", path" + pos + "]\n" +
					e.getClass().getName() + " " +
					e.getMessage() + "\n" + 
					sw.toString()
				);

				if(path != null) path.quit();
			}
		}
		
		System.out.println("==================================================");
		System.out.println("End of run");
		System.out.println("Failed attempts:");
		for (String string : log) {
			System.out.println(string);
			System.out.println("---------------------------------------------");
		}
	}
}
