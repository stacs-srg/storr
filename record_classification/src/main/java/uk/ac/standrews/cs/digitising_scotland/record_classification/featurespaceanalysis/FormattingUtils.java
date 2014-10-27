package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;

/**
 *
 * Created by fraserdunlop on 27/10/2014 at 12:24.
 */
public class FormattingUtils {

    public static String rmLastChar(final String string) {
        if(string.length()>2)
            return string.subSequence(0, string.length() - 2).toString();
        else return string;
    }
    public static String paddedSeparator(final String sep,final int padEachSideBy) {
        String pad = repeatConcatString(" ", padEachSideBy);
        return pad + sep + pad;
    }

    public static String repeatConcatString(final String str,final int reps) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < reps; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}
