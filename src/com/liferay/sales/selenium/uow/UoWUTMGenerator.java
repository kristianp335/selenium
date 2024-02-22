package com.liferay.sales.selenium.uow;

import com.liferay.sales.selenium.util.UTMGenerator;

public class UoWUTMGenerator extends UTMGenerator {
    private static final String[] CAMPAIGNS = new String[]{"clearing2024", "spring2024"};
    private static final String[] CONTENT = new String[]{null}; // Do not use
    private static final String[] EMAIL_SOURCES = new String[]{"march-open-day", "lifelong-learning"};
    private static final String[] MEDIUMS = null; // Usw the default
    private static final String[] SOCIAL_SOURCES = new String[]{"facebook", "linkedin", "instagram", "x", "snapchat"};
    private static final String[] TERMS = new String[]{null}; // Do not use

    public UoWUTMGenerator() {
        super(CAMPAIGNS, CONTENT, MEDIUMS, null, TERMS);
    }

    @Override
    public String decorateUrl(String url) {
        if (Math.random() > .3 && !url.contains("utm_medium" )) {
            StringBuilder urlBuilder = new StringBuilder(url);

            final String medium = pickRandom(this.mediums);
            if (url.contains("?" )) {
                urlBuilder.append("&" );
            } else {
                urlBuilder.append("?" );
            }
            urlBuilder.append("utm_medium=" );
            urlBuilder.append(medium);

            String source = null;
            if (medium.equals("organicsocial" ))
                source = pickRandom(SOCIAL_SOURCES);
            else if (medium.equals("email" ))
                source = pickRandom(EMAIL_SOURCES);

            if (source != null) {
                urlBuilder.append("&" );
                urlBuilder.append("utm_source=" );
                urlBuilder.append(source);
            }

            final String campaign = pickRandom(this.campaigns);
            if (campaign != null) {
                urlBuilder.append("&" );
                urlBuilder.append("utm_campaign=" );
                urlBuilder.append(campaign);
            }

            final String content = pickRandom(this.content);
            if (content != null) {
                urlBuilder.append("&" );
                urlBuilder.append("utm_content=" );
                urlBuilder.append(content);
            }

            final String term = pickRandom(this.terms);
            if (term != null) {
                urlBuilder.append("&" );
                urlBuilder.append("utm_term=" );
                urlBuilder.append(term);
            }
            return urlBuilder.toString();
        }
        return url;
    }
}
