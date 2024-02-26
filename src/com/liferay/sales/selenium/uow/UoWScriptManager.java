package com.liferay.sales.selenium.uow;

import com.liferay.sales.selenium.api.*;
import com.liferay.sales.selenium.chrome.ChromeDriverInitializer;
import com.liferay.sales.selenium.firefox.FirefoxDriverInitializer;
import com.liferay.sales.selenium.util.StreamGobbler;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class UoWScriptManager extends ScriptManager {
    private static final Map<String, ClickpathConfig<ClickpathBase>> CLICKPATH_CONFIG_MAP;
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

    static {
        ClickpathConfig<ClickpathBase> clickpathConfig1 = new ClickpathConfig(UoWClickpath1.class, "1", ClickpathConfig.Type.STANDARD);
        ClickpathConfig<ClickpathBase> clickpathConfig2 = new ClickpathConfig(UoWClickpath2.class, "2", ClickpathConfig.Type.STANDARD);
        ClickpathConfig<ClickpathBase> clickpathConfig3 = new ClickpathConfig(UoWClickpath3.class, "3", ClickpathConfig.Type.STANDARD);
        ClickpathConfig<ClickpathBase> clickpathConfig4 = new ClickpathConfig(UoWClickpath4.class, "4", ClickpathConfig.Type.STANDARD);
        ClickpathConfig<ClickpathBase> clickpathConfig5 = new ClickpathConfig(UoWClickpath5.class, "5", ClickpathConfig.Type.STANDARD);
        ClickpathConfig<ClickpathBase> clickpathConfig6 = new ClickpathConfig(UoWClickpath6.class, "6", ClickpathConfig.Type.STANDARD);
        ClickpathConfig<ClickpathBase> clickpathConfig7 = new ClickpathConfig(UoWClickpathABTest.class, "ab1", ClickpathConfig.Type.AB);
        CLICKPATH_CONFIG_MAP = new HashMap<>() {{
            put(clickpathConfig1.getKey(), clickpathConfig1);
            put(clickpathConfig2.getKey(), clickpathConfig2);
            put(clickpathConfig3.getKey(), clickpathConfig3);
            put(clickpathConfig4.getKey(), clickpathConfig4);
            put(clickpathConfig5.getKey(), clickpathConfig5);
            put(clickpathConfig6.getKey(), clickpathConfig6);
            put(clickpathConfig7.getKey(), clickpathConfig7);
        }};
    }

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
            final String[] defaultClickpathSelection = CLICKPATH_CONFIG_MAP.keySet().toArray(new String[0]);
            final ClickpathTypeSelection defaultClickpathTypeSelection = ClickpathTypeSelection.ALL;

            CommandLine cmd = parseArguments(args, defaultBaseUrl, defaultAnonymousCycles, defaultKnownUserCycles,
                    defaultUsersCSVPathname, defaultUseVpn, defaultWebDriverType, defaultOsaScriptPathname,
                    defaultTunnelblickPathname, defaultClickpathSelection, defaultClickpathTypeSelection);

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
                    // do nothing
                }

                int knownUserCycles = defaultKnownUserCycles;
                try {
                    if (cmd.hasOption("known-user-cycles")) {
                        knownUserCycles = Integer.parseInt(cmd.getOptionValue("known-user-cycles"));
                    }
                } catch (NumberFormatException e) {
                    // do nothing
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

                assert webDriverType != null;
                String webDriverPathname = webDriverType.getDefaultWebDriverPathname();
                String[] webDriverArguments;

                if (cmd.hasOption("driver-path")) {
                    webDriverPathname = cmd.getOptionValue("driver-path");
                }

                if (cmd.hasOption("driver-arguments")) {
                    String webDriverArgumentsString = cmd.getOptionValue("driver-arguments").trim();
                    webDriverArguments = webDriverArgumentsString.split(" ");
                } else {
                    webDriverArguments = webDriverType.getDefaultWebDriverArguments().split(" ");
                }

                String[] clickpathSelection;
                if (cmd.hasOption("clickpaths")) {
                    String clickpathSelectionString = cmd.getOptionValue("clickpaths").trim();
                    clickpathSelection = clickpathSelectionString.split(" ");
                } else {
                    if (cmd.hasOption("clickpath-type")) {
                        final String optionValue = cmd.getOptionValue("clickpath-type");
                        if (optionValue == null || optionValue.isBlank()) {
                            throw new IllegalArgumentException("clickpath-type needs a value");
                        }
                        final ClickpathTypeSelection clickpathTypeSelection =
                                searchEnum(ClickpathTypeSelection.class, optionValue);
                        if (clickpathTypeSelection == null) {
                            throw new IllegalArgumentException(cmd.getOptionValue("clickpath-type") + " is not a valid clickpath-tupe");
                        }
                        if (clickpathTypeSelection != ClickpathTypeSelection.ALL) {
                            clickpathSelection = CLICKPATH_CONFIG_MAP
                                    .values()
                                    .stream()
                                    .filter(
                                            clickpathConfig ->
                                                    clickpathTypeSelection.toString()
                                                            .equalsIgnoreCase(
                                                                    clickpathConfig.getType().toString()))
                                    .map(ClickpathConfig::getKey)
                                    .collect(Collectors.toUnmodifiableList())
                                    .toArray(String[]::new);
                        } else {
                            clickpathSelection = defaultClickpathSelection;
                        }
                    } else {
                        clickpathSelection = defaultClickpathSelection;
                    }
                }

                String osaScriptPathname = defaultOsaScriptPathname;
                if (cmd.hasOption("osascript-path")) {
                    osaScriptPathname = cmd.getOptionValue("osascript-path");
                }

                String tunnelblickPathname = defaultTunnelblickPathname;
                if (cmd.hasOption("tunnelblick-path")) {
                    tunnelblickPathname = cmd.getOptionValue("tunnelblick-path");
                }

                log("The base URL is " + baseUrl);
                log("Running " + anonymousCycles + " anonymous cycles");
                log("Running " + knownUserCycles + " known user cycles");
                if (usersCSVPathname != null) {
                    log("User accounts in " + usersCSVPathname);
                }
                log(useVpn ? "The VPN will be used" : "The VPN will NOT be used");
                System.out.print("Driving " + webDriverType + " with " + webDriverPathname);
                if (webDriverArguments.length > 0) {
                    System.out.print(" using \"");
                    System.out.print(String.join(" ", webDriverArguments));
                    log("\"");
                } else {
                    log("");
                }

                String[][] users = null;
                if (usersCSVPathname != null) {
                    if (doesFileExist(usersCSVPathname)) {
                        users = readUserCSV(usersCSVPathname);
                        log("The number of users for the known user cycles is " + users.length);
                    }
                }

                if (users == null || users.length == 0) {
                    log("There are no user accounts. The known user cycles will be set to 0");
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
                        osaScriptPathname, tunnelblickPathname, clickpathSelection);
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
                                              final String defaultTunnelblickPathnameOption,
                                              final String[] defaultClickpathSelection,
                                              final ClickpathTypeSelection defaultClickpathTypeSelection) throws ParseException {
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
                "The arguments to use with the web driver. The default arguments are \""
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

        final Option clickpathSelection = new Option("c", "clickpaths", true,
                "The clickpaths to use. The default argument is \""
                        + String.join(" ", defaultClickpathSelection) + "\"");
        clickpathSelection.setRequired(false);
        options.addOption(clickpathSelection);

        final Option clickpathTypeSelection = new Option("t", "clickpath-type", true,
                "The type of clickpaths to use. The default argument is "
                        + defaultClickpathTypeSelection
                        + ". If -c[--clickpaths] is used then it will override the type specified here");
        clickpathTypeSelection.setRequired(false);
        options.addOption(clickpathTypeSelection);

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

        CommandLine cmd;
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
                            final String osaScriptPathname, final String tunnelblickPathname, String[] clickpathSelection) {
        final ClickpathBase[] paths = buildPathsArray(baseUrl, clickpathSelection, webDriverType, webDriverArguments);

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
                    vpnConnect(osaScriptPathname, tunnelblickPathname, vpnName, user[0]);
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

    private static ClickpathBase[] buildPathsArray(String baseUrl, String[] clickpathSelection, WebDriverType webDriverType, String[] webDriverArguments) {
        final List<ClickpathBase> clickpaths = new ArrayList<>();
        for (String key : clickpathSelection) {
            if (!CLICKPATH_CONFIG_MAP.containsKey(key)) {
                log("A clickpath with " + key + " was not found");
                continue;
            }
            try {
                final DriverInitializer driverInitializer;
                switch (webDriverType) {
                    case CHROME:
                        driverInitializer = new ChromeDriverInitializer(webDriverArguments);
                        break;
                    case FIREFOX:
                        driverInitializer = new FirefoxDriverInitializer(webDriverArguments);
                        break;
                    default:
                        throw new IllegalArgumentException("Unrecognised web driver type");
                }

                final ClickpathConfig<ClickpathBase> config = CLICKPATH_CONFIG_MAP.get(key);
                final Class<ClickpathBase> clazz = config.getClickpathClass();
                final Constructor<ClickpathBase> ctor = clazz.getConstructor(DriverInitializer.class, String.class);
                ClickpathBase clickpath = ctor.newInstance(driverInitializer, baseUrl);
                log("Added clickpath " + clickpath.getClass().getSimpleName() + " to array");
                clickpaths.add(clickpath);
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException e) {
                log("Unable to create clickpath instance");
                e.printStackTrace();
            }
        }
        return clickpaths.toArray(new ClickpathBase[0]);
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
