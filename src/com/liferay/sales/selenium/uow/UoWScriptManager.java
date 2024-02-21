package com.liferay.sales.selenium.uow;

import com.liferay.sales.selenium.api.ClickpathBase;
import com.liferay.sales.selenium.api.ScriptManager;
import com.liferay.sales.selenium.chrome.ChromeDriverInitializer;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UoWScriptManager extends ScriptManager {
    public static void main(String[] args) {
        try {
            doIt();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void doIt() {
// Before starting the script, make adjustments in this top block
// to reflect the behavior that you need.
// Inspect the clickpaths.
// If you run the A/B-Test, note that you'll have to prepare the
// content according to the documentation
// https://docs.google.com/document/d/1h2E7UUt_i3yqwge25Pd8YXOLHt3Jujz4VBAdgHSeKQw/edit#heading=h.w4lf8kpcller

        System.setProperty("webdriver.chrome.driver", "/opt/homebrew/bin/chromedriver");

        String baseUrl = "https://webserver-lctwarwick-prd.lfr.cloud/";
        String[] arguments = new String[]{"--remote-allow-origins=*"};
        int repeats = 1;

        ClickpathBase[] paths = new ClickpathBase[]{
                new UoWClickpath1(new ChromeDriverInitializer(arguments), baseUrl)
        };

// Typically, nothing more to "configure" below this line.
// Anything that you need to customize your scripts is above.

        System.out.println("Running " + paths.length + " clickpaths for " + repeats + " times");

        long start = System.currentTimeMillis();
        List<String> log = new LinkedList<>();

        for (int i = 1; i <= repeats; i++) {
            long thisStart = System.currentTimeMillis();
            int pos = (int) (Math.random() * paths.length);
            ClickpathBase path = paths[pos];
            System.out.println("Number of failures so far:" + log.size());
            System.out.println("#" + i + "/" + repeats + ": Running with path "
                    + pos + " (" + path.getClass().getSimpleName() + ", using "
                    + path.getDriver().getClass().getSimpleName()
                    + ")");
            path.setDefaultSleep(4000);
            try {
                path.run(null, null);
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String tstamp = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss", Locale.ENGLISH).format(LocalDateTime.now());
                log.add(
                        tstamp + "\n" +
                                "" + i + ", path" + pos + "]\n" +
                                e.getClass().getName() + " " +
                                e.getMessage() + "\n" +
                                sw
                );
                try {
                    path.writePageToDisk("ERROR", "" + i);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
            path.quit();
            long now = System.currentTimeMillis();
            long runtime = now - start;
            long expectedTimeSpan = (runtime / (i + 1)) * repeats;
            long thisTimeSpan = now - thisStart;
            Date expectedEnd = new Date(start + expectedTimeSpan);
            System.out.println("Runtime (current/average) in sec: " + (thisTimeSpan / 1000L) + "/" + (runtime / ((i + 1) * 1000L)));
            System.out.println("Expected remaining run time:      " + expectedEnd);
            System.out.println("Current time:                     " + new Date());

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
}
