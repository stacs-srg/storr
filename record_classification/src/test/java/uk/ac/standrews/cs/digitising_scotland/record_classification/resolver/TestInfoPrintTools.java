package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;

/**
 *
 * Created by fraserdunlop on 08/10/2014 at 15:02.
 */
public class TestInfoPrintTools {
    public void printTestSubjectInfo(Object testClass, Object testSubject) {
        System.out.println(testClass.getClass().getSimpleName() + " is testing " + testSubject.getClass().getSimpleName());
    }
}
