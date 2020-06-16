package dev.rico.core.concurrent;

import dev.rico.internal.core.concurrent.ScheduledTaskResultImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.testng.Assert.*;

/**
 * Tests for {@link Trigger}.
 */
public class TriggerTest {
    private final Duration fourSeconds = Duration.of(4, ChronoUnit.SECONDS);
    private final Duration eightSeconds = Duration.of(8, ChronoUnit.SECONDS);
    private final Duration fifteenSeconds = Duration.of(15, ChronoUnit.SECONDS);

    private LocalDateTime now;

    @BeforeMethod
    public void setUp() {
        now = LocalDateTime.now();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullForIn() {
        Trigger.in(null);
    }

    @Test
    public void testInFirstExecution() {
        // given
        final Trigger trigger = Trigger.in(fourSeconds);

        // when
        final Optional<LocalDateTime> result = trigger.nextExecutionTime(null);

        // then
        assertTrue(result.isPresent());
        assertApproximately(now.plus(fourSeconds), result.get());
    }

    @Test
    public void testInSecondExecution() {
        // given
        final Trigger trigger = Trigger.in(fourSeconds);

        // when
        final Optional<LocalDateTime> result = trigger.nextExecutionTime(lastExecution(now));

        // then
        assertFalse(result.isPresent());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullForEvery() {
        Trigger.every(null);
    }

    @Test
    public void testEveryFirstExecution() {
        // given
        final Trigger trigger = Trigger.every(fourSeconds);

        // when
        final Optional<LocalDateTime> result = trigger.nextExecutionTime(null);

        // then
        assertTrue(result.isPresent());
        assertApproximately(now.plus(fourSeconds), result.get());
    }

    @Test
    public void testEverySecondExecution() {
        // given
        final Trigger trigger = Trigger.every(fourSeconds);

        // when
        final Optional<LocalDateTime> result = trigger.nextExecutionTime(lastExecution(now));

        // then
        assertTrue(result.isPresent());
        assertApproximately(now.plus(fourSeconds), result.get());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullForNowAndEvery() {
        Trigger.nowAndEvery(null);
    }

    @Test
    public void testNowAndEveryFirstExecution() {
        // given
        final Trigger trigger = Trigger.nowAndEvery(fourSeconds);

        // when
        final Optional<LocalDateTime> result = trigger.nextExecutionTime(null);

        // then
        assertTrue(result.isPresent());
        assertApproximately(now, result.get());
    }

    @Test
    public void testNowAndEverySecondExecution() {
        // given
        final Trigger trigger = Trigger.nowAndEvery(fourSeconds);

        // when
        final Optional<LocalDateTime> result = trigger.nextExecutionTime(lastExecution(now));

        // then
        assertTrue(result.isPresent());
        assertApproximately(now.plus(fourSeconds), result.get());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullForAfterAndEvery() {
        Trigger.afterAndEvery(null, null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullForAfterAndEveryDuration() {
        Trigger.afterAndEvery(fifteenSeconds, null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullForAfterAndEveryDelay() {
        Trigger.afterAndEvery(null, fourSeconds);
    }

    @Test
    public void testAfterAndEveryFirstExecution() {
        // given
        final Trigger trigger = Trigger.afterAndEvery(fifteenSeconds, fourSeconds);

        // when
        final Optional<LocalDateTime> result = trigger.nextExecutionTime(null);

        // then
        assertTrue(result.isPresent());
        assertApproximately(now.plus(fifteenSeconds), result.get());
    }

    @Test
    public void testAfterAndEverySecondExecution() {
        // given
        final Trigger trigger = Trigger.afterAndEvery(fifteenSeconds, fourSeconds);

        // when
        final Optional<LocalDateTime> result = trigger.nextExecutionTime(lastExecution(now));

        // then
        assertTrue(result.isPresent());
        assertApproximately(now.plus(fourSeconds), result.get());
    }

    private ScheduledTaskResult lastExecution(LocalDateTime time) {
        return new ScheduledTaskResultImpl(time, time.plus(fourSeconds), time.plus(eightSeconds));
    }

    private void assertApproximately(LocalDateTime expected, LocalDateTime actual) {
        final long diff = Math.abs(ChronoUnit.MILLIS.between(expected, actual));
        if (diff > 100) {
            fail("The actual time was " + diff/1000.0 + " seconds of the expected time");
        }
    }
}
