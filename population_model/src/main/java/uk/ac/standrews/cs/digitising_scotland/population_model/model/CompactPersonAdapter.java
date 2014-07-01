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
package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.Date;

/**
 * Created by graham on 01/07/2014.
 */
public class CompactPersonAdapter {

    public static IPerson convertToFullPerson(CompactPerson person) {

        return person != null ? new FullPerson(person) : null;
    }

    private static class FullPerson implements IPerson {

        private int id;
        private String first_name;
        private String surname;
        private String maiden_name;
        private char sex;
        private Date date_of_birth;
        private Date date_of_death;
        private String occupation;
        private String cause_of_death;
        private String address;
        private String string_rep;

        public FullPerson(CompactPerson person) {

            id = person.getId();
            sex = person.getSex();

            date_of_birth = DateManipulation.daysToDate(person.getDateOfBirth());
            date_of_death = DateManipulation.daysToDate(person.getDateOfDeath());

            string_rep = person.toString();
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String getFirstName() {
            return first_name;
        }

        @Override
        public String getSurname() {
            return surname;
        }

        @Override
        public char getSex() {
            return sex;
        }

        @Override
        public Date getBirthDate() {
            return date_of_birth;
        }

        @Override
        public Date getDeathDate() {
            return date_of_death;
        }

        @Override
        public String getOccupation() {
            return occupation;
        }

        @Override
        public String getCauseOfDeath() {
            return cause_of_death;
        }

        @Override
        public String getAddress() {
            return address;
        }

        @Override
        public String getMaidenName() {
            return maiden_name;
        }

        @Override
        public boolean equals(final Object other) {
            return other instanceof IPerson && ((IPerson)other).getId() == id;
        }

        @Override
        public int hashCode() {
            return id;
        }

        public String toString() { return string_rep; }
    }
}
