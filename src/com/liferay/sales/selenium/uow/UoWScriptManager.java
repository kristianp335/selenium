package com.liferay.sales.selenium.uow;

import com.liferay.sales.selenium.api.ClickpathBase;
import com.liferay.sales.selenium.api.ScriptManager;
import com.liferay.sales.selenium.api.WebDriverType;
import com.liferay.sales.selenium.chrome.ChromeDriverInitializer;
import com.liferay.sales.selenium.firefox.FirefoxDriverInitializer;
import com.liferay.sales.selenium.util.StreamGobbler;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UoWScriptManager extends ScriptManager {
    private static final String[] ECHO_COMMAND = new String[]{"echo", "---PLACEHOLDER---"};
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(1);
    private static final boolean IS_MAC = System.getProperty("os.name" ).equalsIgnoreCase("Mac OS X" );
    private static final String[] VPN_CONNECT_COMMAND = new String[]{"osascript", "-e", "'tell application \"/Applications/Tunnelblick.app\"", "-e", "connect", "---PLACEHOLDER---", "-e", "end tell"};
    private static final String[] VPN_DISCONNECT_COMMAND = new String[]{"osascript", "-e", "'tell application \"/Applications/Tunnelblick.app\"", "-e", "disconnect", "---PLACEHOLDER---", "-e", "end tell"};
    private static final String[] vpnNames = new String[]{
            "nordvpn-au",
            "nordvpn-br",
            "nordvpn-de",
            "nordvpn-fr",
            "nordvpn-us",
            "purevpn-au",
            "purevpn-br",
            "purevpn-de",
            "purevpn-fr",
            "purevpn-us",
    };

    public static void main(String[] args) {
        try {
            String baseUrl = "https://webserver-lctwarwick-prd.lfr.cloud/";
            final Options options = new Options();

            final Option baseUrlOption = new Option("b", "base-url", false, "The base Liferay DXP URL on which the cycles will be run. The default is " + baseUrl);
            baseUrlOption.setRequired(false);
            options.addOption(baseUrlOption);

            final Option anonymousCyclesOption = new Option("a", "anonymous-cycles", true, "The number of anonymous cycles to run. The default is 10" );
            anonymousCyclesOption.setRequired(false);
            options.addOption(anonymousCyclesOption);

            final Option knownUserCyclesOption = new Option("k", "known-user-cycles", true, "The number of known user cycles to run. The default is 0" );
            knownUserCyclesOption.setRequired(false);
            options.addOption(knownUserCyclesOption);

            final Option userCsvOption = new Option("u", "users", true, "The pathname of the CSV containing the user accounts for the known user cycles" );
            userCsvOption.setRequired(false);
            options.addOption(userCsvOption);

            final Option useVpnOption = new Option(null, "use-vpn", false, "Runs the script over a VPN. The default is not to use VPN" );
            useVpnOption.setRequired(false);
            options.addOption(useVpnOption);

            final Option webDriverOption = new Option("d", "driver", true, "The web driver to use, i.e. chrome or firefox. The default is chrome" );
            webDriverOption.setRequired(false);
            options.addOption(webDriverOption);

            final Option webDriverPathOption = new Option(null, "driver-path", true, "The path of the web driver. The default is /opt/homebrew/bin/chromedriver" );
            webDriverPathOption.setRequired(false);
            options.addOption(webDriverPathOption);

            final CommandLineParser parser = new DefaultParser();
            final HelpFormatter formatter = new HelpFormatter();

            CommandLine cmd = null;
            try {
                cmd = parser.parse(options, args);
            } catch (ParseException e) {
                System.out.println(e.getMessage());
                formatter.printHelp("utility-name", options);

                System.exit(1);
            }

            final boolean useVpn = cmd.hasOption(useVpnOption);

            int anonymousCycles;
            try {
                anonymousCycles = cmd.hasOption(anonymousCyclesOption) ? Integer.parseInt(cmd.getOptionValue(anonymousCyclesOption)) : 10;
            } catch (NumberFormatException e) {
                anonymousCycles = 10;
            }

            int knownUserCycles;
            try {
                knownUserCycles = cmd.hasOption(knownUserCyclesOption) ? Integer.parseInt(cmd.getOptionValue(knownUserCyclesOption)) : 0;
            } catch (NumberFormatException e) {
                knownUserCycles = 0;
            }

            final String usersCSVPathname = cmd.hasOption(userCsvOption) ? cmd.getOptionValue(userCsvOption) : "./users.csv";

            if (cmd.hasOption(baseUrlOption)) {
                baseUrl = cmd.getOptionValue(baseUrlOption);
            }

            WebDriverType webDriverType = WebDriverType.CHROME;
            if (cmd.hasOption(webDriverOption)) {
                try {
                    webDriverType = WebDriverType.valueOf(cmd.getOptionValue(webDriverOption));
                } catch (IllegalArgumentException e) {
                    System.err.println("Unknown web driver type: " + cmd.getOptionValue(webDriverOption));
                }
            }

            String webDriverPath = "/opt/homebrew/bin/chromedriver";
            if (cmd.hasOption(webDriverPathOption)) {
                webDriverPath = cmd.getOptionValue(webDriverPathOption);
            }

            System.out.println("The base URL is " + baseUrl);
            System.out.println("Running " + anonymousCycles + " anonymous cycles" );
            System.out.println("Running " + knownUserCycles + " known user cycles" );
            if (usersCSVPathname != null) {
                System.out.println("User accounts in " + usersCSVPathname);
            }
            System.out.println(useVpn ? "The VPN will be used" : "The VPN will NOT be used" );
            System.out.println("Using " + webDriverType);

            String[][] users = null;
            if (usersCSVPathname != null) {
                if (doesFileExist(usersCSVPathname)) {
                    users = readUserCSV(usersCSVPathname);

                    if (users != null)
                        System.out.println("The number of users for the known user cycles is " + users.length);
                }
            }

            switch (webDriverType) {
                case CHROME:
                    System.setProperty("webdriver.chrome.driver", webDriverPath);
                    break;
                case FIREFOX:
                    System.setProperty("webdriver.gecko.driver", webDriverPath);
                    break;
                default:
                    System.err.println("Unknown web driver type" );
                    break;
            }

            doIt(webDriverType, baseUrl, anonymousCycles, knownUserCycles, users, useVpn);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            log("Shutting down process executor" );
            EXECUTOR_SERVICE.shutdown();
        }
    }

    public static void doIt(final WebDriverType webDriverType, final String baseUrl, final int anonymousCycles, final int knownUserCycles, final String[][] users, final boolean useVpn) {
// Before starting the script, make adjustments in this top block
// to reflect the behavior that you need.
// Inspect the clickpaths.
// If you run the A/B-Test, note that you'll have to prepare the
// content according to the documentation
// https://docs.google.com/document/d/1h2E7UUt_i3yqwge25Pd8YXOLHt3Jujz4VBAdgHSeKQw/edit#heading=h.w4lf8kpcller

        final String[] arguments = new String[]{"--remote-allow-origins=*"};

        final ClickpathBase[] paths;

        switch (webDriverType) {
            case FIREFOX:
                paths = new ClickpathBase[]{
                        new UoWClickpath1(new FirefoxDriverInitializer(arguments), baseUrl),
                        new UoWClickpath2(new FirefoxDriverInitializer(arguments), baseUrl),
                        new UoWClickpath3(new FirefoxDriverInitializer(arguments), baseUrl),
                        new UoWClickpath4(new FirefoxDriverInitializer(arguments), baseUrl),
                        new UoWClickpath5(new FirefoxDriverInitializer(arguments), baseUrl)
                };
                break;
            case CHROME:
                paths = new ClickpathBase[]{
                        new UoWClickpath1(new ChromeDriverInitializer(arguments), baseUrl),
                        new UoWClickpath2(new ChromeDriverInitializer(arguments), baseUrl),
                        new UoWClickpath3(new ChromeDriverInitializer(arguments), baseUrl),
                        new UoWClickpath4(new ChromeDriverInitializer(arguments), baseUrl),
                        new UoWClickpath5(new ChromeDriverInitializer(arguments), baseUrl)
                };

                break;
            default:
                throw new IllegalArgumentException("Unrecognised web driver type" );
        }


        final int pathCount = paths.length;
        if (pathCount == 0) {
            log("No active click paths. There is nothing to do" );
            return;
        }

        final int userCount = users != null ? users.length : 0;
        if (knownUserCycles > 0 && userCount == 0) {
            log("No user accounts. Unable to run click paths" );
            return;
        }

// Typically, nothing more to "configure" below this line.
// Anything that you need to customize your scripts is above.

        final int repeats = knownUserCycles + anonymousCycles;
        log("Running " + paths.length + " clickpaths for " + repeats + " times" );

        long start = System.currentTimeMillis();
        final List<String> log = new LinkedList<>();

        for (int i = 1; i <= anonymousCycles; i++) {
            int pathIndex = (int) (Math.random() * paths.length);
            int vpnIndex = (int) (Math.random() * vpnNames.length);
            String vpnName = vpnNames[vpnIndex];
            if (useVpn) {
                try {
                    vpnConnect(vpnName, null);
                } catch (IOException e) {
                    log(e.getMessage());
                }
            }
            ClickpathBase path = paths[pathIndex];
            userSequence(path, null, null, pathIndex, i, anonymousCycles, start, log);
            if (useVpn) {
                try {
                    vpnDisconnect(vpnName);
                } catch (IOException e) {
                    log(e.getMessage());
                }
            }
        }

        for (int i = 1; i <= knownUserCycles; i++) {
            final int pathIndex = (int) (Math.random() * paths.length);
            final ClickpathBase path = paths[pathIndex];

            final int userIndex = (int) (Math.random() * users.length);
            final String[] user = users[userIndex];

            final int vpnIndex = vpnNames.length % (userIndex + 1);
            final String vpnName = vpnNames[vpnIndex];

            if (useVpn) {
                try {
                    vpnConnect(vpnName, null);
                } catch (IOException e) {
                    log(e.getMessage());
                }
            }

            userSequence(path, user[0], user[1], pathIndex, i, knownUserCycles, start, log);

            if (useVpn) {
                try {
                    vpnDisconnect(vpnName);
                } catch (IOException e) {
                    log(e.getMessage());
                }
            }
        }

        log("==================================================" );
        log("End of run" );
        log("Failed attempts: " + log.size());
        for (String string : log) {
            log(string);
            log("---------------------------------------------" );
        }
    }

    private static void vpnConnect(final String vpnName, final String username) throws IOException {
        if (!IS_MAC) {
            log("This is not an OS X environment" );
            return;
        }
        log("Attempting to connect to " + vpnName + " for " + username);
        int index = indexOf(VPN_CONNECT_COMMAND, "---PLACEHOLDER---" );
        if (index >= 0) {
            VPN_CONNECT_COMMAND[index] = vpnName;
        }
        log("Executing shell command: " + String.join(" ", VPN_CONNECT_COMMAND));
        executeShellCommand(VPN_CONNECT_COMMAND);
    }

    private static void userSequence(ClickpathBase path, String username, String password, int pathIndex, int i, int repeats, long start, List<String> log) {
        long thisStart = System.currentTimeMillis();
        log("Number of failures so far:" + log.size());
        log("#" + i + "/" + repeats + ": Running with path "
                + pathIndex + " (" + path.getClass().getSimpleName() + ", using "
                + path.getDriver().getClass().getSimpleName()
                + ")" );
        path.setDefaultSleep(4000);
        try {
            path.run(username, password);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String tstamp = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss", Locale.ENGLISH).format(LocalDateTime.now());
            log.add(
                    tstamp + "\n" +
                            "" + i + ", path" + pathIndex + "]\n" +
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
        log("Runtime (current/average) in sec: " + (thisTimeSpan / 1000L) + "/" + (runtime / ((i + 1) * 1000L)));
        log("Expected remaining run time:      " + expectedEnd);
        log("Current time:                     " + new Date());
    }

    private static void vpnDisconnect(final String vpnName) throws IOException {
        if (!IS_MAC) {
            log("This is not an OS X environment" );
            return;
        }
        log("Attempting to disconnect from " + vpnName);
        int index = indexOf(VPN_DISCONNECT_COMMAND, "---PLACEHOLDER---" );
        if (index >= 0) {
            VPN_DISCONNECT_COMMAND[index] = vpnName;
        }
        log("Executing shell command: " + String.join(" ", VPN_DISCONNECT_COMMAND));
        executeShellCommand(VPN_DISCONNECT_COMMAND);
    }

    private static void executeShellCommand(String[] command) throws IOException {
        if (command == null || command.length == 0) {
            return;
        }
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(VPN_CONNECT_COMMAND);
        builder.directory(new File(System.getProperty("user.home" )));
        Process process = builder.start();
        StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), System.out::println);
        Future<?> future = EXECUTOR_SERVICE.submit(streamGobbler);
        int exitCode = 0;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            log("Unable to wait for process" );
            e.printStackTrace();
        }
        log("Command exited with " + exitCode);
    }
}
