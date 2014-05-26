package uk.ac.standrews.cs.digitising_scotland.util;

import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by graham on 02/05/2014.
 */
public class FilteredIteratorTest {

    @Test
    public void filterEvenNumbers1() {

        Iterator<Integer> evens = makeEvensIterator(new Integer[]{1, 2, 3, 4, 5, 6, 7});

        assertEquals(2, evens.next().intValue());
        assertEquals(4, evens.next().intValue());
        assertEquals(6, evens.next().intValue());
        assertFalse(evens.hasNext());
    }

    @Test
    public void filterEvenNumbers2() {

        Iterator<Integer> evens = makeEvensIterator(new Integer[]{1, 2, 3, 4, 5, 6});

        assertEquals(2, evens.next().intValue());
        assertEquals(4, evens.next().intValue());
        assertEquals(6, evens.next().intValue());
        assertFalse(evens.hasNext());
    }

    @Test
    public void filterEvenNumbers3() {

        Iterator<Integer> evens = makeEvensIterator(new Integer[]{2, 3, 4, 5, 6, 7});

        assertEquals(2, evens.next().intValue());
        assertEquals(4, evens.next().intValue());
        assertEquals(6, evens.next().intValue());
        assertFalse(evens.hasNext());
    }

    @Test
    public void filterEvenNumbers4() {

        Iterator<Integer> evens = makeEvensIterator(new Integer[]{1, 3, 5, 7});

        assertFalse(evens.hasNext());
    }

    @Test
    public void filterEvenNumbers5() {

        Iterator<Integer> evens = makeEvensIterator(new Integer[]{2, 4, 6});

        assertEquals(2, evens.next().intValue());
        assertEquals(4, evens.next().intValue());
        assertEquals(6, evens.next().intValue());
        assertFalse(evens.hasNext());
    }

    @Test
    public void filterEvenNumbers6() {

        Iterator<Integer> evens = makeEvensIterator(new Integer[]{2});

        assertEquals(2, evens.next().intValue());
        assertFalse(evens.hasNext());
    }

    @Test
    public void filterEvenNumbers7() {

        Iterator<Integer> evens = makeEvensIterator(new Integer[]{3});

        assertFalse(evens.hasNext());
    }

    @Test
    public void filterEvenNumbers8() {

        Iterator<Integer> evens = makeEvensIterator(new Integer[]{});

        assertFalse(evens.hasNext());
    }

    private Iterator<Integer> makeEvensIterator(Integer[] array) {

        Iterator<Integer> original = new ArrayIterator<>(array);
        Condition<Integer> even_filter = new Condition<Integer>(){public boolean test(Integer t) { return t%2 == 0; }};
        return new FilteredIterator<>(original, even_filter);
    }
}
