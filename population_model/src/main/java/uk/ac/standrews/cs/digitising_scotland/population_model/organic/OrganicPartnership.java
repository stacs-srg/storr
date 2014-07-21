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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.DivorceAgeForFemaleDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.DivorceAgeForMaleDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.DivorceInstigatedByGenderDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;
import uk.ac.standrews.cs.digitising_scotland.population_model.util.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by victor on 08/07/14.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class OrganicPartnership implements IPartnership {

    private static Random random = RandomFactory.getRandom();
    private static DivorceInstigatedByGenderDistribution divorceInstigatedByGenderDistribution = new DivorceInstigatedByGenderDistribution(random);
    private static DivorceAgeForMaleDistribution divorceAgeForMaleDistribution = new DivorceAgeForMaleDistribution(random);
    private static DivorceAgeForFemaleDistribution divorceAgeForFemaleDistribution = new DivorceAgeForFemaleDistribution(random);

    private Integer id;
    private Integer husband;
    private Integer wife;
    private OrganicTimeline timeline;
    private int marriageDay;
    private List<Integer> childrenIds = null;

    public OrganicPartnership(final int id, final OrganicPerson husband, final OrganicPerson wife, int marriageDay) {

        this.id = id;
        this.husband = husband.getId();
        this.wife = wife.getId();
        this.marriageDay = marriageDay;
        timeline = createPartnershipTimeline(husband, wife);
    }

    public OrganicTimeline createPartnershipTimeline(OrganicPerson husband, OrganicPerson wife) {

        // TODO Correctly populate timeline
        OrganicTimeline timeline = new OrganicTimeline(marriageDay);

        // Decide if/when relationship terminates
        switch (divorceInstigatedByGenderDistribution.getDefinedSample()) {
            case MALE:
                // get male age at divorce
                int maleDivorceAgeInDays;
                do {
                    maleDivorceAgeInDays = divorceAgeForMaleDistribution.getSample();
                }
                while (!PopulationLogic.divorceNotBeforeMarriage(DateManipulation.differenceInDays(husband.getBirthDay(), marriageDay), maleDivorceAgeInDays));
                timeline.addEvent(maleDivorceAgeInDays, new OrganicEvent(EventType.DIVORCE));
                timeline.setEndDate(maleDivorceAgeInDays);
                break;
            case FEMALE:
                // get female age at divorce
                int femaleDivorceAgeInDays;
                do {
                    femaleDivorceAgeInDays = divorceAgeForFemaleDistribution.getSample();
                }
                while (!PopulationLogic.divorceNotBeforeMarriage(DateManipulation.differenceInDays(husband.getBirthDay(), marriageDay), femaleDivorceAgeInDays));
                timeline.addEvent(femaleDivorceAgeInDays, new OrganicEvent(EventType.DIVORCE));
                timeline.setEndDate(femaleDivorceAgeInDays);
                break;
            case NO_DIVORCE:
                // If not then added earliest death date
                int firstPartnersDeathDate = dateOfFirstPartnersDeath(husband.getDeathDay(), wife.getDeathDay());
                timeline.addEvent(firstPartnersDeathDate, new OrganicEvent(EventType.PARTNERSHIP_ENDED_BY_DEATH));
                timeline.setEndDate(firstPartnersDeathDate);
                break;
        }

        // Decide on a number of children for relationship


        // Generate birth dates
        // Check they are permissible - not after death, breakup, too close together

        // Add births to timeline


        timeline.addEvent(400, new OrganicEvent(EventType.BIRTH));

        return timeline;
    }

    private int dateOfFirstPartnersDeath(int husbandDeath, int wifeDeath) {
        if (husbandDeath < wifeDeath)
            return husbandDeath;
        else
            return wifeDeath;
    }

    public OrganicTimeline getTimeline() {
        return timeline;
    }

    @Override
    public int compareTo(final IPartnership arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getFemalePartnerId() {
        return wife;
    }

    @Override
    public int getMalePartnerId() {
        return husband;
    }

    @Override
    public int getPartnerOf(final int id) {

        if (id == husband)
            return wife;
        else if (id == wife)
            return husband;
        else
            return -1;
    }

    @Override
    public Date getMarriageDate() {
        return DateManipulation.daysToDate(marriageDay);
    }

    @Override
    public List<Integer> getChildIds() {
        return childrenIds;
    }

    @Override
    public List<Integer> getPartnerIds() {
        return Arrays.asList(husband, wife);
    }
}
