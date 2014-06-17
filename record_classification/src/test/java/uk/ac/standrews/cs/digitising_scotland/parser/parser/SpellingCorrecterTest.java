package uk.ac.standrews.cs.digitising_scotland.parser.parser;
///*
// *
// */
//package uk.ac.standrews.cs.digitising_scotland.parser.parser;
//
//import static org.junit.Assert.assertEquals;
//
//import java.io.IOException;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import uk.ac.standrews.cs.digitising_scotland.tools.SpellingMistakeFactory;
//
///**
// * The Class SpellingCorrecterTest.
// */
//public class SpellingCorrecterTest {
//
//    private SpellingCorrecter sc;
//    private long startTime;
//    private long endTime;
//
//    /**
//     * Setup.
//     *
//     * @throws IOException Signals that an I/O exception has occurred.
//     */
//    @Before
//    public void setup() throws IOException {
//
//        startTime = System.nanoTime();
//        sc = new SpellingCorrecter("dictSmall.txt");
//
//    }
//
//    /**
//     * Test.
//     *
//     * @throws IOException Signals that an I/O exception has occurred.
//     */
//    @Test
//    public void test() throws IOException {
//
//        assertEquals("tremens", sc.correct("tremmens"));
//        assertEquals("aphtha", sc.correct("aphthae"));
//        assertEquals("trachealis", sc.correct("trahealis"));
//        assertEquals("menorrhagia", sc.correct("menorrhagia"));
//        assertEquals("haemorrhage", sc.correct("heamorrhage"));
//        assertEquals("phthisis", sc.correct("pthisis"));
//        endTime = System.nanoTime();
//        long duration = endTime - startTime;
//        System.out.println(duration / 1000000 + "ms");
//    }
//
//    /**
//     * Test2.
//     *
//     * @throws IOException Signals that an I/O exception has occurred.
//     */
//    @Test
//    public void test2() throws IOException {
//
//        startTime = System.nanoTime();
//
//        SpellingMistakeFactory smf = new SpellingMistakeFactory();
//
//        assertEquals("measles", sc.correct(smf.addMistakeTypo("measles")));
//        assertEquals("abscess", sc.correct(smf.addMistakeTypo("abscess")));
//        assertEquals("aneurysm", sc.correct(smf.addMistakeTypo("aneurysm")));
//        assertEquals("hemoptysis", sc.correct(smf.addMistakeTypo("hemoptysis")));
//        assertEquals("sedentary", sc.correct(smf.addMistakeTypo("sedentary")));
//        assertEquals("oophorectomy", sc.correct(smf.addMistakeTypo("oophorectomy")));
//
//        endTime = System.nanoTime();
//        long duration = endTime - startTime;
//        System.out.println(duration / 1000000 + "ms");
//    }
//
//}
