package dev.rico.metrics.spi.hibernate;

import dev.rico.metrics.spi.MetricsBinder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.jpa.HibernateQueryMetrics;
import org.hibernate.SessionFactory;

public class HibernateQueryMetricsBinding implements MetricsBinder {

    private final SessionFactory sessionFactory;

    private final String sessionFactoryName;

    public HibernateQueryMetricsBinding(final SessionFactory sessionFactory, final String sessionFactoryName) {
        this.sessionFactory = sessionFactory;
        this.sessionFactoryName = sessionFactoryName;
    }

    @Override
    public void init(final MeterRegistry registry, final Iterable<Tag> tags) {
        HibernateQueryMetrics.monitor(registry, sessionFactory, sessionFactoryName, tags);
    }
}
