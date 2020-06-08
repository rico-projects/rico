package dev.rico.log4j.appender;

import dev.rico.internal.core.context.ContextManagerImpl;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.JdkMapAdapterStringMap;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Plugin(name = "ContextWrappingAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class ContextWrappingAppender extends AbstractAppender {

    private final Configuration config;
    private final ConcurrentMap<String, AppenderControl> appenders = new ConcurrentHashMap<>();
    private final AppenderRef[] appenderRefs;

    private ContextWrappingAppender(final String name, final Filter filter, final boolean ignoreExceptions,
                                    Configuration config, final AppenderRef[] appenderRefs)
    {
        super(name, filter, null, ignoreExceptions, Property.EMPTY_ARRAY);
        this.config = config;
        this.appenderRefs = appenderRefs;
    }

    @Override
    public void start() {
        for (final AppenderRef ref : appenderRefs) {
            final String name = ref.getRef();
            final Appender appender = config.getAppender(name);
            if (appender != null) {
                final Filter filter = appender instanceof AbstractAppender ?
                        ((AbstractAppender) appender).getFilter() : null;
                appenders.put(name, new AppenderControl(appender, ref.getLevel(), filter));
            } else {
                LOGGER.error("Appender " + ref + " cannot be located. Reference ignored");
            }
        }
        super.start();
    }

    @Override
    public void append(LogEvent source) {
        Map<String, String> contextData = ContextManagerImpl.getInstance().getAttributes();
        JdkMapAdapterStringMap targetContext = new JdkMapAdapterStringMap(contextData);

        ReadOnlyStringMap sourceContext = source.getContextData();
        targetContext.putAll(sourceContext);
        LogEvent targetEvent = new Log4jLogEvent.Builder(source)
                .setContextData(targetContext)
                .build();

        for (final AppenderControl control : appenders.values()) {
            control.callAppender(targetEvent);
        }
    }

    @PluginFactory
    public static ContextWrappingAppender createAppender(
            @PluginAttribute("name") final String name,
            @PluginAttribute("ignoreExceptions") final String ignore,
            @PluginElement("AppenderRef") final AppenderRef[] appenderRefs,
            @PluginConfiguration final Configuration config,
            @PluginElement("Filter") final Filter filter)
    {

        final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
        if (name == null) {
            LOGGER.error("No name provided for RewriteAppender");
            return null;
        }
        if (appenderRefs == null) {
            LOGGER.error("No appender references defined for RewriteAppender");
            return null;
        }
        return new ContextWrappingAppender(name, filter, ignoreExceptions, config, appenderRefs);
    }
}
