package ru.javawebinar.topjava;

import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CustomStopwatch extends Stopwatch {
    private static final Logger log = LoggerFactory.getLogger(CustomStopwatch.class);

    private final List<String> testsTimeResults;

    public CustomStopwatch(List<String> testsTimeResults) {
        this.testsTimeResults = testsTimeResults;
    }

    @Override
    protected void succeeded(long nanos, Description description) {
        logTimeResult(description.getMethodName(), TimeUnit.NANOSECONDS.toMillis(nanos));
    }

    private void logTimeResult(String methodName, long millis) {
        testsTimeResults.add(String.format("Method %s: %d ms", methodName, millis));
        log.debug("Method {}: {} ms", methodName, millis);
    }
}
