import org.apache.log4j.Logger;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemAppenderStressTest {
    private MemAppender memAppenderLinkedList;
    private MemAppender memAppenderArrayList;
    private Logger logger;

    @BeforeEach
    public void setUp() {
        memAppenderLinkedList = MemAppender.getInstance(10000, new LinkedList<>(), new PatternLayout());
        memAppenderArrayList = MemAppender.getInstance(10000, new ArrayList<>(), new PatternLayout());

        logger = Logger.getLogger(MemAppenderStressTest.class);
    }

    @Test
    public void testLinkedListAppenderPerformance() {
        logger.addAppender(memAppenderLinkedList);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            logger.info("Linked List test log " + i);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("LinkedListAppender time: " + (endTime - startTime) + " ms");
    }

    @Test
    public void testArrayListAppenderPerformance() {
        logger.addAppender(memAppenderArrayList);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            logger.info("Array test log " + i);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("ArrayListAppender time: " + (endTime - startTime) + " ms");
    }

    @Test
    public void testConsoleAndFileAppenderPerformance() throws Exception {
        ConsoleAppender consoleAppender = new ConsoleAppender(new PatternLayout());
        FileAppender fileAppender = new FileAppender(new PatternLayout(), "logs.log");

        logger.addAppender(consoleAppender);
        long consoleStartTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            logger.info("Test log to console " + i);
        }

        long consoleEndTime = System.currentTimeMillis();
        System.out.println("ConsoleAppender time: " + (consoleEndTime - consoleStartTime) + " ms");

        logger.addAppender(fileAppender);
        long fileStartTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            logger.info("Test log to file " + i);
        }

        long fileEndTime = System.currentTimeMillis();
        System.out.println("FileAppender time: " + (fileEndTime - fileStartTime) + " ms");
    }
}