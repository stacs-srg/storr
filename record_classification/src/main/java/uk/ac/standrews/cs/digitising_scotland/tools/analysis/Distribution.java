package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import uk.ac.standrews.cs.digitising_scotland.tools.fileutils.DSStoreRemover;

/**
 * Holds info about a datasets file distribution.
 * 
 * @author jkc25
 */
public class Distribution {

    private int noOfClasses;
    private int noOfTotalValues;
    private String[] classNames;
    private int[] noOfValues;
    private ClassificationDistribution[] orderedClass;

    /**
     * Returns the distribution list ordered by % of the total.
     * 
     * @return distribution list ordered by % of the total
     */
    public ClassificationDistribution[] getOrderedClass() {

        ClassificationDistribution[] newOrderedClass = orderedClass;
        return newOrderedClass;
    }

    /**
     * Creates a new distribution from the selected base folder.
     * 
     * @param base
     *            Location of files.
     */
    public Distribution(final File base) {

        DSStoreRemover dsr = new DSStoreRemover();
        dsr.remove(base);
        System.out.println(base.isDirectory());
        classNames = base.list();
        orderedClass = new ClassificationDistribution[classNames.length];

        Arrays.sort(classNames, new Comparator<String>() {

            public int compare(final String f1, final String f2) {

                return Integer.valueOf(f1.substring(0, 1).compareTo(f2.substring(0, 1)));
            }
        });

        this.noOfClasses = classNames.length;
        noOfValues = new int[classNames.length];
        noOfTotalValues = 0;

        for (int i = 0; i < classNames.length; i++) {
            noOfValues[i] = new File(base + "/" + classNames[i]).list().length;
            noOfTotalValues += noOfValues[i];
        }

        for (int i = 0; i < classNames.length; i++) {
            orderedClass[i] = new ClassificationDistribution(classNames[i], noOfValues[i], Math.rint(((double) noOfValues[i] / (double) noOfTotalValues) * 10000) / 100);
        }

        Arrays.sort(orderedClass);

    }

    /**
     * Returns total number of values.
     * 
     * @return total number of files.
     */
    public int getNoOfTotalValues() {

        return noOfTotalValues;
    }

    /**
     * Returns the number of classes.
     * 
     * @return number of classes.
     */
    public int getNoOfClasses() {

        return noOfClasses;
    }

    /**
     * Returns an array of class names.
     * 
     * @return Returns an array of class names.
     */
    public String[] getClassNames() {

        String[] newClassNames = classNames;
        return newClassNames;
    }

    /**
     * Returns the total number of values.
     * 
     * @return Returns the total number of values.
     */
    public int[] getNoOfValues() {

        int[] newNoOfValues = noOfValues;
        return newNoOfValues;
    }

    /**
     * Returns the number of files that make up a specific class.
     * 
     * @param classification
     *            Name of the class we want the number of files for.
     * @return Number of files in this class or -1 if class is not present.
     */
    public int getValueForName(final String classification) {

        for (int i = 0; i < classNames.length; i++) {
            if (classNames[i].equalsIgnoreCase(classification)) { return noOfValues[i]; }
        }

        return -1;
    }

    /**
     * Prints the Distribution details to the console.
     */
    public void printDistribution() {

        System.out.println(this.toString());
    }

    /**
     * Overrides toString.
     * 
     * @return String interpretation of the class
     */
    public String toString() {

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < classNames.length; i++) {
            buf.append(classNames[i] + " \t" + noOfValues[i] + " \t" + Math.rint(((double) noOfValues[i] / (double) noOfTotalValues) * 10000) / 100 + "%" + "\n");
        }

        return buf.toString();
    }

}
