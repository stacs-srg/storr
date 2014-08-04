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

import java.util.Date;
import java.util.List;

/**
 * Abstract person implementation.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public abstract class AbstractPerson implements IPerson {

    protected int id;
    protected String first_name;
    protected String surname;
    protected char sex;
    protected Date birth_date;
    protected String birth_place;
    protected Date death_date;
    protected String death_place;
    protected String death_cause;
    protected String occupation;
    protected String string_rep;
    protected List<Integer> partnerships;
    protected int parents_partnership_id;

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
        return (Date) birth_date.clone();
    }

    @Override
    public String getBirthPlace() {
        return birth_place;
    }

    @Override
    public Date getDeathDate() {
        return death_date == null ? null : (Date) death_date.clone();
    }

    @Override
    public String getDeathPlace() {
        return death_place;
    }

    @Override
    public String getDeathCause() {
        return death_cause;
    }

    @Override
    public String getOccupation() {
        return occupation;
    }

    @Override
    public List<Integer> getPartnerships() {
        return partnerships;
    }

    @Override
    public int getParentsPartnership() {
        return parents_partnership_id;
    }

    @SuppressWarnings("NonFinalFieldReferenceInEquals")
    @Override
    public boolean equals(final Object other) {
        return other instanceof IPerson && ((IPerson) other).getId() == id;
    }

    @SuppressWarnings("NonFinalFieldReferencedInHashCode")
    @Override
    public int hashCode() {
        return id;
    }

    public String toString() {
        return string_rep;
    }
}
