package dev.rico.core;

import dev.rico.core.context.Context;
import dev.rico.core.context.ContextManager;
import dev.rico.core.trace.Span;
import dev.rico.core.trace.SpanType;
import dev.rico.internal.core.context.ContextManagerImpl;
import dev.rico.internal.core.tracing.service.LogReporter;
import dev.rico.internal.core.tracing.TracerImpl;
import dev.rico.internal.core.tracing.TracingBootstrap;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class TraceTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final LogReporter reporter = new LogReporter();
        final ContextManager contextManager = new ContextManagerImpl();
        final TracingBootstrap bootstrap = new TracingBootstrap("Test-App", reporter, contextManager);
        final TracerImpl tracer = bootstrap.getTracer();

        tracer.runInSpan("MySpan", () -> {
            sleep(500);
            System.out.println("Ich laufe im Span");
            sleep(1_000);
        });

        Executors.newSingleThreadExecutor().submit(() -> {
            final Span threadSpan = tracer.startSpan("ThreadSpan", SpanType.LOCAL);
            sleep(1_000);
            System.out.println("Ich laufe im Thread");
            sleep(300);
            threadSpan.complete();
        });

        final Span mainSpan = tracer.startSpan("MainSpan", SpanType.LOCAL);

        System.out.println("jetzt passieren ein paar dinge");
        sleep(1_000);

        final Span childSpan = tracer.startSpan("ChildSpan", SpanType.LOCAL);
        System.out.println("Und jetzt macht das Kind was");
        sleep(200);
        childSpan.complete();


        Executors.newSingleThreadExecutor().submit(() -> {
            final Span threadSpan = tracer.startChildSpan(mainSpan, "ThreadSpan", SpanType.LOCAL);
            threadSpan.addContext(Context.of("Type", "InThread"));
            sleep(1_000);
            System.out.println("Ich laufe im Thread Als Child");
            sleep(300);
            threadSpan.complete();
        }).get();

        System.out.println("Und der papa macht weiter");
        sleep(300);
        mainSpan.complete();
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
