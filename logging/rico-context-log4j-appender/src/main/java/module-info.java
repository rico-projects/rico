module dev.rico.log4j.appender {

    exports dev.rico.log4j.appender;
    opens dev.rico.log4j.appender;

    requires static org.apiguardian.api;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j;

    requires transitive dev.rico.core;
}
