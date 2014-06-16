/*
 * 
Understanding Scotland's People (USP) project.

The aim of the project is to produce a linked pedigree for all publicly available Scottish birth/death/marriage records from 1855 to the present day.
Digitisation of the records is being carried out by the ESRC-funded Digitising Scotland project, run by University of St Andrews and National Records of Scotland.
The project is led by Chris Dibben at the Longitudinal Studies Centre at St Andrews. The other project members are Lee Williamson (also at the Longitudinal Studies Centre
Graham Kirby, Alan Dearle and Jamie Carson at the School of Computer Science at St Andrews; and Eilidh Garret and Alice Reid at the Department of Geography at Cambridge.
 */

package uk.ac.standrews.cs.usp.tools.analysis;

import java.util.HashMap;

/**
 * The Class Term.

 * @author jkc25
 */
public class Term {

    private String content;
    private HashMap<String, Double> perClassMI;
    private HashMap<String, Double> perClassCHI;
    private double averageMI;
    private double averageCHI;
    private double maxMI;
    private double maxCHI;

    /**
     * Contains metrics about how useful a tarms isn for every class that it is
     * found in along with average and max data.
     * 
     * @param content
     *            the string content of the term.
     */
    public Term(final String content) {

        this.content = content;
    }

    /**
     * Returns the content of the term.
     * 
     * @return String, content
     */
    public String getContent() {

        return content;
    }

    /**
     * Sets the content of a term.
     * 
     * @param content The terms content.
     */
    public void setContent(final String content) {

        this.content = content;
    }

    /**
     * Contains a map that holds the mutual information (MI) between the term
     * and every class that it is on.
     * 
     * @return HashMap<String, Double> per class MI.
     */
    public HashMap<String, Double> getPerClassMI() {

        return perClassMI;
    }

    /**
     * Sets the per class mi.
     *
     * @param perClassMI the per class mi
     */
    public void setPerClassMI(final HashMap<String, Double> perClassMI) {

        this.perClassMI = perClassMI;
    }

    /**
     * Gets the per class chi.
     *
     * @return the per class chi
     */
    public HashMap<String, Double> getPerClassCHI() {

        return perClassCHI;
    }

    /**
     * Sets the per class chi.
     *
     * @param perClassCHI the per class chi
     */
    public void setPerClassCHI(final HashMap<String, Double> perClassCHI) {

        this.perClassCHI = perClassCHI;
    }

    /**
     * Gets the average mi.
     *
     * @return the average mi
     */
    public double getAverageMI() {

        return averageMI;
    }

    /**
     * Sets the average mi.
     *
     * @param averageMI the new average mi
     */
    public void setAverageMI(final double averageMI) {

        this.averageMI = averageMI;
    }

    /**
     * Gets the average chi.
     *
     * @return the average chi
     */
    public double getAverageCHI() {

        return averageCHI;
    }

    /**
     * Sets the average chi.
     *
     * @param averageCHI the new average chi
     */
    public void setAverageCHI(final double averageCHI) {

        this.averageCHI = averageCHI;
    }

    /**
     * Gets the max mi.
     *
     * @return the max mi
     */
    public double getMaxMI() {

        return maxMI;
    }

    /**
     * Sets the max mi.
     *
     * @param maxMI the new max mi
     */
    public void setMaxMI(final double maxMI) {

        this.maxMI = maxMI;
    }

    /**
     * Gets the max chi.
     *
     * @return the max chi
     */
    public double getMaxCHI() {

        return maxCHI;
    }

    /**
     * Sets the max chi.
     *
     * @param maxCHI the new max chi
     */
    public void setMaxCHI(final double maxCHI) {

        this.maxCHI = maxCHI;
    }

}
