package com.liferay.sales.selenium.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class StreamGobbler implements Runnable {
    private final Consumer<String> inputConsumer;
    private final InputStream inputStream;
    private final Consumer<String> errorConsumer;
    private final InputStream errorStream;

    public StreamGobbler(InputStream inputStream, Consumer<String> inputConsumer, InputStream errorStream, Consumer<String> errorConsumer) {
        this.inputStream = inputStream;
        this.inputConsumer = inputConsumer;
        this.errorStream = errorStream;
        this.errorConsumer = errorConsumer;
    }

    @Override
    public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines()
                .forEach(inputConsumer);
        new BufferedReader(new InputStreamReader(errorStream)).lines()
                .forEach(errorConsumer);
    }
}