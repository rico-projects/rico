package dev.rico.internal.client.tracing.http;

import dev.rico.client.Client;
import dev.rico.core.Configuration;
import dev.rico.core.http.HttpURLConnectionInterceptor;
import dev.rico.core.http.spi.RequestHandlerProvider;
import dev.rico.tracing.Tracer;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class HttpTracingHandlerProvider implements RequestHandlerProvider, Supplier<Tracer> {

    private Tracer tracer;

    private final Lock tracerCreationLock = new ReentrantLock();

    @Override
    public HttpURLConnectionInterceptor getHandler(final Configuration configuration) {
        return new HttpTracingHandler(this);
    }

    @Override
    public Tracer get() {
        if(tracer == null) {
            tracerCreationLock.lock();
            try {
                if (tracer == null) {
                    tracer = Client.getService(Tracer.class);
                }
            } finally {
                tracerCreationLock.unlock();
            }
        }
        return tracer;
    }
}
