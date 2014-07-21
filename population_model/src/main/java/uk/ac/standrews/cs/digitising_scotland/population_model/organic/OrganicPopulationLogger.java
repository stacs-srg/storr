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
    private static int[] maleAgeAtMarriage = new int[100];
    private static int[] femaleAgeAtMarriage = new int[100];
    private static int[] ageDifferenceAtMarriage = new int[25];

    public static void incPopulation() {
        population++;
    }

    public static void decPopulation() {
        population--;
    }
    
    public static int getPopulation() {
    	return population;
    }

    public static void incMarriages() {
        marriages++;
    }

    public static void decMarriages() {
        marriages--;
    }
    
    public static void incBirths() {
        births++;
    }

    public static void addMaleAgeAtMarriage(int days) {
        int years = (int) ((float) days / OrganicPopulation.DAYS_PER_YEAR);
        maleAgeAtMarriage[years]++;
    }

    public static void addFemaleAgeAtMarriage(int days) {
        int years = (int) ((float) days / OrganicPopulation.DAYS_PER_YEAR);
        femaleAgeAtMarriage[years]++;
    }

    public static void addAgeDifferenceAtMarriage(int maleDays, int femaleDays) {
        int difference = Math.abs(maleDays - femaleDays);
        int years = (int) ((float) difference / OrganicPopulation.DAYS_PER_YEAR);
        ageDifferenceAtMarriage[years]++;
    }

    public static void logMarriage(int maleDays, int femaleDays) {
        incMarriages();
        addMaleAgeAtMarriage(maleDays);
        addFemaleAgeAtMarriage(femaleDays);
        addAgeDifferenceAtMarriage(maleDays, femaleDays);
    }

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
    }

}
