package dev.rico.internal.core.http;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ByteUnitTests {

    @Test
    public void testReturnCorrectUnitForBytesInBinary() {
        //given
        final long bytes = 3;
        final boolean binary = true;

        //when
        final ByteUnit unit = ByteUnit.findBestUnit(bytes, binary);

        //than
        Assert.assertNotNull(unit);
        Assert.assertEquals(unit, ByteUnit.BYTE);
    }

    @Test
    public void testReturnCorrectUnitForBytesInDecimal() {
        //given
        final long bytes = 3;
        final boolean binary = false;

        //when
        final ByteUnit unit = ByteUnit.findBestUnit(bytes, binary);

        //than
        Assert.assertNotNull(unit);
        Assert.assertEquals(unit, ByteUnit.BYTE);
    }

    @Test
    public void testReturnCorrectUnitForBytesInBinaryForEdgeCase() {
        //given
        final long bytes = 1023;
        final boolean binary = true;

        //when
        final ByteUnit unit = ByteUnit.findBestUnit(bytes, binary);

        //than
        Assert.assertNotNull(unit);
        Assert.assertEquals(unit, ByteUnit.BYTE);
    }

    @Test
    public void testReturnCorrectUnitForKiloBytesInBinary() {
        //given
        final long bytes = 3234;
        final boolean binary = true;

        //when
        final ByteUnit unit = ByteUnit.findBestUnit(bytes, binary);

        //than
        Assert.assertNotNull(unit);
        Assert.assertEquals(unit, ByteUnit.KILOBYTE);
    }

    @Test
    public void testReturnCorrectUnitForKiloBytesInBinaryForMinEdgeCase() {
        //given
        final long bytes = 1024;
        final boolean binary = true;

        //when
        final ByteUnit unit = ByteUnit.findBestUnit(bytes, binary);

        //than
        Assert.assertNotNull(unit);
        Assert.assertEquals(unit, ByteUnit.KILOBYTE);
    }

    @Test
    public void testReturnCorrectUnitForKiloBytesInDecimalForMinEdgeCase() {
        //given
        final long bytes = 1000;
        final boolean binary = false;

        //when
        final ByteUnit unit = ByteUnit.findBestUnit(bytes, binary);

        //than
        Assert.assertNotNull(unit);
        Assert.assertEquals(unit, ByteUnit.KILOBYTE);
    }

    @Test
    public void testCalculationInKilobytesInBinary() {
        //given
        final long bytes = 1024;
        final boolean binary = true;
        final ByteUnit unit = ByteUnit.KILOBYTE;

        //when
        final double sizeInKb = unit.convertBytesToUnit(bytes, binary);

        //than
        Assert.assertEquals(sizeInKb, 1.0, 0.00001);
    }

    @Test
    public void testCalculationInKilobytesInBinary2() {
        //given
        final long bytes = 7293;
        final boolean binary = true;
        final ByteUnit unit = ByteUnit.KILOBYTE;

        //when
        final double sizeInKb = unit.convertBytesToUnit(bytes, binary);

        //than
        Assert.assertEquals(sizeInKb, 7.1220703125, 0.00001);
    }

    @Test
    public void testCalculationInKilobytesInBinary3() {
        //given
        final long bytes = 12;
        final boolean binary = true;
        final ByteUnit unit = ByteUnit.KILOBYTE;

        //when
        final double sizeInKb = unit.convertBytesToUnit(bytes, binary);

        //than
        Assert.assertEquals(sizeInKb, 0.01171875, 0.00001);
    }

    @Test
    public void testCalculationInKilobytesInDecimal() {
        //given
        final long bytes = 1024;
        final boolean binary = false;
        final ByteUnit unit = ByteUnit.KILOBYTE;

        //when
        final double sizeInKb = unit.convertBytesToUnit(bytes, binary);

        //than
        Assert.assertEquals(sizeInKb, 1.024, 0.00001);
    }

    @Test
    public void testCalculationInKilobytesInDecimal2() {
        //given
        final long bytes = 7293;
        final boolean binary = false;
        final ByteUnit unit = ByteUnit.KILOBYTE;

        //when
        final double sizeInKb = unit.convertBytesToUnit(bytes, binary);

        //than
        Assert.assertEquals(sizeInKb, 7.293, 0.00001);
    }

    @Test
    public void testCalculationInKilobytesInDecimal3() {
        //given
        final long bytes = 12;
        final boolean binary = false;
        final ByteUnit unit = ByteUnit.KILOBYTE;

        //when
        final double sizeInKb = unit.convertBytesToUnit(bytes, binary);

        //than
        Assert.assertEquals(sizeInKb, 0.012, 0.00001);
    }


    @Test
    public void testCalculationInMegabytesInBinary() {
        //given
        final long bytes = 1240000;
        final boolean binary = true;
        final ByteUnit unit = ByteUnit.MEGABYTE;

        //when
        final double sizeInKb = unit.convertBytesToUnit(bytes, binary);

        //than
        Assert.assertEquals(sizeInKb, 1.18255615234375, 0.00001);
    }

    @Test
    public void testCalculationInMegabytesInDecimal() {
        //given
        final long bytes = 1240000;
        final boolean binary = false;
        final ByteUnit unit = ByteUnit.MEGABYTE;

        //when
        final double sizeInKb = unit.convertBytesToUnit(bytes, binary);

        //than
        Assert.assertEquals(sizeInKb, 1.24, 0.00001);
    }

}
