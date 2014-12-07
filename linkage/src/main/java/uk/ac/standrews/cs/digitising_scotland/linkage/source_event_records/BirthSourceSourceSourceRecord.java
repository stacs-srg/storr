package uk.ac.standrews.cs.digitising_scotland.linkage.source_event_records;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.Date;

/**
 * A representation of a Birth Record in the form used by the Digitising Scotland Project.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 *         <p/>
 *         Fields are as follows:
 *         <p/>
 *         Ref Field
 *         1. Unique'Record'Identifier'
 *         2. Surname
 *         3. Forename
 *         4. Sex
 *         5. Year of Registration
 *         6. Registration District Number
 *         7. Registration District Suffix
 *         8. Entry
 *         9. Birth Year
 *         10. Mother’s Maiden Surname
 *         11. Changed Surname
 *         12. Changed Forename
 *         13. Birth Day
 *         14. Birth Month
 *         15. Birth Address
 *         16. Father’s Forename
 *         17. Father’s Surname ('0' if same as Surname)
 *         18. Father’s Occupation
 *         19. Mother’s Forename
 *         20. Mother’s Surname ('0' if same as Surname)
 *         21. Changed Mothers Maiden Surname
 *         22. Parents Day of Marriage
 *         23. Parents Month of Marriage
 *         24. Parents Year of Marriage
 *         25. Parents Place of Marriage
 *         26. Illegitimate indicator ('Y' or empty)
 *         27. Informant ('M', 'F' or empty)
 *         28. Informant did not Sign ('X' or empty)
 *         29. Corrected Entry ('1', '2', '3' or empty)
 *         30. Adoption ('A' or empty)
 *         31. Image Quality ('1', '2' or empty)
 *         <p/>
 *         <p/>
 *         Examples of birth records:
 *         <p/>
 *         1000001|HAY|HERCULES|M|1855|009|00|041||SKLATER|||21|7|SILWICK|WALTER|0|FISHERMAN|INGA|0|||1|1840|SELIVOE||F|||||
 *         1000002|JAMESON|JAMINA|F|1855|009|00|042|||||26|7|HOGANESS|ROBERT|0|FISH_CURER|ANN|0|SKLATER||11|1841|SELIVOE||F|||||
 *         1000003|IRVINE|CATHERINE|F|1855|009|00|043|||||20|7|TULKY|JOHN|0|FISHERMAN_&_CROFTER|MARGARET|0|JOHNSON||12|1841|SELIVOE| |F|||||
 *         1000004|HAWICK|CATHERINE|F|1855|009|00|044|||||25|7|AITH|SCOTT|0|SEAMAN|44|MARY|0|YELL||12|1841|SELIVOE||M|X||||
 *         1000005|GEORGESON|PETER|M|1855|009|00|045||ISBESTER|||17|5|SAND|GEORGE|0|SEAMAN|MARGARET|0|||11|1838|WATNESS||M|X||||
 */
public class BirthSourceSourceSourceRecord extends IndividualSourceSourceRecord {

    private DateRecord birth_date;
    private String birth_address;

    private DateRecord parents_marriage_date;
    private String parents_place_of_marriage;

    private String illegitimate_indicator;
    private String informant;
    private String informant_did_not_sign;
    private String adoption;

    public BirthSourceSourceSourceRecord(final IPerson person, IPopulation population) {

        birth_date = new DateRecord();
        parents_marriage_date = new DateRecord();

        // Attributes associated with individual
        setUid(String.valueOf(person.getId()));
        setSex(String.valueOf(person.getSex()));
        setForename(person.getFirstName());
        setSurname(person.getSurname());

        final Date birth_date = person.getBirthDate();

        long birth_day = DateManipulation.dateToDay(birth_date);
        long birth_month = DateManipulation.dateToMonth(birth_date);
        long birth_year = DateManipulation.dateToYear(birth_date);

        setBirthDay(String.valueOf(birth_day));
        setBirthMonth(String.valueOf(birth_month));
        setBirthYear(String.valueOf(birth_year));

        int parents_partnership_id = person.getParentsPartnership();
        if (parents_partnership_id != -1) {

            final IPartnership parents_partnership = population.findPartnership(parents_partnership_id);

            // Attributes associated with individual's parents' marriage.
            final Date marriage_date = parents_partnership.getMarriageDate();

            long marriage_day = DateManipulation.dateToDay(marriage_date);
            long marriage_month = DateManipulation.dateToMonth(marriage_date);
            long marriage_year = DateManipulation.dateToYear(marriage_date);

            setParentsMarriageDay(String.valueOf(marriage_day));
            setParentsMarriageMonth(String.valueOf(marriage_month));
            setParentsMarriageYear(String.valueOf(marriage_year));

            setParentAttributes(person, population, parents_partnership);
        }
    }

    public String getBirthDay() {
        return birth_date.getDay();
    }

    public void setBirthDay(final String birth_day) {
        birth_date.setDay(birth_day);
    }

    public String getBirthMonth() {
        return birth_date.getMonth();
    }

    public void setBirthMonth(final String birth_month) {
        birth_date.setMonth(birth_month);
    }

    public String getBirthYear() {
        return birth_date.getYear();
    }

    public void setBirthYear(final String birth_year) {
        birth_date.setYear(birth_year);
    }

    public String getBirthAddress() {
        return birth_address;
    }

    public void setBirthAddress(final String birth_address) {
        this.birth_address = birth_address;
    }

    public String getParentsMarriageDay() {
        return parents_marriage_date.getDay();
    }

    public void setParentsMarriageDay(final String parents_marriage_day) {
        parents_marriage_date.setDay(parents_marriage_day);
    }

    public String getParentsMarriageMonth() {
        return parents_marriage_date.getMonth();
    }

    public void setParentsMarriageMonth(final String parents_marriage_month) {
        parents_marriage_date.setMonth(parents_marriage_month);
    }

    public String getParentsMarriageYear() {
        return parents_marriage_date.getYear();
    }

    public void setParentsMarriageYear(final String parents_marriage_year) {
        parents_marriage_date.setYear(parents_marriage_year);
    }

    public String getParentsPlaceOfMarriage() {
        return parents_place_of_marriage;
    }

    public void setParentsPlaceOfMarriage(final String parents_place_of_marriage) {
        this.parents_place_of_marriage = parents_place_of_marriage;
    }

    public String getIllegitimateIndicator() {
        return illegitimate_indicator;
    }

    public void setIllegitimateIndicator(final String illegitimate_indicator) {
        this.illegitimate_indicator = illegitimate_indicator;
    }

    public String getInformant() {
        return informant;
    }

    public void setInformant(final String informant) {
        this.informant = informant;
    }

    public String getInformantDidNotSign() {
        return informant_did_not_sign;
    }

    public void setInformantDidNotSign(final String informant_did_not_sign) {
        this.informant_did_not_sign = informant_did_not_sign;
    }

    public String getAdoption() {
        return adoption;
    }

    public void setAdoption(final String adoption) {
        this.adoption = adoption;
    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();

        append(builder, uid, surname, forename, sex, registration_year, registration_district_number, registration_district_suffix, entry, birth_date.getYear(), mothers_maiden_surname, surname_changed,
                forename_changed, birth_date.getDay(), birth_date.getMonth(), birth_address, fathers_forename, fathers_surname, fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname_changed,
                parents_marriage_date.getDay(), parents_marriage_date.getMonth(), parents_marriage_date.getYear(), parents_place_of_marriage, illegitimate_indicator, informant, informant_did_not_sign, entry_corrected,
                adoption, image_quality);

        return builder.toString();
    }
}
