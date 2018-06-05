package dev.rico.core.http;

import java.util.function.Function;

@FunctionalInterface
public interface HttpExecutorFactory<R> extends Function<HttpProvider<R>, HttpExecutor<R>> {
}
