package com.liferay.sales.selenium.uow;

import com.liferay.sales.selenium.api.DriverInitializer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * This class represents a clickpath that plays the video of a randomly selected researcher
 */
public class UoWClickpath4 extends UoWBaseClickpath {
    public UoWClickpath4(DriverInitializer di, String baseUrl) {
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

        final String pageUrl = "/research";
        doGoTo(pageUrl);

        final String researchCardXPath = "//div[contains(concat(' ', normalize-space(@class), ' '), ' video-container ')]";
        markByXPath(researchCardXPath);
        final List<WebElement> videoContainers = getElementsByXPath(researchCardXPath);
        if (videoContainers.size() > 0) {
            final WebElement videoContainer = pickRandom(videoContainers);
            scrollTo(videoContainer);

            final WebElement youTubeIframe = videoContainer.findElement(By.tagName("iframe" ));
            doClick(youTubeIframe);

            sleep(10000);
        }

        quit();
    }
}
