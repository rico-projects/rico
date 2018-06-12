import dev.rico.client.Client;
import dev.rico.core.context.Context;
import dev.rico.core.context.ContextManager;
import dev.rico.core.http.HttpClient;
import dev.rico.internal.core.context.ContextManagerImpl;
import dev.rico.internal.tracing.TracerImpl;
import dev.rico.internal.tracing.TracingBootstrap;
import dev.rico.internal.tracing.service.HttpSender;
import dev.rico.internal.tracing.service.LogReporter;
import dev.rico.tracing.Span;
import dev.rico.tracing.SpanType;
import dev.rico.tracing.Tracer;
import zipkin2.Callback;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class TraceTest {

    public static void main(String[] args) throws Exception {
//        final LogReporter reporter = new LogReporter();
//
//        final HttpSender sender = new HttpSender(new URI("http://127.0.0.1:9411/api/v2/spans"), Client.getService(HttpClient.class));
//        sender.setHttpResultCallback(new Callback<Void>() {
//            @Override
//            public void onSuccess(Void value) {
//                System.out.println("juhu");
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                t.printStackTrace();
//            }
//        });
//
//        final ContextManager contextManager = Client.getService(ContextManager.class);
//        contextManager.addGlobalContext("App", "TestApp");
//        contextManager.addThreadContext("Thread", "MainThread");
//
//        final TracingBootstrap bootstrap = new TracingBootstrap("Test-App", sender, contextManager);
//        final TracerImpl tracer = bootstrap.getTracer();

        final Tracer tracer = Client.getService(Tracer.class);

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

        final Span childSpan = tracer.startSpan("ChildSpan", SpanType.CLIENT);
        System.out.println("Und jetzt macht das Kind was");
        sleep(200);
        childSpan.complete();

        final Span childSpan2 = tracer.startSpan("ChildSpan2", SpanType.LOCAL);
        childSpan2.addContext(Context.of("type", "withoutSleep"));
        childSpan2.complete();


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
