import dev.rico.internal.remoting.converters.BeanConverterFactory;
import dev.rico.internal.remoting.converters.BigDecimalConverterFactory;
import dev.rico.internal.remoting.converters.BigIntegerConverterFactory;
import dev.rico.internal.remoting.converters.BooleanConverterFactory;
import dev.rico.internal.remoting.converters.ByteConverterFactory;
import dev.rico.internal.remoting.converters.CalendarConverterFactory;
import dev.rico.internal.remoting.converters.CharacterConverterFactory;
import dev.rico.internal.remoting.converters.ClassConverterFactory;
import dev.rico.internal.remoting.converters.DateConverterFactory;
import dev.rico.internal.remoting.converters.DoubleConverterFactory;
import dev.rico.internal.remoting.converters.DurationConverterFactory;
import dev.rico.internal.remoting.converters.EnumConverterFactory;
import dev.rico.internal.remoting.converters.FloatConverterFactory;
import dev.rico.internal.remoting.converters.IntegerConverterFactory;
import dev.rico.internal.remoting.converters.LocalDateConverterFactory;
import dev.rico.internal.remoting.converters.LocalDateTimeConverterFactory;
import dev.rico.internal.remoting.converters.LongConverterFactory;
import dev.rico.internal.remoting.converters.PeriodConverterFactory;
import dev.rico.internal.remoting.converters.ShortConverterFactory;
import dev.rico.internal.remoting.converters.StringConverterFactory;
import dev.rico.internal.remoting.converters.UuidConverterFactory;
import dev.rico.internal.remoting.converters.ZonedDateTimeConverterFactory;
import dev.rico.remoting.converter.ConverterFactory;

module dev.rico.remoting.common {

    exports dev.rico.remoting;
    exports dev.rico.remoting.converter;

    exports dev.rico.internal.remoting to dev.rico.remoting.client,
            dev.rico.remoting.server,
            dev.rico.remoting.server.spring,
            dev.rico.remoting.server.spring.test;
    exports dev.rico.internal.remoting.codec to dev.rico.remoting.client,
            dev.rico.remoting.server;
    exports dev.rico.internal.remoting.commands to dev.rico.remoting.client,
            dev.rico.remoting.server;
    exports dev.rico.internal.remoting.collections to dev.rico.remoting.client,
            dev.rico.remoting.server;
    exports dev.rico.internal.remoting.info to dev.rico.remoting.client,
            dev.rico.remoting.server;
    exports dev.rico.internal.remoting.legacy to dev.rico.remoting.client,
            dev.rico.remoting.server;
    exports dev.rico.internal.remoting.legacy.core to dev.rico.remoting.client,
            dev.rico.remoting.server;
    exports dev.rico.internal.remoting.legacy.communication to dev.rico.remoting.client,
            dev.rico.remoting.server,
            dev.rico.remoting.server.spring.test;
    exports dev.rico.internal.remoting.legacy.commands to dev.rico.remoting.client,
            dev.rico.remoting.server,
            dev.rico.remoting.server.spring.test;

    uses ConverterFactory;

    provides ConverterFactory with BooleanConverterFactory,
            ByteConverterFactory,
            CalendarConverterFactory,
            DateConverterFactory,
            BeanConverterFactory,
            DoubleConverterFactory,
            EnumConverterFactory,
            FloatConverterFactory,
            IntegerConverterFactory,
            LongConverterFactory,
            ShortConverterFactory,
            StringConverterFactory,
            BigDecimalConverterFactory,
            BigIntegerConverterFactory,
            UuidConverterFactory,
            DurationConverterFactory,
            LocalDateTimeConverterFactory,
            PeriodConverterFactory,
            ZonedDateTimeConverterFactory,
            LocalDateConverterFactory,
            ClassConverterFactory,
            CharacterConverterFactory;

    requires transitive dev.rico.core;

    requires static org.apiguardian.api;
    requires org.slf4j;
    requires java.desktop;
    requires com.google.gson;
}