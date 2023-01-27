package com.liferay.sales.selenium.liferaycity;
import com.liferay.sales.selenium.api.ClickpathBase;
import com.liferay.sales.selenium.chrome.ChromeDriverInitializer;
import com.liferay.sales.selenium.firefox.FirefoxDriverInitializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class LiferayCity {
	public static void main(String[] args) {
		ClickpathBase path = null;
//			System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
			
		String[][] cityUsers = readUserCSV("/home/olaf/cityUsers.csv");
		
		String baseUrl = "https://webserver-lctcitysite-prd.lfr.cloud/";
		String[] arguments = new String[] { "--headless" };
//		String[] arguments = new String[] {  };
		ClickpathBase[] paths = new ClickpathBase[] {
				 new LiferayCityClickpath1(new ChromeDriverInitializer(arguments), baseUrl)
				,new LiferayCityClickpath2(new ChromeDriverInitializer(arguments), baseUrl)
				,new LiferayCityClickpath3(new ChromeDriverInitializer(arguments), baseUrl)
				,new LiferayCityClickpath4(new ChromeDriverInitializer(arguments), baseUrl)
//				,new LiferayCityClickpathABTest(new ChromeDriverInitializer(args), baseUrl)
//				,new LiferayCityClickpathABTest(new ChromeDriverInitializer(args), baseUrl)
//				,new LiferayCityClickpathABTest(new ChromeDriverInitializer(args), baseUrl)
				,new LiferayCityClickpath1(new FirefoxDriverInitializer(arguments), baseUrl)
				,new LiferayCityClickpath2(new FirefoxDriverInitializer(arguments), baseUrl)
				,new LiferayCityClickpath3(new FirefoxDriverInitializer(arguments), baseUrl)
				,new LiferayCityClickpath4(new FirefoxDriverInitializer(arguments), baseUrl)
//				,new LiferayCityClickpathABTest(new FirefoxDriverInitializer(args), baseUrl)
//				,new LiferayCityClickpathABTest(new FirefoxDriverInitializer(args), baseUrl)
//				,new LiferayCityClickpathABTest(new FirefoxDriverInitializer(args), baseUrl)
		};

//		paths = new ClickpathBase[] {
//				new LiferayCityClickpath4(new ChromeDriverInitializer(), baseUrl)
//		};
		
		
		LinkedList<String> log = new LinkedList<String>();

		for(int i=0; i<100; i++) {
			int pos = (int)(Math.random()*cityUsers.length);
			String[] user = cityUsers[pos];
			pos = (int)(Math.random()*paths.length);
			path = paths[pos];
			System.out.println("Number of failures so far:" + log.size());
			System.out.println("#" + i + ": Running user " + user[0] + " with path " + pos + " (" + path.getClass().getSimpleName() + ")" );
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
			}
			if(path != null) {
				path.quit();
				path = null;
			}
			System.out.println();
		}
		
		System.out.println("==================================================");
		System.out.println("End of run");
		System.out.println("Failed attempts: " + log.size());
		for (String string : log) {
			System.out.println(string);
			System.out.println("---------------------------------------------");
		}
	}
	
	/**
	 * Read a stupidly simple CSV format: No title, content is just 
	 * rows with "name,password" (comma-separated, no escaping, no quotes)
	 * Luxury trimming done to individual entries without extra charge.
	 * 
	 * @param filename
	 * @return
	 */
	public static String[][] readUserCSV(String filename) {
		ArrayList<String[]> content = new ArrayList<String[]>();
		try (Scanner scanner = new Scanner(new File(filename))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				try (Scanner rowScanner = new Scanner(line)) {
					ArrayList<String> row = new ArrayList<String>(2);
					rowScanner.useDelimiter(",");
					while (rowScanner.hasNext()) {
						row.add(rowScanner.next().trim());
					}
					content.add(row.toArray(new String[row.size()]));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return (String[][]) content.toArray(new String[content.size()][]);
	}	
	
}
