package com.liferay.sales.selenium.uow;

import com.liferay.sales.selenium.api.DriverInitializer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * This class represents a clickpath that views the homepage
 */
public class UoWClickpathABTest extends UoWBaseClickpath {
    private static int countControl;
    private static int countControlFired;
    private static int countVariant;
    private static int countVariantFired;

    public UoWClickpathABTest(DriverInitializer di, String baseUrl) {
        super(di, baseUrl, new UoWUTMGenerator());
    }

    public void run(String username, String password) {
        resizeBrowser(1536, 835);
        deleteAllCookies();

        if (username != null & password != null) {
            log("Running clickpath with " + username);
            doLogin(username, password);
        } else {
            log("Running clickpath with an anonymous user");
        }

        final String pageUrl = "/news";
        doGoTo(pageUrl);

        sleep(3000);

        final String newsCardXPath = "//div[contains(concat(' ', normalize-space(@class), ' '), ' component-card ')]";
        final List<WebElement> newsCards = getElementsByXPath(newsCardXPath);
        if (newsCards.size() > 0) {
            final WebElement newsCard = pickRandom(newsCards);
            scrollTo(newsCard);
            sleep(2000, false);
            final WebElement readMoreLink = newsCard.findElement(By.tagName("a"));
            if (readMoreLink != null) {
                final String linkText = readMoreLink.getText();
                boolean fire = false;

                final double rand = Math.random();
                log("rand : " + rand);

                if ("Read more...".equalsIgnoreCase(linkText)) /* VARIANT */ {
                    log("VARIANT : \'" + linkText + "\"");
                    fire = rand > 0.2;
                    countVariant++;
                    countVariantFired = countVariantFired + (fire ? 1 : 0);
                } else if ("Click here to learn more".equalsIgnoreCase(linkText)) /* CONTROL */ {
                    log("CONTROL : \"" + linkText + "\"");
                    fire = rand > 0.3;
                    countControl++;
                    countControlFired = countControlFired + (fire ? 1 : 0);
                } else {
                    log("Link text is not as expected : " + linkText);
                }

                if (fire) {
                    log("firing A/B Test action");
                    doClick(readMoreLink);
                } else {
                    log("NOP for A/B Test action");
                }
            }
        }

        System.out.println("Stats: Control " + countControl + "(" + countControlFired + "), "
                + "Variant " + countVariant + "(" + countVariantFired + ")");

        sleep(2000);
        quit();
    }
}
