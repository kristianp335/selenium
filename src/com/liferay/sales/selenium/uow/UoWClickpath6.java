package com.liferay.sales.selenium.uow;

import com.liferay.sales.selenium.api.DriverInitializer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Random;

/**
 * This class represents a clickpath that searches for and randomly selects a news article
 */
public class UoWClickpath6 extends UoWBaseClickpath {
    private static final Random rand = new Random();

    public UoWClickpath6(DriverInitializer di, String baseUrl) {
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

        final String pageUrl = "/";
        doGoTo(pageUrl);

        sleep(2000, false);

        int option = pickRandom(new Integer[]{1, 2});
        log("Running option " + option);
        switch (option) {
            case 1:
                option1();
                break;
            default:
                option2();
                break;
        }

        sleep(10000);
        quit();
    }

    private void option1() {
        final int blogCount = 12;
        final int blogsPerPage = 3;
        final int selectedBlog = rand.nextInt(blogCount) + 1;
        int pageIndex = selectedBlog / blogsPerPage;
        int blogPositionOnPage = selectedBlog % blogsPerPage;
        // Allow for MOD zero
        if (blogPositionOnPage == 0) {
            pageIndex--;
            blogPositionOnPage = blogsPerPage;
        }

        log("selectedBlog : " + selectedBlog);
        log("pageIndex : " + pageIndex);
        log("blogPositionOnPage : " + blogPositionOnPage);

        for (int page = 0; page < pageIndex; page++) {
            final WebElement nextPageLink = getElementByCSS("button.next");
            nextPageLink.click();
            sleep(1000, false);
        }

        final List<WebElement> blogCards = getElementsByCSS("div.component-card");
        final WebElement blogCard = blogCards.get(blogPositionOnPage - 1);
        selectBlog(blogCard);
    }

    private void option2() {
        option1();

        final List<WebElement> blogCards = getElementsByCSS("div.component-card");
        final WebElement blogCard = pickRandom(blogCards);
        selectBlog(blogCard);
    }

    private void selectBlog(WebElement blogCard) {
        scrollTo(blogCard);

        final WebElement blogTitle = blogCard.findElement(By.cssSelector("span[data-lfr-editable-id='02-title']"));
        log("Selecting blog: " + blogTitle.getText());
        sleep(1000, false);

        final WebElement readMoreLink = blogCard.findElement(By.tagName("a"));
        if (readMoreLink != null) {
            doClick(readMoreLink);
            sleep(5000);
            log("Scrolling to bottom of page");
            scrollToFooter();
        }
    }
}
