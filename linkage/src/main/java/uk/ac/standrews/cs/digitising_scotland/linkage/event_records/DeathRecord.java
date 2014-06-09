package uk.ac.standrews.cs.digitising_scotland.linkage.event_records;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.DBBackedPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.Person;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * A representation of a Birth Record in the form used by the Digitising Scotland Project.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 *         <p/>
 *         Fields are as follows:
 *         <p/>
 *         Ref Field
 *         1. Unique'Record'Identifier 1
 *         2. Surname 1
 *         3. Forename 1
 *         4. Sex 1
 *         5. Year of Registration 1
 *         6. Registration District Number 1
 *         7. Registration District Suffix 1
 *         8. Entry 1
 *         9. Death Year 1
 *         10. Age at Death 1
 *         11. Mother’s Maiden Surname 2
 *         12. Changed Surname 3(10)
 *         13. Changed Forename 3(10)
 *         14. Changed Death Age 3(23) (number followed by 'M', 'W', 'D' or 'H' for months/weeks/dates/hours when Age at Death < 1)
 *         15. Date of Birth (Only for 1966 – 1973, e.g. '17091881' or empty)
 *         16. Occupation
 *         17. Marital Status 3(20) ('B', 'D', 'M', 'S', 'W' or empty)
 *         18. Spouse’s Name(s) 3(22)
 *         19. Spouse’s Occupation(s) 3(22)
 *         20. Death Month
 *         21. Death Day
 *         22. Place of Death
 *         23. Father’s Forename
 *         24. Father’s Surname 3(11) ('0' if same as Surname)
 *         25. Father’s Occupation
 *         26. Father Deceased 3(21) ('Y' or empty)
 *         27. Mother’s Forename
 *         28. Changed Mothers Maiden Surname 3(10)
 *         29. Mother Deceased 3(21) ('Y' or empty)
 *         30. Cause of Death (a)
 *         31. Cause of Death (b) 2
 *         32. Cause of Death (c) 2
 *         33. Certifying Doctor
 *         34. Corrected Entry 3(12) ('1', '2', '3' or empty)
 *         35. Image Quality ('1', '2' or empty)
 *         <p/>
 *         <p/>
 *         Examples of death records:
 *         <p/>
 *         6000002|GARRIOCH|ANN|F|1855|010|1|16|1855|131||||||MIDWIFE|W|ROBERT_SCOTT|STOREMAN|5|18|SCALLOWAY_TINGWALL_ZETLAND|PETER|0|SAILOR|D|GRACE|TULLOCH|D|AGE_&_INFIMITIES||||||
 *         6000003|_ANDERSON|HELEN|F|1855|010|1|17|1855|19||||||DOMESTIC_SERVANT|S||||5|19|BERRY_NEAR_TALLOWAY_TINGWALL_ZETLAND|PETER_ANDERSON|CATTLE_DEALER||PHILIDELPHIA|POTTINGER||PNEUMONIA||||||
 *         6000004|MOUAT|JAMES|M|1855|010|1|18|1855|0||||82D||||||5|25|NORTH_HAMMERSLAND_TINGWALL_ZETLAND|JEREMIAH|0|FARMER||MARY|IRVINE||COSTIVENESS||||||
 *         8000001|WALTERSON|ROBINA|F|1966|010|00|0009|1966|84|||||17091881||W|ROBERT_JAM ES_WALTERSON|CROFTER|9|16|WEST_BURRAFIRTH_BRIDGE_OF_WALLS|JAMES|CH RISTIE|FISHERMAN|Y|AGNES|CHRISTIE|Y|HYPOSTATIC_PNEUMONIA|CORONARY_THR OMBOSIS|SENILITY|J._ROBERTSON_DURHAM|||
 */
public class DeathRecord extends IndividualRecord {

    public static final int FIRST_YEAR_DOB_PRESENT = 1966;
    private static final SimpleDateFormat DOB_DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

    private DateRecord death_date;

    private String death_age;
    private String death_age_changed;

    private String death_place;
    private String death_cause_a;
    private String death_cause_b;
    private String death_cause_c;
    private String certifying_doctor;

    private String birth_date;
    private String occupation;

    private String father_deceased;
    private String mother_deceased;

    private String marital_status;
    private String spouses_names;
    private String spouses_occupations;

    public DeathRecord(final Person person) {

        death_date = new DateRecord();

        final DBBackedPartnership family = person.getParentsFamily();

        // Attributes associated with individual
        setUid(String.valueOf(person.getID()));
        setSex(String.valueOf(person.getGender()));
        setForename(person.getFirstName());
        setSurname(person.getSurname());
        setOccupation(person.getOccupation());
        setDeathCauseA(person.getCauseOfDeath());

        Date birth_date = person.getBirthDate();
        Date death_date = (Date) person.getDeathDate();

        processDates(birth_date, death_date);

        if (family != null) { // could refactor this with the **new** bride and groom methods...

            // Attributes associated with individual's parents
            for (final Person parent : family.getPartners()) {
                if (parent.getGender() == IPerson.MALE) { // the father

                    setFathersForename(parent.getFirstName());
                    setFathersSurname(getRecordedParentsSurname(parent.getSurname(), person.getSurname()));
                    setFathersOccupation(parent.getOccupation());
                } else { // the mother

                    setMothersForename(parent.getFirstName());
                    setMothersSurname(parent.getSurname());
                    setMothersMaidenSurname(parent.getMaidenName());
                }
            }
        }
    }

    public static synchronized String formatDate(final Date date) {
        return DOB_DATE_FORMAT.format(date);
    }

    public static synchronized java.util.Date parseDate(final String date_string) throws ParseException {

        return DOB_DATE_FORMAT.parse(date_string);
    }

    private void processDates(final Date birth_date, final Date death_date) {

        if (death_date != null) {

            int birth_year = DateManipulation.dateToYear(birth_date);
            int death_year = DateManipulation.dateToYear(death_date);

            setDeathYear(String.valueOf(death_year));
            setDeathAge(String.valueOf(death_year - birth_year));

            if (death_year >= DeathRecord.FIRST_YEAR_DOB_PRESENT) {
                setBirthDate(formatDate(birth_date));
            }
        }
    }

    public String getDeathDay() {
        return death_date.getDay();
    }

    public void setDeathDay(final String death_day) {
        death_date.setDay(death_day);
    }

    public String getDeathMonth() {
        return death_date.getMonth();
    }

    public void setDeathMonth(final String death_month) {
        death_date.setMonth(death_month);
    }

    public String getDeathYear() {
        return death_date.getYear();
    }

    public void setDeathYear(final String death_year) {
        death_date.setYear(death_year);
    }

    public String getDeathAge() {
        return death_age;
    }

    public void setDeathAge(final String death_age) {
        this.death_age = death_age;
    }

    public String getDeathAgeChanged() {
        return death_age_changed;
    }

    public void setDeathAgeChanged(final String death_age_changed) {
        this.death_age_changed = death_age_changed;
    }

    public String getDeathPlace() {
        return death_place;
    }

    public void setDeathPlace(final String death_place) {
        this.death_place = death_place;
    }

    public String getDeathCauseA() {
        return death_cause_a;
    }

    public void setDeathCauseA(final String death_cause_a) {
        this.death_cause_a = death_cause_a;
    }

    public String getDeathCauseB() {
        return death_cause_b;
    }

    public void setDeathCauseB(final String death_cause_b) {
        this.death_cause_b = death_cause_b;
    }

    public String getDeathCauseC() {
        return death_cause_c;
    }

    public void setDeathCauseC(final String death_cause_c) {
        this.death_cause_c = death_cause_c;
    }

    public String getCertifyingDoctor() {
        return certifying_doctor;
    }

    public void setCertifyingDoctor(final String certifying_doctor) {
        this.certifying_doctor = certifying_doctor;
    }

    public String getBirthDate() {
        return birth_date;
    }

    public void setBirthDate(final String birth_date) {
        this.birth_date = birth_date;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(final String occupation) {
        this.occupation = occupation;
    }

    public String getFatherDeceased() {
        return father_deceased;
    }

    public void setFatherDeceased(final String father_deceased) {
        this.father_deceased = father_deceased;
    }

    public String getMotherDeceased() {
        return mother_deceased;
    }

    public void setMotherDeceased(final String mother_deceased) {
        this.mother_deceased = mother_deceased;
    }

    public String getMaritalStatus() {
        return marital_status;
    }

    public void setMaritalStatus(final String marital_status) {
        this.marital_status = marital_status;
    }

    public String getSpousesNames() {
        return spouses_names;
    }

    public void setSpousesNames(final String spouses_names) {
        this.spouses_names = spouses_names;
    }

    public String getSpousesOccupations() {
        return spouses_occupations;
    }

    public void setSpousesOccupations(final String spouses_occupations) {

        this.spouses_occupations = spouses_occupations;
    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();

        append(builder, uid, surname, forename, sex, registration_year, registration_district_number, registration_district_suffix, entry, death_date.getYear(), death_age, mothers_maiden_surname,
                surname_changed, forename_changed, death_age_changed, birth_date, occupation, marital_status, spouses_names, spouses_occupations, death_date.getMonth(), death_date.getDay(),
                death_place, fathers_forename, fathers_surname, fathers_occupation, father_deceased, mothers_forename, mothers_surname, mothers_maiden_surname_changed, mother_deceased,
                death_cause_a, death_cause_b, death_cause_c, certifying_doctor, entry_corrected, image_quality);

        return builder.toString();
    }
}
