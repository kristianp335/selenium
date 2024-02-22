package com.liferay.sales.selenium.util;

public class UTMGenerator {
    private static final String[] DEFAULT_CAMPAIGNS = new String[]{"pink_monday", "green_tuesday", "red_wednesday", "blue_thursday", "black_friday", "yellow_saturday", "white_sunday"};
    private static final String[] DEFAULT_CONTENT = new String[]{"random", "made_up", "unknown", "artificial"};
    private static final String[] DEFAULT_MEDIUMS = new String[]{"organicsocial", "affiliate", "email", "referral", "cpc", "display"};
    private static final String[] DEFAULT_SOURCES = new String[]{"example.com", "liferay.com", "test.example.com", "example.de"};
    private static final String[] DEFAULT_TERMS = new String[]{"term1", "term2", "term3", "term4", "term5"};
    protected final String[] campaigns;
    protected final String[] content;
    protected final String[] mediums;
    protected final String[] sources;
    protected final String[] terms;

    public UTMGenerator() {
        campaigns = DEFAULT_CAMPAIGNS;
        content = DEFAULT_CONTENT;
        mediums = DEFAULT_MEDIUMS;
        sources = DEFAULT_SOURCES;
        terms = DEFAULT_TERMS;
    }

    public UTMGenerator(String[] campaigns, String[] content, String[] mediums, String[] sources, String[] terms) {
        this.campaigns = campaigns == null ? DEFAULT_CAMPAIGNS : campaigns;
        this.content = content == null ? DEFAULT_CONTENT : content;
        this.mediums = mediums == null ? DEFAULT_MEDIUMS : mediums;
        this.sources = sources == null ? DEFAULT_SOURCES : sources;
        this.terms = terms == null ? DEFAULT_TERMS : terms;
    }

    public String decorateUrl(String url) {
        if (Math.random() > .3 && !url.contains("utm_source" )) {
            if (url.contains("?" )) {
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

    protected String pickRandom(String[] strings) {
        int pos = (int) (Math.random() * strings.length);
        return strings[pos];
    }
}
