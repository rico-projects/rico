package dev.rico.internal.tracing.service;

import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;

public class ConsoleReporter extends AsyncReporter<Span> {
    @Override
    public void flush() {

    }

    @Override
    public void close() {

    }

    @Override
    public void report(Span span) {
        System.out.println(span.toString());
    }
}
