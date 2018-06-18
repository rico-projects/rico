import dev.rico.client.Client;
import dev.rico.core.context.Context;
import dev.rico.core.http.HttpClient;
import dev.rico.tracing.Span;
import dev.rico.tracing.SpanType;
import dev.rico.tracing.Tracer;

import java.util.concurrent.Executors;

public class TraceTest2 {

    public static void main(String[] args) throws Exception {
        final Tracer tracer = Client.getService(Tracer.class);

        final Span mainSpan = tracer.startSpan("MainSpan", SpanType.LOCAL);

        final HttpClient httpClient = Client.getService(HttpClient.class);
        httpClient.get("http://localhost:8080/greeting").withoutContent().readString().execute().get();

        System.out.println("Und der papa macht weiter");
        sleep(300);
        mainSpan.complete();

        sleep(5_000);
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
