import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import java.io.StringWriter;
import java.util.Date;

public class VelocityLayout extends Layout {
    static String Template_Name = "Template1";
    static String Patterns = "[$d] $t $c $p : $m";
    static VelocityEngine engine = new VelocityEngine();
    static {
        engine.setProperty(Velocity.RESOURCE_LOADER, "string");
        engine.setProperty("resource.loader.string.class", StringResourceLoader.class.getName());
        engine.setProperty("resource.loader.string.cache", true);
        engine.setProperty("resource.loader.string.modification_check_interval", 50);
        engine.setProperty(Velocity.RUNTIME_LOG, "velocity.log");
        engine.init();

        StringResourceRepository repo1 = StringResourceLoader.getRepository();
        repo1.putStringResource(Template_Name, Patterns);
    }

    @Override
    public String format(LoggingEvent events) {
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("d", new Date(events.getTimeStamp()).toString());
        velocityContext.put("t", events.getThreadName());
        velocityContext.put("c", events.getLevel().toString());
        velocityContext.put("p", events.getLogger().getName());
        velocityContext.put("m", events.getRenderedMessage());

        StringWriter newWriter = new StringWriter();
        engine.getTemplate(Template_Name).merge(velocityContext, newWriter);
        return newWriter.toString();
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

    @Override
    public void activateOptions() {

    }
}