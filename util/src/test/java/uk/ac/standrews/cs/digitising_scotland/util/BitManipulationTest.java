package uk.ac.standrews.cs.digitising_scotland.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import uk.ac.standrews.cs.digitising_scotland.util.BitManipulation;

@RunWith(Parameterized.class)
public class BitManipulationTest {

    private static final int BITS_PER_BYTE = 8;

    @Parameters
    public static Collection<Object[]> generateData() {

        return Arrays.asList(new Object[][]{{(byte) 0}, {(byte) 1}, {(byte) 2}, {(byte) 8}, {(byte) 32}, {(byte) -1}, {Byte.MAX_VALUE}, {Byte.MIN_VALUE}});
    }

    private byte bits;

    public BitManipulationTest(final byte bits) {

        this.bits = bits;
    }

    @Test
    public void check() {

        assertAllBitsSetCorrectlyTogether(true);
        assertAllBitsSetCorrectlyTogether(false);

        assertEachBitSetCorrectlyIndividually();

        assertSettingEachBitDoesntAffectOtherBits(true);
        assertSettingEachBitDoesntAffectOtherBits(false);
    }

    private void assertAllBitsSetCorrectlyTogether(final boolean bit) {

        setAllBits(bit);
        assertAllBitsAre(bit);
    }

    private void setAllBits(final boolean bit) {

        for (int bit_position = 0; bit_position < BITS_PER_BYTE; bit_position++) {
            writeBit(bit, bit_position);
        }
    }

    private void assertAllBitsAre(final boolean bit) {

        for (int bit_position = 0; bit_position < BITS_PER_BYTE; bit_position++) {
            assertBitIs(bit_position, bit);
        }
    }

    private void assertEachBitSetCorrectlyIndividually() {

        for (int bit_position = 0; bit_position < BITS_PER_BYTE; bit_position++) {
            assertBitSetCorrectly(bit_position, true);
            assertBitSetCorrectly(bit_position, false);
        }
    }

    private void assertBitSetCorrectly(final int bit_position, final boolean bit) {

        writeBit(bit, bit_position);
        assertBitIs(bit_position, bit);
    }

    private void assertSettingEachBitDoesntAffectOtherBits(final boolean initial_bit) {

        setAllBits(initial_bit);
        final byte start_pattern = bits;

        for (int bit_position = 0; bit_position < BITS_PER_BYTE; bit_position++) {
            assertSettingBitDoesntAffectOtherBits(start_pattern, bit_position);
        }
    }

    private void assertSettingBitDoesntAffectOtherBits(final byte start_pattern, final int bit_position) {

        final boolean bit = BitManipulation.readBit(bits, bit_position);
        writeBit(!bit, bit_position);
        writeBit(bit, bit_position);
        assertEquals(bits, start_pattern);
    }

    private void assertBitIs(final int bit_position, final boolean bit) {

        assertEquals(bit, BitManipulation.readBit(bits, bit_position));
    }

    private void writeBit(final boolean bit, final int bit_position) {

        bits = BitManipulation.writeBit(bits, bit, bit_position);
    }
}
