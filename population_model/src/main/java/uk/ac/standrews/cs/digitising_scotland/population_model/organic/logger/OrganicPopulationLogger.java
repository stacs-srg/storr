/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.population_model.organic.logger;

import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

/**
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OrganicPopulationLogger {

    private static int[] populationAtYearEnds;
    private static int startYear;

    public static void initPopulationAtYearEndsArray(final int startYear, final int endYear) {
        populationAtYearEnds = new int[endYear - startYear + 1];
        OrganicPopulationLogger.startYear = startYear;
    }

    public static void addPopulationForYear(int year, int population) {
        try {
            populationAtYearEnds[year - startYear] = population;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Array out of bounds - population end of year");
        }
    }

    public static void printPopulationGraph() {
        OrganicPopulation.writer.println("POPULATION GRAPH");
        printGraph(populationAtYearEnds, Integer.toString(startYear), Integer.toString(startYear + populationAtYearEnds.length), true, 30);
    }

    private static int population = 0;
    private static int marriages = 0;
    private static int births = 0;
    private static int divorces = 0;
    private static int remarriages = 0;
    private static final int MAX_AGE = 100;
    private static int[] maleAgeAtMarriage = new int[MAX_AGE];

    public static void printMaleAgeAtMarriageGraph() {
        OrganicPopulation.writer.println("MALE AGE AT MARRIAGE GRAPH");
        printGraph(maleAgeAtMarriage, "0", Integer.toString(MAX_AGE), false, 10);
    }

    private static int[] femaleAgeAtMarriage = new int[MAX_AGE];

    public static void printFemaleAgeAtMarriageGraph() {
        OrganicPopulation.writer.println("FEMALE AGE AT MARRIAGE GRAPH");
        printGraph(femaleAgeAtMarriage, "0", Integer.toString(MAX_AGE), false, 10);
    }

    private static final int AGE_DIFFERENCE_AT_MARRIAGE_DISPLAY_UPTO = 101;
    private static int[] ageDifferenceAtMarriage = new int[AGE_DIFFERENCE_AT_MARRIAGE_DISPLAY_UPTO];

    public static void printAgeDifferenceAtMarriageGraph() {
        OrganicPopulation.writer.println("AGE DIFFERENCE AT MARRIAGE GRAPH");
        printGraph(ageDifferenceAtMarriage, "0", Integer.toString(AGE_DIFFERENCE_AT_MARRIAGE_DISPLAY_UPTO), false, 10);
    }

    private static final int NUMBER_OF_CHILDREN_DISPLAY_UPTO = 15;
    private static int[] numberOfChildrenPerPartnership = new int[NUMBER_OF_CHILDREN_DISPLAY_UPTO];

    public static void printNumberOfChildrenInMarriageGraph() {
        OrganicPopulation.writer.println("NUMBER OF CHILDREN IN MARRIAGE GRAPH");
        printGraph(numberOfChildrenPerPartnership, "0", Integer.toString(NUMBER_OF_CHILDREN_DISPLAY_UPTO), false, 10);
    }

    private static int neverMarried = 0;

    private static int leftOverChildren = 0;
    private static int stopedHavingEarlyDeaths = 0;

    /**
     * Increments the size of the population.
     */
    public static void incPopulation() {
        population++;
    }

    /**
     * Decrements the size if the poulation.
     */
    public static void decPopulation() {
        population--;
    }

    /**
     * Returns the size of the alive population.
     *
     * @return The number of people alive in the population.
     */
    public static int getPopulation() {
        return population;
    }

    /**
     * Increments the number of marriages.
     */
    public static void incMarriages() {
        marriages++;
    }

    /**
     * Increases the number of remarriages.
     */
    public static void incRemarriages() {
        remarriages++;
    }

    /**
     * Increments the number of divorces.
     */
    public static void incDivorces() {
        divorces++;
    }

    /**
     * Increments the number of births.
     */
    public static void incBirths() {
        births++;
    }

    /**
     * Increments the number of people who are never married.
     */
    public static void incNeverMarried() {
        neverMarried++;
    }

    /**
     * Returns the number of left over children.
     * 
     * @return The number of left over children.
     */
    public static int getLeftOverChildren() {
        return leftOverChildren;
    }

    /**
     * Increments the number of left over children.
     * 
     * @param number The number to increment by.
     */
    public static void incLeftOverChildren(int number) {
        leftOverChildren += number;
    }

    /**
     * Decrements the number of left over children.
     * 
     * @param number The number to decremented by.
     */
    public static void decLeftOverChildren(int number) {
        leftOverChildren -= number;
    }

    /**
     * Increments count for the specified number of children in a partnership.
     * 
     * @param number The number of children count to be incremented.
     */
    public static void addNumberOfChildren(final int number) {
        numberOfChildrenPerPartnership[number]++;
    }

    /**
     * Returns the number of children stoped early by death of parents.
     * @return The number of children stoped early by death of parents.
     */
    public static int getStopedHavingEarlyDeaths() {
        return stopedHavingEarlyDeaths;
    }

    /**
     * Increments the number of children stoped early by death of parents.
     * @param number The number to increment by.
     */
    public static void incStopedHavingEarlyDeaths(int number) {
        OrganicPopulationLogger.stopedHavingEarlyDeaths += stopedHavingEarlyDeaths;
    }

    private static void addMaleAgeAtMarriage(final int days) {
        int years = (int) ((float) days / OrganicPopulation.getDaysPerYear());
        maleAgeAtMarriage[years]++;
    }

    private static void addFemaleAgeAtMarriage(final int days) {
        int years = (int) ((float) days / OrganicPopulation.getDaysPerYear());
        femaleAgeAtMarriage[years]++;
    }

    private static void addAgeDifferenceAtMarriage(final int maleDays, final int femaleDays) {
        int difference = Math.abs(maleDays - femaleDays);
        int years = (int) ((float) difference / OrganicPopulation.getDaysPerYear());
        ageDifferenceAtMarriage[years]++;
    }

    /**
     * Logs all needed information pertaining to the marriage.
     *
     * @param maleDays   Males age in days at the point of marriage.
     * @param femaleDays Females age in days at the point of marriage.
     */
    public static void logMarriage(final int maleDays, final int femaleDays) {
        incMarriages();
        addMaleAgeAtMarriage(maleDays);
        addFemaleAgeAtMarriage(femaleDays);
        addAgeDifferenceAtMarriage(maleDays, femaleDays);
    }

    /**
     * Logs and increments divorce.
     */
    public static void logDivorce() {
        incDivorces();
    }

    public static void printGraph(int[] values, String xStartValue, String xEndValue, boolean line, int lineDepth) {
        int sum = 0;
        int max = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
            if (values[i] > max) {
                max = values[i];
            }
        }
        int oneMarkerValue = max / lineDepth;
        if (oneMarkerValue == 0) {
            oneMarkerValue = 1;
        }
        boolean[][] graph = new boolean[values.length][lineDepth];
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i] / oneMarkerValue; j++) {
                if (j >= lineDepth) {
                    break;
                }
                graph[i][j] = true;
                if (line && j != 0) {
                    graph[i][j - 1] = false;
                }
            }
        }
        OrganicPopulation.writer.print(max + "|");
        for (int j = lineDepth - 1; j >= 0; j--) {
            if (j != lineDepth - 1) {
                int n = 0;
                if (j == 0) {
                    n++;
                }
                for (int i = n; i < Integer.toString(max).length(); i++) {
                    OrganicPopulation.writer.print(" ");
                }
                if (j == 0) {
                    OrganicPopulation.writer.print("0");
                }
                OrganicPopulation.writer.print("|");
            }
            for (int i = 0; i < values.length; i++) {
                if (!graph[i][j]) {
                    OrganicPopulation.writer.print(" ");
                } else if (graph[i][j]) {
                    OrganicPopulation.writer.print("@");
                }
            }
            OrganicPopulation.writer.println();
        }
        for (int i = 0; i < Integer.toString(max).length(); i++) {
            OrganicPopulation.writer.print(" ");
        }
        OrganicPopulation.writer.print(xStartValue);
        int i = xStartValue.length();
        for (i = 0; i < values.length - xEndValue.length(); i++) {
            OrganicPopulation.writer.print("‾");
        }
        OrganicPopulation.writer.println(xEndValue);
        OrganicPopulation.writer.println();
    }

    /**
     * Prints out the data that has been logged from the simulation.
     */
    public static void printLogData() {
        OrganicPopulation.writer.println();
        OrganicPopulation.writer.println("-------Population Logger-------");
        OrganicPopulation.writer.println();

        printMaleAgeAtMarriageGraph();
        printFemaleAgeAtMarriageGraph();
        printAgeDifferenceAtMarriageGraph();
        printNumberOfChildrenInMarriageGraph();
        printPopulationGraph();
//        printAdjustedNumberOfChildrenEndSizesGraph();
        float childrenPerFamily = (float) (births - OrganicPopulation.getDefaultSeedSize()) / (float) marriages;
        OrganicPopulation.writer.println();
        OrganicPopulation.writer.println("Population: " + population);
        OrganicPopulation.writer.println();
        OrganicPopulation.writer.println("Marriages: " + marriages);
        OrganicPopulation.writer.println("Divorces: " + divorces);
        OrganicPopulation.writer.println("Remarriages: " + remarriages);
        OrganicPopulation.writer.print("Never Married: " + neverMarried + " - ");
        OrganicPopulation.writer.format("%.2f", ((float) neverMarried / (float) births) * 100);
        OrganicPopulation.writer.println("%");
        OrganicPopulation.writer.println();
        OrganicPopulation.writer.println("Births: " + births);
        OrganicPopulation.writer.print("Children Per Family: ");
        OrganicPopulation.writer.format("%.2f", childrenPerFamily);
        OrganicPopulation.writer.println();
        OrganicPopulation.writer.println();
//        OrganicPopulation.writer.println("Left over children: " + getNumberOfLeftOverChildren());
        OrganicPopulation.writer.println("Kids killed by early stop: " + getStopedHavingEarlyDeaths());

    }

//    private static void printAdjustedNumberOfChildrenEndSizesGraph() {
//        OrganicPopulation.writer.println("ADJUSTED NUMBER OF CHILDREN END FAMILY SIZES GRAPH");
//        int[] temp = new int[OrganicPartnership.getAdjustedNumberOfChildren().length];
//        for (int i = 0; i < temp.length; i++) {
//            temp[i] = OrganicPartnership.getAdjustedNumberOfChildren()[i].size();
//        }
//        printGraph(temp, "0", Integer.toString(OrganicPopulation.getMaximumNumberOfChildrenInFamily() + 1), false, 10);
//    }

//    private static int getNumberOfLeftOverChildren() {
//        int count = 0;
//        for (int i = 0; i < OrganicPartnership.getAdjustedNumberOfChildren().length; i++) {
//            for (int j = 0; j < OrganicPartnership.getAdjustedNumberOfChildren()[i].size(); j++) {
//                count += OrganicPartnership.getAdjustedNumberOfChildren()[i].get(j) - i;
//            }
//        }
//        return count;
//    }
}
