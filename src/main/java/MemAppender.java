import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MemAppender extends AppenderSkeleton {
    private List<LoggingEvent> events;
    private int maxlength;
    private int deletedLogCounter = 0;
    private int logCounter = 0;
    private static MemAppender instance;

    @Override
    protected void append(LoggingEvent loggingEvent) {
        if (logCounter >= maxlength) {
            deletedLogCounter++;  // Increment discarded log count
            events.remove(0);  // Remove the oldest log
        }
        events.add(loggingEvent);  // Add the new log
        logCounter++;  // Increment counter
    }

    @Override
    public void close() {}

    @Override
    public boolean requiresLayout() { return true; }

    public static MemAppender getInstance(int maxlength, List<LoggingEvent> logs, Layout layout1) {
        if (instance == null) {
            instance = new MemAppender(maxlength);
            instance.setLayout(layout1 != null ? layout1 : new PatternLayout(PatternLayout.DEFAULT_CONVERSION_PATTERN));
            instance.events = logs != null ? logs : new ArrayList<>();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;  // Reset for tests
    }

    public MemAppender(int maxlength) {
        this.maxlength = maxlength;
        this.events = new ArrayList<>();
    }

    public List<String> getEventStrings() {
        return events.stream().map(LoggingEvent::getRenderedMessage).collect(Collectors.toList());
    }

    public List<LoggingEvent> getCurrentLogs() {
        return Collections.unmodifiableList(events);
    }

    public long getDiscardedLogCount() {
        return deletedLogCounter;
    }

    public void printLogs(){
        List<String> logs = events.stream().map(LoggingEvent::getRenderedMessage).collect(Collectors.toList());
        logs.forEach(System.out::println);
        events.clear();
    }
}