package dev.rico.core.http;

import dev.rico.internal.core.http.BinaryByteUnit;
import dev.rico.internal.core.http.DecimalByteUnit;

/**
 * A {@code ByteUnit} represents a byte unit that provides utility methods to convert across units,
 * and to get metadata (like the name) of the unit.
 */
public interface ByteUnit {

    /**
     * Returns the short name of the unit. The short name is defined as shortcut like "KB" or "KiB".
     *
     * @return the short name
     */
    String getShortName();

    /**
     * Returns the name of the unit. The name is returned in lowercase.
     *
     * @return the name
     */
    String getName();

    /**
     * Converts the given byte count in a count based on this unit.
     * Example: 1024 byte will be converted in 1.0 for the "KiB" unit
     *
     * @param byteCount the byte count
     * @return the count for this unit
     */
    double convertBytesToUnit(final long byteCount);

    /**
     * Converts the given count for this unit in the count for another unit.
     * Example: 1 KiB will be converted in 1.024 for the "KB" unit
     *
     * @param countInUnit the count based on this unit
     * @param unit        the unit in that the count should be converted
     * @return the count for the given unit
     */
    double convertToUnit(final double countInUnit, final ByteUnit unit);

    /**
     * Returns a good matching unit based on the given byte count. By the binary parameter you can specifiy if you want
     * a binary or decimal based unit.
     *
     * @param byteCount the byte count
     * @param binary    true if you need a binary unit, false if you need a decimal unit
     * @return the best matching unit
     */
    static ByteUnit findBestUnit(final long byteCount, boolean binary) {
        if (binary) {
            return BinaryByteUnit.findBestUnit(byteCount);
        } else {
            return DecimalByteUnit.findBestUnit(byteCount);
        }
    }
}
