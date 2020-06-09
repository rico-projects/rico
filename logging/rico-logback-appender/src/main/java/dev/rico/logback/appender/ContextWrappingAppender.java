package dev.rico.logback.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;
import dev.rico.internal.core.context.ContextManagerImpl;
import org.slf4j.Marker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Wrapper which adds Rico Context to the MDC map.
 */
public class ContextWrappingAppender<E> extends AppenderBase<E> implements AppenderAttachable<E> {

    private final AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<>();

    @Override
    protected void append(E eventObject) {
        final E result = createEventWithAugmentedMdc(eventObject);
        aai.appendLoopOnAppenders(result);
    }

    @SuppressWarnings("unchecked")
    private E createEventWithAugmentedMdc(E eventObject) {
        if (eventObject instanceof ILoggingEvent) {
            final ILoggingEvent loggingEvent = (ILoggingEvent) eventObject;
            final Map<String, String> newMdc = new HashMap<>(ContextManagerImpl.getInstance().getAttributes());
            newMdc.putAll(loggingEvent.getMDCPropertyMap());
            return (E) new RicoLoggingEvent(loggingEvent, newMdc);
        }

        return eventObject;
    }

    /*
     * AppenderAttachable methods
     */
    public void addAppender(Appender<E> newAppender) {
        addInfo("Attaching appender named [" + newAppender.getName() + "] to " + getClass().getSimpleName() + ".");
        aai.addAppender(newAppender);
    }

    public Iterator<Appender<E>> iteratorForAppenders() {
        return aai.iteratorForAppenders();
    }

    public Appender<E> getAppender(String name) {
        return aai.getAppender(name);
    }

    public boolean isAttached(Appender<E> eAppender) {
        return aai.isAttached(eAppender);
    }

    public void detachAndStopAllAppenders() {
        aai.detachAndStopAllAppenders();
    }

    public boolean detachAppender(Appender<E> eAppender) {
        return aai.detachAppender(eAppender);
    }

    public boolean detachAppender(String name) {
        return aai.detachAppender(name);
    }

    private static class RicoLoggingEvent implements ILoggingEvent {

        private final ILoggingEvent delegate;
        private final Map<String, String> mdcMap;

        RicoLoggingEvent(ILoggingEvent delegate, Map<String, String> mdcMap) {
            this.delegate = delegate;
            this.mdcMap = mdcMap;
        }

        @Override
        public Map<String, String> getMDCPropertyMap() {
            return mdcMap;
        }

        @Override
        public String getThreadName() {
            return delegate.getThreadName();
        }

        @Override
        public Level getLevel() {
            return delegate.getLevel();
        }

        @Override
        public String getMessage() {
            return delegate.getMessage();
        }

        @Override
        public Object[] getArgumentArray() {
            return delegate.getArgumentArray();
        }

        @Override
        public String getFormattedMessage() {
            return delegate.getFormattedMessage();
        }

        @Override
        public String getLoggerName() {
            return delegate.getLoggerName();
        }

        @Override
        public LoggerContextVO getLoggerContextVO() {
            return delegate.getLoggerContextVO();
        }

        @Override
        public IThrowableProxy getThrowableProxy() {
            return delegate.getThrowableProxy();
        }

        @Override
        public StackTraceElement[] getCallerData() {
            return delegate.getCallerData();
        }

        @Override
        public boolean hasCallerData() {
            return delegate.hasCallerData();
        }

        @Override
        public Marker getMarker() {
            return delegate.getMarker();
        }

        @Override
        public Map<String, String> getMdc() {
            return getMDCPropertyMap();
        }

        @Override
        public long getTimeStamp() {
            return delegate.getTimeStamp();
        }

        @Override
        public void prepareForDeferredProcessing() {
            delegate.prepareForDeferredProcessing();
        }
    }

}
