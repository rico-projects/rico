package dev.rico.internal.remoting.converters;

import dev.rico.remoting.converter.Converter;
import dev.rico.remoting.converter.ValueConverterException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

public class ZonedDateTimeConverterTest {

    @Test
    public void testSupportedType() {
        //given
        final ZonedDateTimeConverterFactory factory = new ZonedDateTimeConverterFactory();

        //then
        Assert.assertTrue(factory.supportsType(ZonedDateTime.class));
        Assert.assertFalse(factory.supportsType(LocalDateTime.class));
    }

    @Test
    public void testNullValue() throws ValueConverterException {
        //given
        final ZonedDateTimeConverterFactory factory = new ZonedDateTimeConverterFactory();
        final Converter converter = factory.getConverterForType(ZonedDateTime.class);

        //when
        final Object rawObject = converter.convertToRemoting(null);
        final Object reConverted = converter.convertFromRemoting(rawObject);

        //then
        Assert.assertNull(reConverted);
    }

    @Test
    public void testSameTimeZone() throws ValueConverterException {
        //given
        final ZonedDateTimeConverterFactory factory = new ZonedDateTimeConverterFactory();
        final ZonedDateTime time = ZonedDateTime.now();
        final Converter converter = factory.getConverterForType(ZonedDateTime.class);

        //when
        final Object rawObject = converter.convertToRemoting(time);
        final Object reConverted = converter.convertFromRemoting(rawObject);

        //then
        Assert.assertNotNull(rawObject);
        Assert.assertNotNull(reConverted);
        Assert.assertTrue(ZonedDateTime.class.isAssignableFrom(reConverted.getClass()));
        final ZonedDateTime reconvertedTime = (ZonedDateTime) reConverted;
        Assert.assertEquals(reconvertedTime, time);
    }

    @Test
    public void testDifferentTimeZone() throws ValueConverterException {
        //given
        final ZonedDateTimeConverterFactory factory = new ZonedDateTimeConverterFactory();
        final ZoneId currentZoneId = ZoneId.systemDefault();
        final ZoneId differentZoneId = ZoneId.getAvailableZoneIds().stream()
                .map(i -> ZoneId.of(i))
                .filter(zoneId -> !Objects.equals(zoneId, currentZoneId))
                .findAny()
                .orElseThrow(() -> new RuntimeException("No Zone ID found!"));
        final ZonedDateTime time = ZonedDateTime.now(differentZoneId);
        final Converter converter = factory.getConverterForType(ZonedDateTime.class);

        //when
        final Object rawObject = converter.convertToRemoting(time);
        final Object reConverted = converter.convertFromRemoting(rawObject);

        //then
        Assert.assertNotNull(rawObject);
        Assert.assertNotNull(reConverted);
        Assert.assertTrue(ZonedDateTime.class.isAssignableFrom(reConverted.getClass()));
        final ZonedDateTime reconvertedTime = (ZonedDateTime) reConverted;
        Assert.assertEquals(reconvertedTime, time);
    }

}
