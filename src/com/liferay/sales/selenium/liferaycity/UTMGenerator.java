package com.liferay.sales.selenium.liferaycity;

public class UTMGenerator {
	public UTMGenerator() {
	}
	
	public String decorateUrl(String url) {
		if(Math.random()>.3 && url.indexOf("utm_source") == -1) {
			if(url.contains("?")) {
				url += "&";
			} else {
				url += "?";
			}
			url += "utm_source=" + pickRandom(sources)
			    + "&utm_medium=" + pickRandom(mediums)
			    + "&utm_campaign=" + pickRandom(campaigns)
			    + "&utm_term=" + pickRandom(terms)
			    + "&utm_content=" + pickRandom(content);
		}
		return url;
	}
	
	public String pickRandom(String[] strings) {
		int pos = (int) (Math.random() * strings.length);
		return strings[pos];
	}	
	
	private String[] sources = new String[] {"example.com", "liferay.com", "test.example.com", "example.de"};
	private String[] mediums = new String[] {"organicsocial", "email", "affiliate", "email", "referral", "cpc", "display"};
	private String[] campaigns = new String[] {"pink_monday", "green_tuesday", "red_wednesday", "blue_thursday", "black_friday", "yellow_saturday", "white_sunday"};
	private String[] terms   = new String[] {"term1", "term2", "term3", "term4", "term5"};
	private String[] content = new String[] {"random", "made_up", "unknown", "artificial"};
}
