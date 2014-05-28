package uk.ac.standrews.cs.digitising_scotland.linkage.event_records;

/**
 * Created by graham on 13/05/2014.
 */
public abstract class IndividualRecord extends Record {

    protected String surname;
    protected String surname_changed;
    protected String forename;
    protected String forename_changed;

    protected String sex;

    protected String mothers_forename;
    protected String mothers_surname;
    protected String mothers_maiden_surname;
    protected String mothers_maiden_surname_changed;

    protected String fathers_forename;
    protected String fathers_surname;
    protected String fathers_occupation;

    public String getSurname() {
        return surname;
    }

    public void setSurname(final String surname) {
        this.surname = surname;
    }

    public String getSurnameChanged() {
        return surname_changed;
    }

    public void setSurnameChanged(final String surname_changed) {
        this.surname_changed = surname_changed;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(final String forename) {
        this.forename = forename;
    }

    public String getForenameChanged() {
        return forename_changed;
    }

    public void setForenameChanged(final String forename_changed) {
        this.forename_changed = forename_changed;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(final String sex) {
        this.sex = sex;
    }

    public String getMothersForename() {
        return mothers_forename;
    }

    public void setMothersForename(final String mothers_forename) {
        this.mothers_forename = mothers_forename;
    }

    public String getMothersSurname() {
        return mothers_surname;
    }

    public void setMothersSurname(final String mothers_surname) {
        this.mothers_surname = mothers_surname;
    }

    public String getMothersMaidenSurname() {
        return mothers_maiden_surname;
    }

    public void setMothersMaidenSurname(final String mothers_maiden_surname) {
        this.mothers_maiden_surname = mothers_maiden_surname;
    }

    public String getMothersMaidenSurnameChanged() {
        return mothers_maiden_surname_changed;
    }

    public void setMothersMaidenSurnameChanged(final String mothers_maiden_surname_changed) {
        this.mothers_maiden_surname_changed = mothers_maiden_surname_changed;
    }

    public String getFathersForename() {
        return fathers_forename;
    }

    public void setFathersForename(final String fathers_forename) {
        this.fathers_forename = fathers_forename;
    }

    public String getFathersSurname() {
        return fathers_surname;
    }

    public void setFathersSurname(final String fathers_surname) {
        this.fathers_surname = fathers_surname;
    }

    public String getFathersOccupation() {
        return fathers_occupation;
    }

    public void setFathersOccupation(final String fathers_occupation) {
        this.fathers_occupation = fathers_occupation;
    }
}
