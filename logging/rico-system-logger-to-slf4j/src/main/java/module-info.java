import dev.rico.logging.slf4j.Slf4jLoggerFinder;

module dev.rico.logging.slf4j {
    requires static org.apiguardian.api;
    requires transitive dev.rico.core;
    requires org.slf4j;

    provides System.LoggerFinder with Slf4jLoggerFinder;
}
