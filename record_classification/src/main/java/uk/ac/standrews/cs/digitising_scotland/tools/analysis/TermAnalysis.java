package uk.ac.standrews.cs.digitising_scotland.tools.analysis;
///*
// * 
// */
//package uk.ac.standrews.cs.digitising_scotland.tools.analysis;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.io.StringReader;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map.Entry;
//import java.util.Set;
//
//import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
//import org.apache.lucene.util.Version;
//import org.apache.mahout.classifier.DatasetConfiguration;
//
//import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
//
//import com.google.common.base.Charsets;
//import com.google.common.collect.ConcurrentHashMultiset;
//import com.google.common.collect.HashMultiset;
//import com.google.common.collect.Multiset;
//import com.google.common.collect.Multisets;
//import com.google.common.io.Files;
//
///**
// * The Class TermAnalysis.
// */
//public class TermAnalysis {
//
//    private int termClassCoOccurance = 0; // number of times term and classVar co-occur
//    private int termWithoutClass = 0; // number of times term occurs without classVar
//    private int classWithoutTerm = 0; // number of times classVar occurs without term
//    private int totalNumberOfDocuments = 0; // total number of documents
//    private int noClassOrTerm = 0; // number of times neither c or t occur.
//
//    /**
//     * Gets the number of times term and classVar co-occur.
//     *
//     * @return the termClassCoOccurance
//     */
//    public int getTermClassCoOccurance() {
//
//        return termClassCoOccurance;
//    }
//
//    /**
//     *  Get the number of times term occurs without classVar.
//     *
//     * @return the termWithoutClass
//     */
//    public int getNumberOfTermsWithoutClass() {
//
//        return termWithoutClass;
//    }
//
//    /**
//     * Gets the number of times classVar occurs without term.
//     *
//     * @return the classWithoutTerm
//     */
//    public int getNumberOfClassWithoutTerm() {
//
//        return classWithoutTerm;
//    }
//
//    /**
//     * Gets the n.
//     *
//     * @return the totalNumberOfDocuments
//     */
//    public int getTotalNumberDocuments() {
//
//        return totalNumberOfDocuments;
//    }
//
//    /**
//     * Gets the number of times neither class or term occur.
//
//     *
//     * @return the noClassOrTerm
//     */
//    public int getNumberOfNoClassOrTerm() {
//
//        return noClassOrTerm;
//    }
//
//    private HashMap<String, Integer> uniqueWords;
//    private HashMap<String, Integer> allClasses;
//    private HashMap<String, ArrayList<String>> allDocuments;
//    private int[][] wordMatrix;
//    private File input;
//    private static final Version LUCENE_VERSION = Version.LUCENE_36;
//    private final Analyzer analyzer = new StandardAnalyzer(LUCENE_VERSION);
//    private Multiset<String> words;
//    private HashMap<String, Term> allTerms;
//
//    /**
//     * Calculates all the metrics related to a term, including CHI^@ and MI.
//     *
//     * @param input the input
//     * @throws IOException Signals that an I/O exception has occurred.
//     */
//    public TermAnalysis(final File input) throws IOException {
//
//        this.input = input;
//        this.input = new File(this.input.getAbsolutePath());
//        totalNumberOfDocuments = getNumberOfLines();
//        allDocuments = getAllDocuments();
//
//        words = ConcurrentHashMultiset.create();
//        BufferedReader reader = Files.newReader(new File("resources/allContent.txt"), Charsets.UTF_8);
//        Multiset<String> overallCounts = HashMultiset.create();
//        countWords(analyzer, words, reader, overallCounts);
//
//        calculateTerms();
//        getAllTerms();
//    }
//
//    /**
//     * Returns the mutual information for the given term in the given class.
//     *
//     * @param term term to check MI for.
//     * @param classVar Check with this class.
//     * @return the mutal infomeration
//     */
//    public double getMutalInfomeration(final String term, final String classVar) {
//
//        if (uniqueWords.get(term) == null || allClasses.get(classVar) == null) { return 0.0; }
//        int termID = uniqueWords.get(term);
//        int classID = allClasses.get(classVar);
//        termClassCoOccurance = getA(termID, classID);
//        termWithoutClass = getB(termID, classID);
//        classWithoutTerm = getC(term, classVar);
//        noClassOrTerm = getD(term, classVar);
//
//        double mutualInfo = 0;
//        double var1 = termClassCoOccurance * totalNumberOfDocuments;
//        double var2 = termClassCoOccurance + classWithoutTerm;
//        double var3 = termClassCoOccurance + termWithoutClass;
//
//        mutualInfo = Math.log(var1 / (var2 * var3));
//
//        return mutualInfo;
//    }
//
//    /**
//     * Returns the mutual information for the given term in the given class.
//     *
//     * @param term term to check MI for.
//     * @param classVar Check with this class.
//     * @return the mutal infomeration
//     */
//    public double getMutalInfomeration(final int termID, final String classVar) {
//
//        HashMap<Integer, String> uniqueWordsIntMapped = getUniqueWordReversed();
//        String term = uniqueWordsIntMapped.get(termID);
//        if (uniqueWords.get(term) == null || allClasses.get(classVar) == null) { return 0.0; }
//        int classID = allClasses.get(classVar);
//        termClassCoOccurance = getA(termID, classID);
//        termWithoutClass = getB(termID, classID);
//        classWithoutTerm = getC(term, classVar);
//        noClassOrTerm = getD(term, classVar);
//
//        double mutualInfo = 0;
//        double var1 = termClassCoOccurance * totalNumberOfDocuments;
//        double var2 = termClassCoOccurance + classWithoutTerm;
//        double var3 = termClassCoOccurance + termWithoutClass;
//
//        mutualInfo = Math.log(var1 / (var2 * var3));
//
//        return mutualInfo;
//    }
//
//    private HashMap<Integer, String> getUniqueWordReversed() {
//
//        Set<Entry<String, Integer>> wordMap = uniqueWords.entrySet();
//        HashMap<Integer, String> intMap = new HashMap<Integer, String>();
//
//        for (Entry<String, Integer> entry : wordMap) {
//            intMap.put(entry.getValue(), entry.getKey());
//        }
//        return intMap;
//    }
//
//    public int getTermClassCoOccurance(String term, String className) {
//
//        int termID = uniqueWords.get(term);
//        int classID = allClasses.get(className);
//        return getA(termID, classID);
//    }
//
//    public int calculateTermWithNoCoOccurance(String term, String className) {
//
//        int termID = uniqueWords.get(term);
//        int classID = allClasses.get(className);
//        return getB(termID, classID);
//    }
//
//    public int calculateclassWithoutTerm(String term, String className) {
//
//        return getC(term, className);
//    }
//
//    public int calculateTermClassCoOccurance(String term, String className) {
//
//        return getD(term, className);
//    }
//
//    public int getTermID(String term) {
//
//        return uniqueWords.get(term);
//    }
//
//    public int getClassID(String classVar) {
//
//        return allClasses.get(classVar);
//    }
//
//    /**
//     * Gets the max mutual information.
//     *
//     * @param term the term
//     * @return the max mutual information
//     */
//    public double getMaxMutualInformation(final String term) {
//
//        Object[] classes = allClasses.keySet().toArray();
//        double maxMI = 0;
//
//        for (int i = 0; i < allClasses.size(); i++) {
//            termClassCoOccurance = getA(uniqueWords.get(term), allClasses.get(classes[i].toString()));
//            classWithoutTerm = getC(term, classes[i].toString());
//
//            if (getMutalInfomeration(term, classes[i].toString()) > 0) {
//                if (getMutalInfomeration(term, classes[i].toString()) > maxMI) {
//                    maxMI = getMutalInfomeration(term, classes[i].toString());
//                }
//            }
//        }
//
//        return maxMI;
//
//    }
//
//    /**
//     * Gets the max chi.
//     *
//     * @param term the term
//     * @return the max chi
//     */
//    public double getMaxCHI(final String term) {
//
//        Object[] classes = allClasses.keySet().toArray();
//        double maxCHI = 0;
//
//        for (int i = 0; i < allClasses.size(); i++) {
//            termClassCoOccurance = getA(uniqueWords.get(term), allClasses.get(classes[i].toString()));
//            classWithoutTerm = getC(term, classes[i].toString());
//
//            if (getCHI(term, classes[i].toString()) > 0) {
//                if (getCHI(term, classes[i].toString()) > maxCHI) {
//                    maxCHI = getCHI(term, classes[i].toString());
//                }
//            }
//        }
//        return maxCHI;
//    }
//
//    private int getD(final String term, final String classVar) {
//
//        return (totalNumberOfDocuments - getB(uniqueWords.get(term), allClasses.get(classVar)) - getC(term, classVar));
//    }
//
//    /**
//     * Count words.
//     *
//     * @param analyzer the analyzer
//     * @param words the words
//     * @param in the in
//     * @param overallCounts the overall counts
//     * @throws IOException Signals that an I/O exception has occurred.
//     */
//    public static void countWords(final Analyzer analyzer, final Collection<String> words, final Reader in, final Multiset<String> overallCounts) throws IOException {
//
//        TokenStream ts = analyzer.reusableTokenStream("text", in);
//        ts.addAttribute(CharTermAttribute.class);
//        ts.reset();
//        while (ts.incrementToken()) {
//
//            String s = ts.getAttribute(CharTermAttribute.class).toString();
//            words.add(s);
//        }
//        overallCounts.addAll(words);
//    }
//
//    /**
//     * Calculate terms.
//     */
//    public void calculateTerms() {
//
//        uniqueWords = calculateUniqueWords();
//        allClasses = calculateAllClasses();
//        wordMatrix = new int[allClasses.size()][uniqueWords.size()];
//        wordMatrix = populateMatrix(wordMatrix);
//    }
//
//    private int getC(final String term2, final String classVar2) {
//
//        int total = 0;
//
//        ArrayList<String> documents = allDocuments.get(classVar2);
//
//        Object[] docs = documents.toArray();
//        for (int i = 0; i < docs.length; i++) {
//            if (!docs[i].toString().contains(term2)) {
//                total++;
//            }
//        }
//
//        return total;
//    }
//
//    private int getB(final int termID, final int classID) {
//
//        int total = 0;
//        for (int i = 0; i < wordMatrix.length; i++) {
//            total = total + wordMatrix[i][termID];
//        }
//        return total - getA(termID, classID);
//    }
//
//    private int getA(final int termID, final int classID) {
//
//        return wordMatrix[classID][termID];
//    }
//
//    /**
//     * Gets the chi.
//     *
//     * @param term the term
//     * @param classVar the class var
//     * @return the chi
//     */
//    public double getCHI(final String term, final String classVar) {
//
//        long x2 = 0;
//        long var1 = (long) (totalNumberOfDocuments * Math.pow((termClassCoOccurance * noClassOrTerm - classWithoutTerm * termWithoutClass), 2));
//        long var2 = (long) (termClassCoOccurance + classWithoutTerm) * (long) (termWithoutClass + noClassOrTerm) * (long) (termClassCoOccurance + termWithoutClass) * (long) (classWithoutTerm + noClassOrTerm);
//        if (var2 != 0) {
//            x2 = var1 / var2;
//        }
//        else {
//            x2 = 0;
//        }
//
//        return x2;
//    }
//
//    private HashMap<String, ArrayList<String>> getAllDocuments() {
//
//        HashMap<String, ArrayList<String>> allDocuments = new HashMap<String, ArrayList<String>>(DatasetConfiguration.numberOfClasses());
//        StringBuffer allContent = new StringBuffer();
//        BufferedReader reader = null;
//        System.out.println("using: " + input.getAbsolutePath());
//        try {
//            reader = new BufferedReader(new InputStreamReader(new FileInputStream(input), Charsets.UTF_8));
//            String line = "";
//            while ((line = reader.readLine()) != null) {
//                if (line.length() != 0) {
//                    String classVar = line.split("\t")[0];
//                    String content = line.split("\t")[1];
//                    allContent.append("\n" + content);
//                    if (allDocuments.containsKey(classVar)) {
//                        allDocuments.get(classVar).add(content);
//                    }
//                    else {
//                        allDocuments.put(classVar, new ArrayList<String>());
//                        allDocuments.get(classVar).add(content);
//                    }
//                }
//            }
//        }
//        catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        if (!new File("resources/allContent.txt").exists()) {
//            Utils.writeToFile(allContent.toString(), "resources/allContent.txt");
//        }
//        System.out.println("finished Term Analysis");
//        return allDocuments;
//    }
//
//    public int[][] getWordMatrix() {
//
//        calculateTerms();
//        calculateAllClasses();
//        wordMatrix = new int[allClasses.size()][uniqueWords.size()];
//        wordMatrix = populateMatrix(wordMatrix);
//        return wordMatrix;
//    }
//
//    private int[][] populateMatrix(final int[][] matrix2) {
//
//        wordMatrix = matrix2;
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new InputStreamReader(new FileInputStream(input), Charsets.UTF_8));
//            String line = "";
//            while ((line = reader.readLine()) != null) {
//                String classification = line.split("\t")[0];
//                String allTerms = line.split("\t")[1];
//
//                List<String> result = new ArrayList<String>();
//                TokenStream stream = analyzer.reusableTokenStream("text", new StringReader(allTerms));
//                try {
//                    while (stream.incrementToken()) {
//                        result.add(stream.getAttribute(CharTermAttribute.class).toString());
//                    }
//                }
//                catch (IOException e) {
//                    // not thrown b/c we're using a string reader...
//                }
//
//                Object[] allTermsArr = result.toArray();
//
//                for (int i = 0; i < allTermsArr.length; i++) {
//                    if (allClasses.get(classification) != null && uniqueWords.get(allTermsArr[i]) != null) {
//                        wordMatrix[allClasses.get(classification)][uniqueWords.get(allTermsArr[i].toString())] = wordMatrix[allClasses.get(classification)][uniqueWords.get(allTermsArr[i].toString())] + 1;
//                    }
//                }
//            }
//        }
//        catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return wordMatrix;
//    }
//
//    /**
//     * Returns a hashmap containing all the classes in the training set and an associated index.
//     * 
//     * @return HashMap<String, Integer> Key is the class, Value is the index in the values matrix.
//     */
//    private HashMap<String, Integer> calculateAllClasses() {
//
//        HashMap<String, Integer> allClasses = new HashMap<String, Integer>();
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new InputStreamReader(new FileInputStream(input), Charsets.UTF_8));
//            String line = "";
//            int i = 0;
//            while ((line = reader.readLine()) != null) {
//                line = line.split("\t")[0];
//                if (allClasses.get(line) == null) {
//                    allClasses.put(line, i);
//                    i++;
//                }
//            }
//        }
//        catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                }
//                catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return allClasses;
//    }
//
//    /**
//     * Returns the allClasses hashMap.
//     * 
//     * @return allClasses
//     */
//    public HashMap<String, Integer> getAllClasses() {
//
//        return allClasses;
//    }
//
//    /**
//     * Returns a hashmap containing all the unique words in the training set and an associated index.
//     * 
//     * @return HashMap<String, Integer> Key is the word, Value is the index in the values matrix.
//     */
//    private HashMap<String, Integer> calculateUniqueWords() {
//
//        HashMap<String, Integer> uniqueWords = new HashMap<String, Integer>();
//
//        int i = 0;
//        for (String word : Multisets.copyHighestCountFirst(words).elementSet()) {
//            uniqueWords.put(word, i);
//            i++;
//        }
//
//        return uniqueWords;
//    }
//
//    /**
//     * Retuns a hashmap of unique words. Map is of word to ID.
//     * 
//     * @return uniqueWords hashmap.
//     */
//    public HashMap<String, Integer> getUniqueWords() {
//
//        return uniqueWords;
//    }
//
//    /**
//     * Gets the average mi.
//     *
//     * @param term the term
//     * @return the average mi
//     */
//    public double getAverageMI(final String term) {
//
//        double mi = 0;
//
//        Object[] classes = allClasses.keySet().toArray();
//        for (int i = 0; i < allClasses.size(); i++) {
//            termClassCoOccurance = getA(uniqueWords.get(term), allClasses.get(classes[i].toString()));
//            classWithoutTerm = getC(term, classes[i].toString());
//            if (getMutalInfomeration(term, classes[i].toString()) > 0) {
//                mi = mi + ((termClassCoOccurance + classWithoutTerm) * getMutalInfomeration(term, classes[i].toString()));
//            }
//            else {
//                mi = mi + ((termClassCoOccurance + classWithoutTerm) * 0);
//            }
//        }
//
//        return mi;
//    }
//
//    /**
//     * Gets the average chi.
//     *
//     * @param term the term
//     * @return the average chi
//     */
//    public double getAverageCHI(final String term) {
//
//        double chi = 0;
//
//        Object[] classes = allClasses.keySet().toArray();
//        for (int i = 0; i < allClasses.size(); i++) {
//            termClassCoOccurance = getA(uniqueWords.get(term), allClasses.get(classes[i].toString()));
//            classWithoutTerm = getC(term, classes[i].toString());
//            if (getCHI(term, classes[i].toString()) > 0) {
//                chi = chi + ((termClassCoOccurance + classWithoutTerm) * getCHI(term, classes[i].toString()));
//            }
//            else {
//                chi = chi + ((termClassCoOccurance + classWithoutTerm) * 0);
//            }
//        }
//
//        return chi;
//    }
//
//    /**
//     * Gets the all terms.
//     *
//     * @return the all terms
//     */
//    public HashMap<String, Term> getAllTerms() {
//
//        allTerms = new HashMap<String, Term>();
//        Object[] words = uniqueWords.keySet().toArray();
//        Object[] classes = allClasses.keySet().toArray();
//
//        for (int i = 0; i < words.length; i++) {
//            for (int j = 0; j < classes.length; j++) {
//                if (getMutalInfomeration(words[i].toString(), classes[j].toString()) >= 0) {
//
//                    Term thisTerm = new Term(words[i].toString());
//                    thisTerm.setAverageMI(getAverageMI(words[i].toString()));
//                    thisTerm.setAverageCHI(getAverageCHI(words[i].toString()));
//                    thisTerm.setMaxMI(getMaxMutualInformation(words[i].toString()));
//                    thisTerm.setMaxCHI(getMaxCHI(words[i].toString()));
//
//                    allTerms.put(words[i].toString(), thisTerm);
//                }
//
//            }
//        }
//
//        return allTerms;
//    }
//
//    /**
//     * Best Terms Algorithm See: Best Terms paper.
//     * 
//     * @param targetClass
//     *            The class that we want to select the best terms for.
//     * @param threshold
//     *            A given threshold of terms to delete.
//     */
//
//    //TODO This needs to be finished
//    //    public void bestTermsSelection(final String targetClass, final double threshold) {
//    //
//    //        Set<String> featuresPositive = new HashSet<String>();
//    //        List<String> featuresNegative = new ArrayList<String>();
//    //        List dc = new ArrayList();
//    //        Set df = new HashSet();
//    //        List<String> fd = new ArrayList<String>();
//    //
//    //        for (int i = 0; i < allClasses.keySet().toArray().length; i++) {
//    //            // DC is the set of in-class documents
//    //            for (int j = 0; j < allDocuments.get(allClasses.keySet().toArray()[i]).size(); j++) {
//    //                dc.add(allDocuments.get(i).get(j));
//    //            }
//    //            // for each document in DC
//    //            for (int j = 0; j < dc.size(); j++) {
//    //                ArrayList document = (ArrayList) dc.get(j);
//    //                for (int k = 0; k < document.size(); k++) {
//    //                    String word = (String) dc.get(k);
//    //                    if (getWordProbability(word) > 0.5 * threshold + 0.5 * getClassProbability(allClasses.keySet().toArray()[i])) {
//    //                        fd.add(word);
//    //                    }
//    //
//    //                    if (fd.size() != 0) {
//    //                        ArrayList<String> wordsInClass = getAllWordsInClass(allClasses.keySet().toArray()[i].toString());
//    //                        for (int l = 0; l < wordsInClass.size(); l++) {
//    //                            double wMaxChi = getMaxCHI(wordsInClass.get(l));
//    //                            for (int m = 0; m < fd.size(); m++) {
//    //                                String w = fd.get(l);
//    //                                if (wMaxChi > getMaxCHI(w)) {
//    //                                    featuresPositive.add(wordsInClass.get(l));
//    //                                }
//    //                            }
//    //                        }
//    //                    }
//    //                }
//    //                Object[] fp = featuresPositive.toArray();
//    //
//    //                ArrayList<String>[] allDocs = (ArrayList<String>[]) allDocuments.values().toArray();
//    //
//    //                for (int k = 0; k < fp.length; k++) {
//    //                    for (int k2 = 0; k2 < allDocs.length; k2++) {
//    //                        for (int l = 0; l < allDocs[k2].size(); l++) {
//    //                            if (allDocs[k2].contains(fp[k].toString())) {
//    //                                df.add(allDocs[k2]);
//    //                            }
//    //                        }
//    //                    }
//    //                }
//    //
//    //                for (int k = 0; k < allDocs.length; k++) {
//    //
//    //                }
//    //            }
//    //        }
//    //    }
//
//    private ArrayList<String> getAllWordsInClass(final Object object) {
//
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    private double getClassProbability(final Object object) {
//
//        // TODO Auto-generated method stub
//        return 0;
//    }
//
//    private int getWordProbability(final String word) {
//
//        // TODO Auto-generated method stub
//        return 0;
//    }
//
//    private int getNumberOfLines() throws IOException {
//
//        return Utils.getNumberOfLines(input.getAbsoluteFile());
//    }
//
//}
