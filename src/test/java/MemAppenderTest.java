import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.PatternLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MemAppenderTest {
    private MemAppender memAppender;
    private Logger logger;

    @BeforeEach
    public void setUp() {
        MemAppender.resetInstance();  // Reset the singleton instance
        memAppender = MemAppender.getInstance(5, null, new PatternLayout("[$d] $t $p $c : $m"));
        logger = Logger.getLogger(MemAppenderTest.class);
        logger.addAppender(memAppender);
    }

    @Test
    public void testLogAppending() {
        logger.info("Test log 1");
        logger.error("Test log 2");

        List<LoggingEvent> logs = memAppender.getCurrentLogs();
        assertEquals(2, logs.size());
        assertEquals("Test log 1", logs.get(0).getRenderedMessage());
        assertEquals("Test log 2", logs.get(1).getRenderedMessage());
    }

    @Test
    public void testMaxSizeLimit() {
        for (int i = 0; i < 7; i++) {
            logger.info("Log " + i);
        }

        List<LoggingEvent> logs = memAppender.getCurrentLogs();
        assertEquals(5, logs.size());  // Only the last five logs should remain
        assertEquals(2, memAppender.getDiscardedLogCount());  // 2 logs should be discarded
    }

    @Test
    public void testMemAppenderSingletonPattern() {
        MemAppender instance1 = MemAppender.getInstance(100, null, new PatternLayout());
        MemAppender instance2 = MemAppender.getInstance(200, null, new PatternLayout());
        assertEquals(instance1.hashCode(), instance2.hashCode());
    }

    @Test
    public void testMemAppenderWithNullLayout() {
        MemAppender instance1 = MemAppender.getInstance(100, null, null);
        assertNotNull(instance1);
    }

    @Test
    public void testPrintLogs() {
        // Capture the output of printLogs
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        // Log some messages
        logger.info("Log message 1");
        logger.error("Log message 2");

        // Print logs
        memAppender.printLogs();

        // Restore original System.out
        System.setOut(originalOut);

        // Check the output
        String output = outputStream.toString();
        assertTrue(output.contains("Log message 1"));
        assertTrue(output.contains("Log message 2"));
        assertTrue(memAppender.getCurrentLogs().isEmpty());  // Ensure logs are cleared after printing
    }
}