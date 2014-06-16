//package uk.ac.standrews.cs.usp.parser.preprocessor;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Scanner;
//import java.util.TreeSet;
//
//import uk.ac.standrews.cs.usp.tools.Utils;
//
//import com.google.common.collect.TreeMultiset;
//
//public class EditDistanceClassifier {
//
//    private static LinkedList<InputTriple> list;
//
//    private HashMap<String, String> mappingCache;
//
//    /**
//     * Sets up new edit distance classifier.
//     */
//    public EditDistanceClassifier() {
//
//        list = new LinkedList<InputTriple>();
//
//        mappingCache = new HashMap<String, String>();
//    }
//
//    public static LinkedList<InputTriple> getList() {
//
//        return list;
//    }
//
//    public static void setList(LinkedList<InputTriple> list) {
//
//        EditDistanceClassifier.list = list;
//    }
//
//    /**
//     * Classifies a String to the nearest output class based on string similarity.
//     * @param input to classify.
//     * @param outputs list of possible outputs to classify to.
//     * @return output classification
//     */
//    public String classify(final String input, final List<String> outputs) {
//
//        if (input.trim().equals("")) { return "empty"; }
//        list = new LinkedList<InputTriple>();
//        String[] splitInput = input.split("\\s");
//        int bestOutput = 0;
//        double highestSimilarity = 0;
//
//        ArrayList<TreeMultiset<Double>> outputSets = new ArrayList<TreeMultiset<Double>>();
//
//        double averageScore = 0;
//
//        if (mappingCache.containsKey(input)) { return mappingCache.get(input); }
//
//        //for every output
//        for (int i = 0; i < outputs.size(); i++) {
//            String[] splitOutput = outputs.get(i).split("\\s");
//            averageScore = LevenshteinDistance.similarity(input, outputs.get(i));
//            TreeMultiset<Double> a = TreeMultiset.create();
//            outputSets.add(a);
//
//            //check each word in the output
//            for (int k = 0; k < splitOutput.length; k++) {
//                // against each word on the input
//                for (int j = 0; j < splitInput.length; j++) {
//
//                    double currentScore = LevenshteinDistance.similarity(splitInput[j], splitOutput[k]);
//
//                    // System.out.println("comparing " + splitInput[j] + " to " + splitOutput[k] + ". Currecnt score is " + currentScore);
//                    outputSets.get(i).add(currentScore);
//
//                    if (currentScore >= highestSimilarity) {
//                        highestSimilarity = currentScore;
//                        bestOutput = i;
//
//                    }
//
//                }
//
//            }
//
//            cache(input, outputs.get(bestOutput));
//            System.out.println("highest similarity: " + highestSimilarity);
//
//        }
//
//        if (outputSets != null && !outputSets.isEmpty()) {
//            Collections.sort(outputSets, new Comparator<TreeMultiset<Double>>() {
//
//                @Override
//                public int compare(final TreeMultiset<Double> o1, final TreeMultiset<Double> o2) {
//
//                    return o1.elementSet().last().compareTo(o2.elementSet().last());
//                }
//
//            });
//
//            Collections.reverse(outputSets);
//        }
//        TreeSet<StringSimilarityTripel> l = new TreeSet<StringSimilarityTripel>();
//
//        for (int j = 0; j < outputSets.size(); j++) {
//            l.add(new StringSimilarityTripel(j, outputSets.get(j).toArray(new Double[outputSets.get(j).size()])));
//        }
//
//        for (StringSimilarityTripel s : l) {
//            System.out.println(s.getStringSet());
//        }
//
//        return outputs.get(l.last().getId());
//    }
//
//    /**
//     * Adds string output class mapping to hashmap cache.
//     * @param input string input
//     * @param string output class
//     */
//    private void cache(final String input, final String string) {
//
//        mappingCache.put(input, string);
//    }
//
//    /**
//     * Calculates and prints out string similarity scores and classifications for a set of sample data.
//     * @param testingFiles
//     */
//    public void testWordSimilarityClassifier(final String testingFiles) {
//
//        EditDistanceClassifier classifier = new EditDistanceClassifier();
//        Object[] inputs = getInputs(testingFiles);
//        List<String> outputs = getEditDistanceLables(testingFiles);
//
//        double correct = 0;
//        double total = 0;
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < inputs.length; i++) {
//            InputTriple it = (InputTriple) inputs[i];
//
//            String classification = classifier.classify(it.getContent(), outputs);
//
//            System.out.println("Content: " + it.getContent() + " Correct Class: " + it.getClassification() + " string class: " + classification);
//            sb.append(it.getContent() + ", " + it.getClassification() + "," + classification + "\n");
//            total++;
//            if (it.getClassification().equals(classification)) {
//                correct++;
//            }
//        }
//        System.out.println("Total: " + total + "\nCorrect: " + correct + "\npct: " + (correct / total) + "\n");
//        Utils.writeToFile(sb.toString(), "StringMatchTest.csv");
//    }
//
//    /**
//     * Gets the input for a set of training files in the document name supplied.
//     * @param testingFiles
//     * @return array of inputs
//     */
//    public Object[] getInputs(String testingFiles) {
//
//        LinkedList<InputTriple> l = new LinkedList<InputTriple>();
//
//        try {
//            l = getFiles(new File(testingFiles));
//        }
//        catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return l.toArray();
//    }
//
//    /**
//     * Gets a list of labels that the inputs can be classified as.
//     * @param folder containing the classified files.
//     * @return list of possible outputs.
//     */
//    public List<String> getEditDistanceLables(final String folder) {
//
//        File files = new File(folder);
//        File[] filesArr = files.listFiles();
//        List<String> folders = new LinkedList<String>();
//        for (int i = 0; i < filesArr.length; i++) {
//            if (filesArr[i].isDirectory()) {
//                folders.add(filesArr[i].getName());
//            }
//        }
//        return folders;
//    }
//
//    private static LinkedList<InputTriple> getFiles(final File folder) throws FileNotFoundException {
//
//        // folder.setReadOnly();
//        folder.setReadable(true);
//        File[] files = folder.listFiles();
//
//        if (files == null) {
//            System.err.println("List of files from " + folder + " is null \n");
//        }
//        else {
//
//            for (int j = 0; j < files.length; j++) {
//                if (files[j].isFile() && !files[j].getName().startsWith(".DS_Store")) {
//                    list.add(getTriple(files[j]));
//                }
//                if (files[j].isDirectory()) {
//                    getFiles(files[j]);
//                }
//            }
//        }
//        return list;
//    }
//
//    private static InputTriple getTriple(final File file) throws FileNotFoundException {
//
//        Scanner s = new Scanner(file);
//        StringBuilder sb = new StringBuilder();
//        while (s.hasNextLine()) {
//            String line = s.nextLine();
//            sb.append(line);
//        }
//
//        InputTriple i = new InputTriple(file.getParentFile().getName(), file.getName(), sb.toString());
//
//        return i;
//    }
//
//    public static void main(String[] args) {
//
//        EditDistanceClassifier e = new EditDistanceClassifier();
//        LinkedList<String> l = new LinkedList<String>();
//        String[] classes = {"Accountants", "Maids and Related Housekeeping", "Carpenters, Joiners and Parquetry Workers", "Workers", "Ship's Master (Sea)", "Able Seaman", "Working Proprietors (Wholesale and Retail Trade) [food, non-food, dealers]", " Military", "Able Seaman",
//                        "Other Service Workers"};
//
//        for (int i = 0; i < classes.length; i++) {
//            l.push(classes[i].trim().toLowerCase());
//        }
//
//        //        System.out.println(e.classify("Accountants and Maids", l));
//        //        System.out.println(e.classify("Maids and Related Housekeeping", l));
//        //        System.out.println(e.classify("joiner (carpenter)", l));
//        //        System.out.println(e.classify("master (merchant service)", l));
//        //        System.out.println(e.classify("postmistress", l));
//        //        System.out.println(e.classify("r.a.f. aircraftsman (bank clerk)", l));
//        //        System.out.println(e.classify("able seaman", l));
//        // System.out.println(e.classify("chartered accountant", l));
//        //  System.out.println(e.classify("accountants reciever of companies", l));
//
//        //DirectorySetup ds = new DirectorySetup();
//        // ds.process(new File("occuaptionTextClass.txt"));
//
//        e.testWordSimilarityClassifier("testStringSim");
//    }
//}
