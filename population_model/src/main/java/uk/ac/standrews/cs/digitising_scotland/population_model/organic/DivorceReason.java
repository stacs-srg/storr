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
package uk.ac.standrews.cs.digitising_scotland.population_model.organic;

/**
 * Represents the five permissible reasons for divorce as laid out in UK law.
 * 
 * @author Tom Dalton
 */
public enum DivorceReason {
    /**
     * Divorce reason cited as adultery by the non instigating party.
     */
    ADULTERY,
    /**
     * Divorce reason cited as the behaviour of the non instigating party.
     */
    BEHAVIOUR,
    /**
     * Divorce reason cited as desertion by the non instigating party.
     */
    DESERTION,
    /**
     * Divorce reason cited as the separation (of at least two years) with consent of the non instigating party.
     */
    SEPARATION_WITH_CONSENT,
    /**
     * Divorce reason cited as the separation (of at least five years) with no consent of the non instigating party.
     */
    SEPARATION
}
