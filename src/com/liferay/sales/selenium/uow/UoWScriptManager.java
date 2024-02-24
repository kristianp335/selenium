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
import java.net.URISyntaxException;
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
    private static final int CONNECTION_DELAY = 15000;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(1);
    private static final boolean IS_MAC = System.getProperty("os.name").equalsIgnoreCase("Mac OS X");
    private static final String[] VPN_COMMAND = new String[]{"---OSASCRIPT---", "-e",
            "---TELL-COMMAND---", "-e", "---VPN-ACTION---", "-e",
            "end", "tell"};
    private static final String[] VPN_NAMES = new String[]{
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
        int exitCode = 0;
        try {
            final String defaultBaseUrl = "https://webserver-lctwarwick-prd.lfr.cloud/";
            final int defaultAnonymousCycles = 10;
            final int defaultKnownUserCycles = 0;
            final String defaultUsersCSVPathname = "./users.csv";
            final boolean defaultUseVpn = false;
            final WebDriverType defaultWebDriverType = WebDriverType.CHROME;
            final String defaultOsaScriptPathname = "/usr/bin/osascript";
            final String defaultTunnelblickPathname = "/Applications/Tunnelblick.app";

            CommandLine cmd = parseArguments(args, defaultBaseUrl, defaultAnonymousCycles, defaultKnownUserCycles,
                    defaultUsersCSVPathname, defaultUseVpn, defaultWebDriverType, defaultOsaScriptPathname,
                    defaultTunnelblickPathname);

            if (cmd != null) {
                String baseUrl = defaultBaseUrl;
                if (cmd.hasOption("base-url")) {
                    baseUrl = cmd.getOptionValue("base-url");
                }

                int anonymousCycles = defaultAnonymousCycles;
                try {
                    if (cmd.hasOption("anonymous-cycles")) {
                        anonymousCycles = Integer.parseInt(cmd.getOptionValue("anonymous-cycles"));
                    }
                } catch (NumberFormatException e) {
                    anonymousCycles = defaultAnonymousCycles;
                }

                int knownUserCycles = defaultKnownUserCycles;
                try {
                    if (cmd.hasOption("known-user-cycles")) {
                        knownUserCycles = Integer.parseInt(cmd.getOptionValue("known-user-cycles"));
                    }
                } catch (NumberFormatException e) {
                    knownUserCycles = defaultKnownUserCycles;
                }

                final String usersCSVPathname = cmd.hasOption("users") ? cmd.getOptionValue("users")
                        : defaultUsersCSVPathname;

                boolean useVpn = defaultUseVpn;
                if (cmd.hasOption("use-vpn")) {
                    useVpn = true;
                }

                WebDriverType webDriverType = defaultWebDriverType;
                if (cmd.hasOption("driver")) {
                    try {
                        webDriverType = searchEnum(WebDriverType.class, cmd.getOptionValue("driver"));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Unknown web driver type: " + cmd.getOptionValue("driver"));
                        throw e;
                    }
                }

                String webDriverPathname = webDriverType.getDefaultWebDriverPathname();
                String[] webDriverArguments = null;

                if (cmd.hasOption("driver-path")) {
                    webDriverPathname = cmd.getOptionValue("driver-path");
                }

                if (cmd.hasOption("driver-arguments")) {
                    String webDriverArgumentsString = cmd.getOptionValue("driver-arguments").trim();
                    webDriverArguments = webDriverArgumentsString.split(" ");
                } else {
                    webDriverArguments = webDriverType.getDefaultWebDriverArguments().split(" ");
                }

                String osaScriptPathname = defaultOsaScriptPathname;
                if (cmd.hasOption("osascript-path")) {
                    osaScriptPathname = cmd.getOptionValue("osascript-path");
                }

                String tunnelblickPathname = defaultTunnelblickPathname;
                if (cmd.hasOption("tunnelblick-path")) {
                    tunnelblickPathname = cmd.getOptionValue("tunnelblick-path");
                }

                System.out.println("The base URL is " + baseUrl);
                System.out.println("Running " + anonymousCycles + " anonymous cycles");
                System.out.println("Running " + knownUserCycles + " known user cycles");
                if (usersCSVPathname != null) {
                    System.out.println("User accounts in " + usersCSVPathname);
                }
                System.out.println(useVpn ? "The VPN will be used" : "The VPN will NOT be used");
                System.out.print("Driving " + webDriverType + " with " + webDriverPathname);
                if (webDriverArguments != null && webDriverArguments.length > 0) {
                    System.out.print(" using \"");
                    System.out.print(String.join(" ", webDriverArguments));
                    System.out.println("\"");
                } else {
                    System.out.println();
                }

                String[][] users = null;
                if (usersCSVPathname != null) {
                    if (doesFileExist(usersCSVPathname)) {
                        users = readUserCSV(usersCSVPathname);

                        if (users != null)
                            System.out.println("The number of users for the known user cycles is " + users.length);
                    }
                }

                if (users == null || users.length == 0) {
                    System.out.println("There are no user accounts. The known user cycles will be set to 0");
                    knownUserCycles = 0;
                }

                switch (webDriverType) {
                    case CHROME:
                        System.setProperty("webdriver.chrome.driver", webDriverPathname);
                        break;
                    case FIREFOX:
                        System.setProperty("webdriver.gecko.driver", webDriverPathname);
                        break;
                    default:
                        System.err.println("Unknown web driver type");
                        throw new IllegalArgumentException("Unknown web driver type");
                }

                doIt(webDriverType, webDriverArguments, baseUrl, anonymousCycles, knownUserCycles, users, useVpn,
                        osaScriptPathname, tunnelblickPathname);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            exitCode = 1;
        } finally {
            log("Shutting down process executor");
            EXECUTOR_SERVICE.shutdown();
            System.exit(exitCode);
        }
    }

    private static CommandLine parseArguments(String[] args, final String defaultBaseUrl,
                                              final int defaultAnonymousCycles,
                                              final int defaultKnownUserCycles, final String defaultUserCsvPathname, final boolean defaultUseVpn,
                                              final WebDriverType defaultWebDriverType,
                                              final String defaultOsaScriptPathname,
                                              final String defaultTunnelblickPathnameOption) throws ParseException {
        final Options options = new Options();

        final Option baseUrlOption = new Option("b", "base-url", false,
                "The base Liferay DXP URL on which the cycles will be run. The default is " + defaultBaseUrl);
        baseUrlOption.setRequired(false);
        options.addOption(baseUrlOption);

        final Option anonymousCyclesOption = new Option("a", "anonymous-cycles", true,
                "The number of anonymous cycles to run. The default is " + defaultAnonymousCycles);
        anonymousCyclesOption.setRequired(false);
        options.addOption(anonymousCyclesOption);

        final Option knownUserCyclesOption = new Option("k", "known-user-cycles", true,
                "The number of known user cycles to run. The default is " + defaultKnownUserCycles);
        knownUserCyclesOption.setRequired(false);
        options.addOption(knownUserCyclesOption);

        final Option userCsvOption = new Option("u", "users", true,
                "The pathname of the CSV containing the user accounts for the known user cycles. The default is "
                        + defaultUserCsvPathname);
        userCsvOption.setRequired(false);
        options.addOption(userCsvOption);

        final Option useVpnOption = new Option(null, "use-vpn", false,
                "Runs the script over a VPN. "
                        + (defaultUseVpn ? "The default is to use VPN" : "The default is NOT to use VPN"));
        useVpnOption.setRequired(false);
        options.addOption(useVpnOption);

        final Option webDriverOption = new Option("d", "driver", true,
                "The web driver to use, i.e. chrome or firefox. The default is "
                        + defaultWebDriverType.toString().toLowerCase());
        webDriverOption.setRequired(false);
        options.addOption(webDriverOption);

        final Option webDriverPathOption = new Option(null, "driver-path", true,
                "The pathname of the web driver. The default is " + defaultWebDriverType.getDefaultWebDriverPathname());
        webDriverPathOption.setRequired(false);
        options.addOption(webDriverPathOption);

        final Option webDriverArgumentsOption = new Option(null, "driver-arguments", true,
                "The arguments to use with the web driver. The default argumes are \""
                        + defaultWebDriverType.getDefaultWebDriverArguments() + "\"");
        webDriverArgumentsOption.setRequired(false);
        options.addOption(webDriverArgumentsOption);

        final Option osaScriptPathnameOption = new Option(null, "osascript-path", true,
                "The pathname of the osascript command "
                        + defaultOsaScriptPathname);
        osaScriptPathnameOption.setRequired(false);
        options.addOption(osaScriptPathnameOption);

        final Option tunnelblickPathnameOption = new Option(null, "tunnelblick-path", true,
                "The pathname of the Tunnelblick.app "
                        + defaultTunnelblickPathnameOption);
        tunnelblickPathnameOption.setRequired(false);
        options.addOption(tunnelblickPathnameOption);

        final Option helpOption = new Option("h", "help", false, "Displays this help information");
        helpOption.setRequired(false);
        options.addOption(helpOption);

        final CommandLineParser parser = new DefaultParser();
        final HelpFormatter formatter = new HelpFormatter();

        String utilityName = "";
        try {
            File utilityFile = new File(
                    UoWScriptManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            utilityName = "./" + utilityFile.getName();
        } catch (URISyntaxException e) {
            // do nothing
        }

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                formatter.printHelp(utilityName, options);
                return null;
            }

        } catch (ParseException e) {
            formatter.printHelp(utilityName, options);
            throw e;
        }
        return cmd;
    }

    public static void doIt(final WebDriverType webDriverType, final String[] webDriverArguments, final String baseUrl,
                            final int anonymousCycles, final int knownUserCycles, final String[][] users, final boolean useVpn,
                            final String osaScriptPathname, final String tunnelblickPathname) {
        // Before starting the script, make adjustments in this top block
        // to reflect the behavior that you need.
        // Inspect the clickpaths.
        // If you run the A/B-Test, note that you'll have to prepare the
        // content according to the documentation
        // https://docs.google.com/document/d/1h2E7UUt_i3yqwge25Pd8YXOLHt3Jujz4VBAdgHSeKQw/edit#heading=h.w4lf8kpcller

        final ClickpathBase[] paths;

        switch (webDriverType) {
            case FIREFOX:
                paths = new ClickpathBase[]{
                        new UoWClickpath1(new FirefoxDriverInitializer(webDriverArguments), baseUrl),
                        new UoWClickpath2(new FirefoxDriverInitializer(webDriverArguments), baseUrl),
                        new UoWClickpath3(new FirefoxDriverInitializer(webDriverArguments), baseUrl),
                        new UoWClickpath4(new FirefoxDriverInitializer(webDriverArguments), baseUrl),
                        new UoWClickpath5(new FirefoxDriverInitializer(webDriverArguments), baseUrl),
                        new UoWClickpath6(new FirefoxDriverInitializer(webDriverArguments), baseUrl),
                        new UoWClickpathABTest(new FirefoxDriverInitializer(webDriverArguments), baseUrl)
                };
                break;
            case CHROME:
                paths = new ClickpathBase[]{
                        new UoWClickpath1(new ChromeDriverInitializer(webDriverArguments), baseUrl),
                        new UoWClickpath2(new ChromeDriverInitializer(webDriverArguments), baseUrl),
                        new UoWClickpath3(new ChromeDriverInitializer(webDriverArguments), baseUrl),
                        new UoWClickpath4(new ChromeDriverInitializer(webDriverArguments), baseUrl),
                        new UoWClickpath5(new ChromeDriverInitializer(webDriverArguments), baseUrl),
                        new UoWClickpath6(new ChromeDriverInitializer(webDriverArguments), baseUrl),
                        new UoWClickpathABTest(new ChromeDriverInitializer(webDriverArguments), baseUrl)
                };
                break;
            default:
                throw new IllegalArgumentException("Unrecognised web driver type");
        }

        final int pathCount = paths.length;
        if (pathCount == 0) {
            log("No active click paths. There is nothing to do");
            return;
        }

        final int userCount = users != null ? users.length : 0;
        if (knownUserCycles > 0 && userCount == 0) {
            log("No user accounts. Unable to run click paths");
            return;
        }

        // Typically, nothing more to "configure" below this line.
        // Anything that you need to customize your scripts is above.

        final int repeats = knownUserCycles + anonymousCycles;
        log("Running " + paths.length + " clickpaths for " + repeats + " time(s)");

        long start = System.currentTimeMillis();
        final List<String> log = new LinkedList<>();

        for (int i = 1; i <= anonymousCycles; i++) {
            int pathIndex = (int) (Math.random() * paths.length);
            int vpnIndex = (int) (Math.random() * VPN_NAMES.length);
            String vpnName = VPN_NAMES[vpnIndex];
            if (useVpn) {
                try {
                    vpnConnect(osaScriptPathname, tunnelblickPathname, vpnName, null);
                } catch (IOException e) {
                    log(e.getMessage());
                }
            }
            ClickpathBase path = paths[pathIndex];
            userSequence(path, null, null, pathIndex, i, anonymousCycles, start, log);
            if (useVpn) {
                try {
                    vpnDisconnect(osaScriptPathname, tunnelblickPathname, vpnName);
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

            final int vpnIndex = VPN_NAMES.length % (userIndex + 1);
            final String vpnName = VPN_NAMES[vpnIndex];

            if (useVpn) {
                try {
                    vpnConnect(osaScriptPathname, tunnelblickPathname, vpnName, null);
                } catch (IOException e) {
                    log(e.getMessage());
                }
            }

            userSequence(path, user[0], user[1], pathIndex, i, knownUserCycles, start, log);

            if (useVpn) {
                try {
                    vpnDisconnect(osaScriptPathname, tunnelblickPathname, vpnName);
                } catch (IOException e) {
                    log(e.getMessage());
                }
            }
        }

        log("==================================================");
        log("End of run");
        log("Failed attempt(s): " + log.size());
        for (String string : log) {
            log(string);
            log("---------------------------------------------");
        }
    }

    private static void vpnConnect(final String osaScriptPathname, final String tunnelblickPathname,
                                   final String vpnName, final String username) throws IOException {
        if (!IS_MAC) {
            log("This is not an OS X environment");
            return;
        }
        log("Attempting to connect to " + vpnName + (username != null ? " for " + username : ""));
        String[] command = VPN_COMMAND.clone();
        final int osaScriptIndex = indexOf(command, "---OSASCRIPT---");
        if (osaScriptIndex >= 0) {
            command[osaScriptIndex] = osaScriptPathname;
        }
        final int tellCommandIndex = indexOf(command, "---TELL-COMMAND---");
        if (tellCommandIndex >= 0) {
            command[tellCommandIndex] = "tell application \"" + tunnelblickPathname + "\"";
        }
        final int vpnActionIndex = indexOf(command, "---VPN-ACTION---");
        if (vpnActionIndex >= 0) {
            command[vpnActionIndex] = "connect \"" + vpnName + "\"";
        }
        log("Executing shell command: " + String.join(" ", command));
        try {
            int exitCode = executeShellCommand(command);
            if (exitCode > 0) {
                throw new RuntimeException("Unable to connect to vpn");
            }
            log("Waiting for " + CONNECTION_DELAY + "ms");
            Thread.sleep(CONNECTION_DELAY);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to connect to vpn", e);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private static void userSequence(ClickpathBase path, String username, String password, int pathIndex, int i,
                                     int repeats, long start, List<String> log) {
        long thisStart = System.currentTimeMillis();
        log("Number of failures so far:" + log.size());
        log("#" + i + "/" + repeats + ": Running with path "
                + (pathIndex + 1) + " (" + path.getClass().getSimpleName() + ", using "
                + path.getDriver().getClass().getSimpleName()
                + ")");
        path.setDefaultSleep(4000);
        try {
            path.run(username, password);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String tstamp = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss", Locale.ENGLISH)
                    .format(LocalDateTime.now());
            log.add(
                    tstamp + "\n" +
                            "" + i + ", path" + pathIndex + "]\n" +
                            e.getClass().getName() + " " +
                            e.getMessage() + "\n" +
                            sw);
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
        log("Runtime (current / average) in sec: " + (thisTimeSpan / 1000L) + " / " + (runtime / ((i + 1) * 1000L)));
        log("Expected remaining run time:      " + expectedEnd);
        log("Current time:                     " + new Date());
    }

    private static void vpnDisconnect(final String osaScriptPathname, final String tunnelblickPathname,
                                      final String vpnName) throws IOException {
        if (!IS_MAC) {
            log("This is not an OS X environment");
            return;
        }
        log("Attempting to disconnect from " + vpnName);
        String[] command = VPN_COMMAND.clone();
        final int osaScriptIndex = indexOf(command, "---OSASCRIPT---");
        if (osaScriptIndex >= 0) {
            command[osaScriptIndex] = osaScriptPathname;
        }
        final int tellCommandIndex = indexOf(command, "---TELL-COMMAND---");
        if (tellCommandIndex >= 0) {
            command[tellCommandIndex] = "tell application \"" + tunnelblickPathname + "\"";
        }
        final int vpnActionIndex = indexOf(command, "---VPN-ACTION---");
        if (vpnActionIndex >= 0) {
            command[vpnActionIndex] = "disconnect \"" + vpnName + "\"";
        }
        log("Executing shell command: " + String.join(" ", command));
        try {
            int exitCode = executeShellCommand(command);
            if (exitCode > 0) {
                throw new RuntimeException("Unable to connect to vpn");
            }
            log("Waiting for " + CONNECTION_DELAY + "ms");
            Thread.sleep(CONNECTION_DELAY);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to connect to vpn", e);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private static int executeShellCommand(final String[] command) throws IllegalAccessException, IOException {
        if (command == null || command.length == 0) {
            throw new IllegalAccessException("command is null or empty");
        }
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        builder.directory(new File(System.getProperty("user.home")));
        Process process = builder.start();
        StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println,
                process.getErrorStream(), System.err::println);
        Future<?> future = EXECUTOR_SERVICE.submit(streamGobbler);
        int exitCode = 0;
        try {
            exitCode = process.waitFor();
            log("Command is done : " + future.isDone());
        } catch (InterruptedException e) {
            log("Unable to wait for process");
            e.printStackTrace();
        }
        log("Command exited with " + exitCode);
        return exitCode;
    }
}
