package dev.rico.internal.server.timing;

import dev.rico.internal.core.Assert;
import dev.rico.server.timing.Metric;
import dev.rico.server.timing.ServerTiming;
import dev.rico.server.timing.Timing;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.Optional;

@Timing
@Interceptor
public class TimingInterceptor {

    private final ServerTiming serverTiming;

    @Inject
    public TimingInterceptor(final ServerTiming serverTiming) {
        this.serverTiming = Assert.requireNonNull(serverTiming, "serverTiming");
    }

    @AroundInvoke
    public Object handle(final InvocationContext joinPoint) throws Exception {
        final Method m = joinPoint.getMethod();
        final Timing timingAnnotation = m.getAnnotation(Timing.class);
        if(timingAnnotation != null) {
            final String name = Optional.ofNullable(timingAnnotation.value()).orElse("UNKNOWN");
            final String description = timingAnnotation.description();
            final Metric metric = serverTiming.start(name, description);
            try {
                return joinPoint.proceed();
            } finally {
                metric.stop();
            }
        } else {
            return joinPoint.proceed();
        }
    }
}
