package com.liferay.sales.selenium.uow;

import com.liferay.sales.selenium.api.DriverInitializer;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * This class represents a clickpath that searches for a course using random keywords and navigates to the first course if one is found
 */
public class UoWClickpath3 extends UoWBaseClickpath {
    private final String[] SEARCH_TERMS = new String[]{
            "science", "computer science", "economics", "LAW", "Business Management", "Chemistry", "nonsense"
    };

    public UoWClickpath3(DriverInitializer di, String baseUrl) {
        super(di, baseUrl, new UoWUTMGenerator());
    }

    public void run(String username, String password) {
        resizeBrowser(1536, 835);
        deleteAllCookies();

        if (username != null & password != null) {
            log("Running clickpath with " + username);
            doLogin(username, password);
        } else {
            log("Running clickpath with anonymously" );
        }

        final String pageUrl = "/study";
        doGoTo(pageUrl);

        final String searchBoxXPath = "//div[contains(@class, 'lfr-layout-structure-item-com-liferay-portal-search-web-search-bar-portlet-searchbarportlet') and not(contains(@class, 'mobile-search'))]//input[@data-qa-id='searchInput']";
        markByXPath(searchBoxXPath);
        final List<WebElement> searchBoxElements = getElementsByXPath(searchBoxXPath);
        if (searchBoxElements.size() > 0) {
            final WebElement searchBox = lastOf(searchBoxElements);
            type(searchBox, pickRandom(SEARCH_TERMS));
            searchBox.sendKeys(Keys.ENTER);
            sleep(2000, false);

            List<WebElement> courseButtons = getElementsByExactLinkText("View course" );
            WebElement courseButton = courseButtons.isEmpty() ? null : courseButtons.get(0);
            if (courseButton != null) {
                doClick(courseButton);
            }
            sleep(2000);
        }

        quit();
    }
}
