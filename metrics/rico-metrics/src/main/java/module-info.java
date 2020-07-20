import dev.rico.internal.metrics.binder.RuntimeClassloaderMetricsBinderProvider;
import dev.rico.internal.metrics.binder.RuntimeGcMetricsBinderProvider;
import dev.rico.internal.metrics.binder.RuntimeMemoryMetricsBinderProvider;
import dev.rico.internal.metrics.binder.RuntimeThreadMetricsBinderProvider;
import dev.rico.internal.metrics.binder.SystemFileDescriptorMetricsBinderProvider;
import dev.rico.internal.metrics.binder.SystemProcessorMetricsBinderProvider;
import dev.rico.metrics.spi.MetricsBinderProvider;

module dev.rico.metrics {

    exports dev.rico.metrics;
    exports dev.rico.metrics.types;
    exports dev.rico.internal.metrics to dev.rico.metrics.server,
            dev.rico.metrics.server.javaee,
            dev.rico.metrics.server.spring,
            dev.rico.metrics.binding.tomcat;
    exports dev.rico.metrics.spi;

    provides MetricsBinderProvider with RuntimeClassloaderMetricsBinderProvider,
            RuntimeGcMetricsBinderProvider,
            RuntimeMemoryMetricsBinderProvider,
            RuntimeThreadMetricsBinderProvider,
            SystemFileDescriptorMetricsBinderProvider,
            SystemProcessorMetricsBinderProvider;


    requires transitive dev.rico.core;
    requires static org.apiguardian.api;
    requires micrometer.core;
    requires org.slf4j;
}
