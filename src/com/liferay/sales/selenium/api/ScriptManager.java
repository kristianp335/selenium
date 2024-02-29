package com.liferay.sales.selenium.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.IntStream;

public abstract class ScriptManager {
    public static boolean doesFileExist(String filePathString) {
        File f = new File(filePathString);
        return (f.exists() && !f.isDirectory());
    }

    public static <T> int indexOf(T[] arr, T val) {
        return IntStream.range(0, arr.length).filter(i -> arr[i] == val).findFirst().orElse(-1);
    }

    /**
     * Read a stupidly simple CSV format: No title, content is just
     * rows with "name,password" (comma-separated, no escaping, no quotes)
     * Luxury trimming done to individual entries without extra charge.
     *
     * @param filename the filename and path
     * @return a two-dimensional array containing the users
     */
    public static String[][] readUserCSV(String filename) {
        final ArrayList<String[]> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                final ArrayList<String> record = readCsvRecord(line);
                if (record.size() == 2) {
                    records.add(record.toArray(String[]::new));
                } else {
                    throw new IllegalArgumentException("The CSV record returned the wrong number of column values. Expected 1 and received " + record.size());
                }
            }
        } catch (FileNotFoundException e) {
            log("Unable to find the file - " + filename);
        }
        return records.toArray(String[][]::new);
    }

    private static ArrayList<String> readCsvRecord(String row) {
        try (Scanner rowScanner = new Scanner(row)) {
            ArrayList<String> values = new ArrayList<>();
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next().trim());
            }
            return values;
        }
    }

    /**
     * Syntactic sugar for System.out.println
     *
     * @param message the message
     */
    protected static void log(String message) {
        System.out.println(message);
    }

    /**
     * Read a stupidly simple CSV format: No title, content is just
     * rows with "name" (comma-separated, no escaping, no quotes)
     * Luxury trimming done to individual entries without extra charge.
     *
     * @param filename the filename and path
     * @return an array containing the users
     */
    public static String[] readVpnCsv(String filename) {
        final ArrayList<String> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                final ArrayList<String> record = readCsvRecord(line);
                if (record.size() == 1) {
                    records.add(record.get(0));
                } else {
                    throw new IllegalArgumentException("The CSV record returned the wrong number of column values. Expected 1 and received " + record.size());
                }
            }
        } catch (FileNotFoundException e) {
            log("Unable to find the file - " + filename);
        }
        return records.toArray(String[]::new);
    }

    public static <T extends Enum<?>> T searchEnum(Class<T> enumeration,
                                                   String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(search) == 0) {
                return each;
            }
        }
        return null;
    }

}
