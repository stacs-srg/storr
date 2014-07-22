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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions;

import uk.ac.standrews.cs.digitising_scotland.population_model.util.RandomFactory;

import java.util.Random;

/**
 * Governs the chances of remarriage after divorce.
 *
 * Created by victor on 22/07/2014.
 */
public class RemarriageDistribution implements Distribution<Boolean>{


    /**
     * Based on previous martial status in all marriages occurring in England and Wales (1981 - 2011)
     *
     * http://www.ons.gov.uk/ons/taxonomy/index.html?nscl=Marriages%2C+Cohabitations%2C+Civil+Partnerships+and+Divorces#tab-data-tables
     *
     * According to this data, for the past 30 years, both genders exhibit similar chances of getting remarried after a divorce. (~21%)
     */

    private final int PROBABILITY_OF_REMARRIAGE = 21;

    Random rand = RandomFactory.getRandom();
    private UniformDistribution dist = new UniformDistribution(1,100,rand);

    @Override
    public Boolean getSample() {
        int cent = dist.getSample();
        return (cent <= PROBABILITY_OF_REMARRIAGE);
    }
}
