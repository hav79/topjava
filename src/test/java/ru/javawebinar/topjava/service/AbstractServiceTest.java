package ru.javawebinar.topjava.service;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.concurrent.TimeUnit;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class AbstractServiceTest {

    private static final Logger log = getLogger("result");

    private static StringBuilder results = new StringBuilder();

    static {
        // needed only for java.util.logging (postgres driver)
        SLF4JBridgeHandler.install();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    // http://stackoverflow.com/questions/14892125/what-is-the-best-practice-to-determine-the-execution-time-of-the-bussiness-relev
    public Stopwatch stopwatch = new Stopwatch() {
        @Override
        protected void finished(long nanos, Description description) {
            String result = String.format("\n%-25s %7d", description.getMethodName(), TimeUnit.NANOSECONDS.toMillis(nanos));
            results.append(result);
            log.info(result + " ms\n");
        }
    };

    @AfterClass
    public static void printResult() {
        log.info("\n---------------------------------" +
                "\nTest                 Duration, ms" +
                "\n---------------------------------" +
                results +
                "\n---------------------------------");
    }

    @Test
    public abstract void delete() throws Exception;

    @Test
    public abstract void deleteNotFound() throws Exception;

    @Test
    public abstract void create() throws Exception;

    @Test
    public abstract void get() throws Exception;

    @Test
    public abstract void getNotFound() throws Exception;

    @Test
    public abstract void update() throws Exception;

    @Test
    public abstract void updateNotFound() throws Exception;

    @Test
    public abstract void getAll() throws Exception;
}
