/**
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
package uk.ac.standrews.cs.digitising_scotland.population_model.organic;

public class OrganicPopulationLogger {

    private static int population = 0;
    private static int marriages = 0;
    private static int births = 0;
    private static int divorces = 0;
    private static int remarriages = 0;
    private static final int MAX_AGE = 100;
    private static int[] maleAgeAtMarriage = new int[MAX_AGE];
    private static int[] femaleAgeAtMarriage = new int[MAX_AGE];
    private static final int AGE_DIFFERENCE_AT_MARRIAGE_DISPLAY_UPTO = 21;
    private static int[] ageDifferenceAtMarriage = new int[AGE_DIFFERENCE_AT_MARRIAGE_DISPLAY_UPTO];
    private static final int NUMBER_OF_CHILDREN_DISPLAY_UPTO = 15;
    private static int[] numberOfChildrenPerPartnership = new int[NUMBER_OF_CHILDREN_DISPLAY_UPTO];
    private static int neverMarried = 0;
    
	public static int leftOverChildren = 0;
	public static int stopedHavingEarlyDeaths = 0;

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
     * Increases the number of remarriages
     */
    public static void incRemarriages() {
        remarriages ++;
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

    public static void addNumberOfChildren(final int number) {
        numberOfChildrenPerPartnership[number]++;
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

    public static void logDivorce() {
        incDivorces();
    }

    /**
     * Prints out the data that has been logged from the simulation.
     */
    public static void printLogData() {
        System.out.println();
        System.out.println("-------Population Logger-------");
        System.out.println();
        System.out.println("Population: " + population);
        System.out.println("Marriages: " + marriages);
        System.out.println("Births: " + births);
        System.out.println();
        System.out.println("Male Age At Marriage Distrobution");
        System.out.println();
        System.out.println("| Age | Number |");
        for (int i = 0; i < maleAgeAtMarriage.length; i++) {
            System.out.print("| ");
            System.out.format("%3d", i);
            System.out.print(" | ");
            System.out.format("%6d", maleAgeAtMarriage[i]);
            System.out.println(" |");
        }
        System.out.println();

        System.out.println();
        System.out.println("Female Age At Marriage Distrobution");
        System.out.println();
        System.out.println("| Age | Number |");
        for (int i = 0; i < maleAgeAtMarriage.length; i++) {
            System.out.print("| ");
            System.out.format("%3d", i);
            System.out.print(" | ");
            System.out.format("%6d", femaleAgeAtMarriage[i]);
            System.out.println(" |");
        }
        System.out.println();

        System.out.println();
        System.out.println("Age Difference At Marriage Distrobution");
        System.out.println();
        System.out.println("| Diff | Number |");
        for (int i = 0; i < ageDifferenceAtMarriage.length; i++) {
            System.out.print("|  ");
            System.out.format("%2d", i);
            System.out.print("  | ");
            System.out.format("%6d", ageDifferenceAtMarriage[i]);
            System.out.println(" |");
        }
        System.out.println();
        System.out.println();
        System.out.println("Number Of Children In Partnership Distrobution");
        System.out.println();
        System.out.println("| Kids | Number |");
        for (int i = 0; i < numberOfChildrenPerPartnership.length; i++) {
            System.out.print("|  ");
            System.out.format("%2d", i);
            System.out.print("  | ");
            System.out.format("%6d", numberOfChildrenPerPartnership[i]);
            System.out.println(" |");
        }
        float childrenPerFamily = (float) (births - OrganicPopulation.getDefaultSeedSize()) / (float) marriages;
        System.out.println();
        System.out.println("Children Per Family: ");
        System.out.format("%.2f", childrenPerFamily);
        System.out.println();

        System.out.println("Divorces: " + divorces);
        System.out.println("Remarriages: " + remarriages);

        System.out.print("Never Married: " + neverMarried + " - ");
        System.out.format("%.2f", ((float) neverMarried / (float) births) * 100);
        System.out.println("%");
    }

}
