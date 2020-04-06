package dev.rico.internal.core.http;

import dev.rico.core.http.ByteUnit;

import java.util.Objects;

public enum BinaryByteUnit implements ByteUnit {

    BYTE("B", "byte", 0),
    KIBIBYTE("KiB", "kibibyte", 1),
    MEBIBYTE("MiB", "mebibyte", 2),
    GIBIBYTE("GiB", "gibibyte", 3),
    TEBIBYTE("TiB", "tebibyte", 4),
    PEBIBYTE("PiB", "pebibyte", 5);

    private static final int UNIT = 1024;

    private final String shortName;

    private final String name;

    private final int exponent;

    BinaryByteUnit(final String shortName, final String name, final int exponent) {
        this.shortName = shortName;
        this.name = name;
        this.exponent = exponent;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public String getName() {
        return name;
    }

    public double convertBytesToUnit(final long byteCount) {
        if(Objects.equals(this, DecimalByteUnit.BYTE)) {
            return byteCount;
        }
        return byteCount / Math.pow(UNIT, exponent);
    }

    public double convertToUnit(final double countInUnit, final ByteUnit unit) {
        if(Objects.equals(this, BinaryByteUnit.BYTE)) {
            return unit.convertBytesToUnit((long) countInUnit);
        }
        final long byteCount = (long) (countInUnit * Math.pow(UNIT, exponent));
        return unit.convertBytesToUnit(byteCount);
    }

    public static BinaryByteUnit findBestUnit(final long byteCount) {
        final BinaryByteUnit[] dictionary = {KIBIBYTE, MEBIBYTE, GIBIBYTE, TEBIBYTE, PEBIBYTE};
        if (byteCount < UNIT) return BYTE;

        int exp = (int) (Math.log(byteCount) / Math.log(UNIT));
        return dictionary[Math.min(Math.max(0, exp-1), dictionary.length -1)];
    }
}
