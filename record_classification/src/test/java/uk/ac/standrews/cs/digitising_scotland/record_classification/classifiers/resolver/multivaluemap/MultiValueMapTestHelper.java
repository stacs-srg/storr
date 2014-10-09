package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.multivaluemap;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.generic.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Holds a MultiValueMap and CodeDictionary. Allows mock entries to be added to map using CodeDictionary.
 * Created by fraserdunlop on 07/10/2014 at 12:03.
 */
public class MultiValueMapTestHelper {

    private final CodeDictionary codeDictionary;
    private final MultiValueMap<Code, Classification> map;

    public MultiValueMap<Code, Classification> getMap() {
        return map;
    }

    public MultiValueMapTestHelper() throws IOException {
        File codeDictionaryFile = new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile());
        codeDictionary = new CodeDictionary(codeDictionaryFile);
        map = new MultiValueMap<>(new HashMap<Code,List<Classification>>());
    }

    /**
     * Adds the mock entry to map.
     * @param string the string
     * @param codeAsString the code
     * @param conf the conf
     */
    public void addMockEntryToMatrix(final String string, final String codeAsString, final double conf) throws CodeNotValidException {
        TokenSet tokenSet = new TokenSet(string);
        Code code = codeDictionary.getCode(codeAsString);
        map.add(code, new Classification(code, tokenSet, conf));
    }
}
