/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.multivaluemap;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.generic.MultiValueMap;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

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
        map = new MultiValueMap<>(new HashMap<Code, List<Classification>>());
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
