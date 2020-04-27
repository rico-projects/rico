package dev.rico.internal.core.http;

import dev.rico.core.http.ByteUnit;

public enum DecimalByteUnit implements ByteUnit {

    BYTE("B", "byte", 0),
    KILOBYTE("KB", "kilobyte", 1),
    MEGABYTE("MB", "megabyte", 2),
    GIGABYTE("GB", "gigabyte", 3),
    TERABYTE("TB", "terabyte", 4),
    PEGTABYTE("PB", "petabyte", 5);

    private static final int UNIT = 1000;

    private final String shortName;

    private final String name;

    private final int exponent;

    DecimalByteUnit(final String shortName, final String name, final int exponent) {
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
        if(this == DecimalByteUnit.BYTE) {
            return byteCount;
        }
        return byteCount / Math.pow(UNIT, exponent);
    }

    public double convertToUnit(final double countInUnit, final ByteUnit unit) {
        if(this == DecimalByteUnit.BYTE) {
            return unit.convertBytesToUnit((long) countInUnit);
        }
        final long byteCount = (long) (countInUnit * Math.pow(UNIT, exponent));
        return unit.convertBytesToUnit(byteCount);
    }

    public static DecimalByteUnit findBestUnit(final long byteCount) {
        final DecimalByteUnit[] dictionary = { KILOBYTE, MEGABYTE, GIGABYTE, TERABYTE, PEGTABYTE };
        if (byteCount < UNIT) return BYTE;

        int exp = (int) (Math.log(byteCount) / Math.log(UNIT));
        return dictionary[Math.min(Math.max(0, exp-1), dictionary.length -1)];
    }
}
