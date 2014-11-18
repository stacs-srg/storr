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
/*
 * | ______________________________________________________________________________________________ | Understanding
 * Scotland's People (USP) project. | | The aim of the project is to produce a linked pedigree for all publicly | |
 * available Scottish birth/death/marriage records from 1855 to the present day. | | | | Digitization of the records is
 * being carried out by the ESRC-funded Digitising | | Scotland project, run by University of St Andrews and National
 * Records of Scotland. | | | | The project is led by Chris Dibben at the Longitudinal Studies Centre at St Andrews. | |
 * The other project members are Lee Williamson (also at the Longitudinal Studies Centre) | | Graham Kirby, Alan Dearle
 * and Jamie Carson at the School of Computer Science at St Andrews; | | and Eilidh Garret and Alice Reid at the
 * Department of Geography at Cambridge. | | | |
 * ______________________________________________________________________________________________
 */
package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * Reports details of each run, including, parameters, dataset info and results.
 * 
 * @author jkc25
 */
public final class Reporter {

    /** The data set. */
    private String dataSet = "";

    /** The number of classes. */
    private double numberOfClasses;

    /** The number of training files. */
    private double numberOfTrainingFiles;

    /** The number of features. */
    private String numberOfFeatures = "";

    /** The best accuracy string. */
    private String bestAccuracyString = "";

    /** The total nb correct. */
    private double totalNBCorrect = 0;

    /** The total sgd correct. */
    private double totalSGDCorrect = 0;

    /** The total ppsgd correct. */
    private double totalPPSGDCorrect = 0;

    /** The total best con correct. */
    private double totalBestConCorrect = 0;

    /** The total majority vote. */
    private double totalMajorityVote = 0;

    /** The total threshold vote. */
    private double totalThresholdVote = 0;

    /** The proxy confidence. */
    private double proxyConfidence = 0;

    /** The string sim correct. */
    private double stringSimCorrect = 0;

    // Singleton, so no public access.
    /**
     * Instantiates a new reporter.
     */
    private Reporter() {

    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance() or the first access to
     * SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {

        /** The Constant INSTANCE. */
        public static final Reporter INSTANCE = new Reporter();
    }

    /**
     * Writes all available metrics to a file.
     * 
     * @return True if file writing is sucessful, false otherwise.
     */
    public boolean report() {

        double bestAccuracy = -10;
        String[] stringResults;

        String output = dataSet + "," + numberOfClasses + "," + numberOfTrainingFiles + "," + numberOfFeatures + "," + bestAccuracy + "," + totalNBCorrect + "," + totalSGDCorrect + "," + totalPPSGDCorrect + "," + totalBestConCorrect + "," + proxyConfidence + "," + totalThresholdVote + ","
                        + totalMajorityVote + "\n";
        stringResults = output.split(",");

        for (int i = 5; i < stringResults.length; i++) {
            if (Double.parseDouble(stringResults[i]) > bestAccuracy) {
                bestAccuracy = Double.parseDouble(stringResults[i]);
            }
        }
        output = "dataSet: " + dataSet + "\n" + "numberOfClasses: " + numberOfClasses + "\n" + "numberOfTrainingFiles: " + numberOfTrainingFiles + "\n" + "numberOfFeatures: " + numberOfFeatures + "\n" + "best overall accuracy: " + bestAccuracy + "\n" + "totalNBCorrect: " + totalNBCorrect + "\n"
                        + "totalSGDCorrect: " + totalSGDCorrect + "\n" + "totalStringSimCorrect: " + stringSimCorrect + "\n" + "totalPPSGDCorrect: " + totalPPSGDCorrect + "\n" + "totalBestConCorrect: " + totalBestConCorrect + "\n" + "proxyConfidence: " + proxyConfidence + "\n"
                        + "totalThresholdVote: " + totalThresholdVote + "\n" + "totalMajorityVote: " + totalMajorityVote + "\n";
        Utils.writeToFile(output, "log.txt");
        return true;
    }

    /**
     * Returns the number of correct classifications by the SGD Classifier.
     * 
     * @return double, correct number of classifications.
     */
    public double getTotalSGDCorrect() {

        return totalSGDCorrect;
    }

    /**
     * Sets the number of correct classifications by the SGD Classifier.
     * 
     * @param totalSGDCorrect
     *            number of correct classifications by the SGD Classifier.
     */
    public void setTotalSGDCorrect(final double totalSGDCorrect) {

        this.totalSGDCorrect = totalSGDCorrect;
    }

    /**
     * Returns the number of correct classifications with the preparsed data with the SGD Classifier.
     * 
     * @return double, correct number of classifications.
     */
    public double getTotalPPSGDCorrect() {

        return totalPPSGDCorrect;
    }

    /**
     * Sets the number of correct classifications by the SGD Classifier with the pre-parsed data.
     * 
     * @param totalPPSGDCorrect
     *            number of correct classifications by the SGD Classifier.
     */
    public void setTotalPPSGDCorrect(final double totalPPSGDCorrect) {

        this.totalPPSGDCorrect = totalPPSGDCorrect;
    }

    /**
     * Returns the number of correct classifications using the best confidence method.
     * 
     * @return double, correct number of classifications using best confidence technique.
     */
    public double getTotalBestConCorrect() {

        return totalBestConCorrect;
    }

    /**
     * Sets the total best con correct.
     *
     * @param totalBestConCorrect the new total best con correct
     */
    public void setTotalBestConCorrect(final double totalBestConCorrect) {

        this.totalBestConCorrect = totalBestConCorrect;
    }

    /**
     * Gets the data set.
     *
     * @return the data set
     */
    public String getDataSet() {

        return dataSet;
    }

    /**
     * Sets the data set.
     *
     * @param dataSet the new data set
     */
    public void setDataSet(final String dataSet) {

        this.dataSet = dataSet;
    }

    /**
     * Gets the number of classes.
     *
     * @return the number of classes
     */
    public double getNumberOfClasses() {

        return numberOfClasses;
    }

    /**
     * Sets the number of classes.
     *
     * @param numberOfClasses the new number of classes
     */
    public void setNumberOfClasses(final double numberOfClasses) {

        this.numberOfClasses = numberOfClasses;
    }

    /**
     * Gets the number of features.
     *
     * @return the number of features
     */
    public String getNumberOfFeatures() {

        return numberOfFeatures;
    }

    /**
     * Sets the number of features.
     *
     * @param numberOfFeatures the new number of features
     */
    public void setNumberOfFeatures(final String numberOfFeatures) {

        this.numberOfFeatures = numberOfFeatures;
    }

    /**
     * Gets the single instance of Reporter.
     *
     * @return single instance of Reporter
     */
    public static Reporter getInstance() {

        return SingletonHolder.INSTANCE;
    }

    /**
     * Gets the best accuracy.
     *
     * @return the best accuracy
     */
    public String getBestAccuracy() {

        return bestAccuracyString;
    }

    /**
     * Sets the best accuracy.
     *
     * @param bestAccuracy the new best accuracy
     */
    public void setBestAccuracy(final String bestAccuracy) {

        this.bestAccuracyString = bestAccuracy;
    }

    /**
     * Gets the number of training files.
     *
     * @return the number of training files
     */
    public double getNumberOfTrainingFiles() {

        return numberOfTrainingFiles;
    }

    /**
     * Sets the number of training files.
     *
     * @param numberOfTrainingFiles the new number of training files
     */
    public void setNumberOfTrainingFiles(final double numberOfTrainingFiles) {

        this.numberOfTrainingFiles = numberOfTrainingFiles;
    }

    /**
     * Gets the total nb correct.
     *
     * @return the total nb correct
     */
    public double getTotalNBCorrect() {

        return totalNBCorrect;
    }

    /**
     * Sets the total nb correct.
     *
     * @param totalNBCorrect the new total nb correct
     */
    public void setTotalNBCorrect(final double totalNBCorrect) {

        this.totalNBCorrect = totalNBCorrect;
    }

    /**
     * Sets the threshold vote.
     *
     * @param totalCorrect the new threshold vote
     */
    public void setThresholdVote(final double totalCorrect) {

        this.totalThresholdVote = totalCorrect;
    }

    /**
     * Sets the proxy confidence.
     *
     * @param totalCorrect the new proxy confidence
     */
    public void setProxyConfidence(final double totalCorrect) {

        this.proxyConfidence = totalCorrect;
    }

    /**
     * Sets the majority vote.
     *
     * @param totalCorrect the new majority vote
     */
    public void setMajorityVote(final double totalCorrect) {

        this.totalMajorityVote = totalCorrect;
    }

    /**
     * Sets the string sim correct.
     *
     * @param d the new string sim correct
     */
    public void setStringSimCorrect(final double d) {

        this.stringSimCorrect = d;
    }

    /**
     * Gets the string sim correct.
     *
     * @return the string sim correct
     */
    public double getStringSimCorrect() {

        return stringSimCorrect;
    }

}
