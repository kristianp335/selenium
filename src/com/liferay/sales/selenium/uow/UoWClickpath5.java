package com.liferay.sales.selenium.uow;

import com.liferay.sales.selenium.api.DriverInitializer;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a clickpath that searches for and randomly selects a news article
 */
public class UoWClickpath5 extends UoWBaseClickpath {

    private static final Map<String, String> CATEGORY_MAP = new HashMap<>() {{
        put("Careers", "40878");
        put("Fair", "40884");
        put("Open Day", "40872");
        put("Press Release", "40869");
        put("Science", "40881");
        put("Virtual Open Day", "40875");
    }};
    private static final String FILTER_CATEGORY = "filter_category_45412";
    private final String[] SEARCH_TERMS = new String[]{
            "warwick", "science", "career", "\"student development\"", "employment"
    };

    public UoWClickpath5(DriverInitializer di, String baseUrl) {
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

        sleep(2000, false);

        int option = pickRandom(new Integer[]{1, 2, 3, 4});
        log("Running option " + option);
        switch (option) {
            case 1:
                option1();
                break;
            case 2:
                option2();
                break;
            case 3:
                option3();
                break;
            default:
                option4();
                break;
        }

        sleep(15000);
        quit();
    }

    private void option1() {
        final String newsCardXPath = "//div[contains(concat(' ', normalize-space(@class), ' '), ' component-card ')]";
        final List<WebElement> newsCards = getElementsByXPath(newsCardXPath);
        if (newsCards.size() > 0) {
            final WebElement newsCard = pickRandom(newsCards);
            scrollTo(newsCard);
            sleep(2000, false);
            final WebElement readMoreLink = newsCard.findElement(By.tagName("a"));
            if (readMoreLink != null) {
                doClick(readMoreLink);
                scrollToFooter();
            }
        }
    }

    private void option2() {
        final String carouselItemsXPath = "//div[contains(concat(' ', normalize-space(@class), ' '), ' carousel-item ')]";
        final List<WebElement> carouselItems = getElementsByXPath(carouselItemsXPath);
        if (carouselItems.size() > 0) {
            final WebElement carouselItem = pickRandom(carouselItems);
            if (carouselItem != null) {
                final WebElement readMoreButton = carouselItem.findElement(By.tagName("a"));
                if (readMoreButton != null) {
                    try {
                        doClick(readMoreButton);
                        scrollToFooter();
                    } catch (ElementNotInteractableException e) {
                        log("Unable to click the Read More button");
                        final String hint = getClass().getSimpleName() + "-option2";
                        writePageToDisk("ERROR", hint);
                        takeScreenshot("ERROR", hint);
                    }
                }
            }
        }
    }

    private void option3() {
        final String category = pickRandom(CATEGORY_MAP.keySet().toArray(new String[0]));
        final String categoryId = CATEGORY_MAP.get(category);
        log(String.format("Selecting category %s [%s]", category, categoryId));
        StringBuilder urlBuilder = new StringBuilder("/news");
        urlBuilder.append("?");
        urlBuilder.append(FILTER_CATEGORY);
        urlBuilder.append("=");
        urlBuilder.append(categoryId);
        final String url = urlBuilder.toString();
        log("Navigating to " + url);
        doGoTo(url);
        sleep(2000, false);
        option1();
    }

    private void option4() {
        final WebElement keywordSearch = getElementByCSS("input.form-control.form-control-sm.input-group-inset.input-group-inset-after");
        if (keywordSearch != null) {
            scrollTo(keywordSearch);
            type(keywordSearch, pickRandom(SEARCH_TERMS));
            keywordSearch.sendKeys(Keys.ENTER);
            sleep(2000, false);

            boolean clearFilter = pickRandom(new Boolean[]{true, false});
            if (clearFilter) {
                log("Clearing filter");
                clearFilter();
            }
            option1();
        }
    }

    private void clearFilter() {
        final WebElement clearFilter = getElementByXPath("//button[contains(@id, '_removeAllFilters')]");
        if (clearFilter != null) {
            doClick(clearFilter);
            sleep(1000, false);
        }
    }
}
