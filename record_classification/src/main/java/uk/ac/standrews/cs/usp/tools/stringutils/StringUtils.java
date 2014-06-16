package uk.ac.standrews.cs.usp.tools.stringutils;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import uk.ac.standrews.cs.usp.parser.machinelearning.tokenizing.TokenStreamIterator;
import uk.ac.standrews.cs.usp.parser.machinelearning.tokenizing.TokenStreamIteratorFactory;

import com.google.common.collect.TreeMultiset;

public class StringUtils {

    /**
     * Creates a {@link TreeMultiset} from a string. The input string is tokenised and then added to the set.
     * @param string String to be tokenised and added to the multiset
     * @return a multiset with each token from the input string added to the set
     */
    public static TreeMultiset<String> getMultiSetFromString(final String string) {

        TreeMultiset<String> multiset = TreeMultiset.create();
        TokenStreamIterator<CharTermAttribute> it = TokenStreamIteratorFactory.newTokenStreamIterator(string);
        while (it.hasNext()) {
            multiset.add(it.next().toString());
        }

        return multiset;
    }

}
