package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.types.LXP_SCALAR;
import uk.ac.standrews.cs.digitising_scotland.jstore.types.Types;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import static uk.ac.standrews.cs.digitising_scotland.jstore.types.LXPBaseType.STRING;

/**
 * Created by al on 03/10/2014.
 */
public class Person extends AbstractLXP {

    // Person labels - these should not really be here (maybe generated?

    @LXP_SCALAR(type = STRING)
    public static final String SURNAME = "surname";
    @LXP_SCALAR(type = STRING)
    public static final String FORENAME = "forename";
    @LXP_SCALAR(type = STRING)
    public static final String SEX = "sex";
    @LXP_SCALAR(type = STRING)
    public static final String FATHERS_FORENAME = "fathers_forename";
    @LXP_SCALAR(type = STRING)
    public static final String FATHERS_SURNAME = "fathers_surname";
    @LXP_SCALAR(type = STRING)
    public static final String FATHERS_OCCUPATION = "fathers_occupation";
    @LXP_SCALAR(type = STRING)
    public static final String MOTHERS_FORENAME = "mothers_forename";
    @LXP_SCALAR(type = STRING)
    public static final String MOTHERS_SURNAME = "mothers_surname";
    @LXP_SCALAR(type = STRING)
    public static final String MOTHERS_MAIDEN_SURNAME = "mothers_maiden_surname";
    @LXP_SCALAR(type = STRING)
    public static final String CHANGED_SURNAME = "changed_surname";
    @LXP_SCALAR(type = STRING)
    public static final String CHANGED_FORENAME = "changed_forename";
    @LXP_SCALAR(type = STRING)
    public static final String CHANGED_MOTHERS_MAIDEN_SURNAME = "changed_mothers_maiden_surname";
    @LXP_SCALAR(type = STRING)
    public static final String ORIGINAL_RECORD_ID = "original_record_id";
    @LXP_SCALAR(type = STRING)
    public static final String ORIGINAL_RECORD_TYPE = "original_record_type";
    @LXP_SCALAR(type = STRING)
    public static final String ROLE = "role";
    @LXP_SCALAR(type = STRING)
    public static final String OCCUPATION = "occupation";

    // Marriage record labels - these should not really be here (maybe generated?

    private static final String GROOM_SURNAME = "groom_surname";    // TODO check these everywhere for duplication
    private static final String GROOM_FORENAME = "groom_forename";
    private static final String BRIDE_SURNAME = "bride_surname";
    private static final String BRIDE_FORENAME = "bride_forename";
    private static final String BRIDE_FATHERS_FORENAME = "bride_fathers_forename";
    private static final String BRIDE_FATHERS_SURNAME = "bride_fathers_surname";
    private static final String BRIDE_FATHER_OCCUPATION = "bride_fathers_occupation";
    private static final String BRIDE_MOTHERS_MAIDEN_SURNAME = "bride_mothers_maiden_surname";
    private static final String GROOM_FATHERS_SURNAME = "groom_fathers_surname";
    private static final String GROOM_FATHERS_OCCUPATION = "groom_fathers_occupation";
    private static final String GROOM_MOTHERS_MAIDEN_SURNAME = "groom_mothers_maiden_surname";
    private static final String GROOM_MOTHERS_FORENAME = "groom_mothers_forename";
    private static final String GROOM_FATHERS_FORENAME = "groom_fathers_forename";
    private static final String BRIDE_MOTHERS_FORENAME = "bride_mothers_forename";

    public Person() {
        super();
    }

    public Person(JSONReader reader) throws PersistentObjectException {

        super(reader);
    }

    public Person(String surname, String forename, String sex, String fathers_forename, String fathers_surname, String fathers_occupation, String mothers_forename, String mothers_surname, String mothers_maiden_surname, String changed_surname, String changed_forename, String changed_mothers_maiden_surname, String original_record_id, String original_record_type, String role, String occupation) {

        this();
        put(SURNAME, surname);
        put(FORENAME, forename);
        put(SEX, sex);
        put(FATHERS_FORENAME, fathers_forename);
        put(FATHERS_SURNAME, fathers_surname);
        put(FATHERS_OCCUPATION, fathers_occupation);
        put(MOTHERS_FORENAME, mothers_forename);
        put(MOTHERS_SURNAME, mothers_surname);
        put(MOTHERS_MAIDEN_SURNAME, mothers_maiden_surname);
        put(CHANGED_SURNAME, changed_surname);
        put(CHANGED_FORENAME, changed_forename);
        put(CHANGED_MOTHERS_MAIDEN_SURNAME, changed_mothers_maiden_surname);
        put(ORIGINAL_RECORD_ID, original_record_id);
        put(ORIGINAL_RECORD_TYPE, original_record_type);
        put(ROLE, role);
        put(OCCUPATION, occupation);
    }

    public static Person createPersonFromOwnBirthDeath(ILXP BD_record) throws KeyNotFoundException, TypeMismatchFoundException {// TODO rewrite as typed

        String surname = BD_record.getString(SURNAME);
        String forename = BD_record.getString(FORENAME);
        String sex = BD_record.getString(SEX);
        String fathers_forename = BD_record.getString(FATHERS_FORENAME);

        String fathers_surname = BD_record.getString(FATHERS_SURNAME);
        if (fathers_surname.equals("0")) {
            fathers_surname = BD_record.getString(SURNAME);           // TODO move this code elsewhere
        }

        String fathers_occupation = BD_record.getString(FATHERS_OCCUPATION);
        String mothers_forename = BD_record.getString(MOTHERS_FORENAME);

        String mothers_surname = BD_record.getString(MOTHERS_SURNAME);
        if (mothers_surname.equals("0")) {
            mothers_surname = BD_record.getString(SURNAME);        // TODO move this code elsewhere
        }

        String mothers_maiden_surname = BD_record.getString(MOTHERS_MAIDEN_SURNAME);
        String changed_surname = BD_record.getString(CHANGED_SURNAME);
        String changed_forename = BD_record.getString(CHANGED_FORENAME);
        String changed_mothers_maiden_surname = BD_record.getString(CHANGED_MOTHERS_MAIDEN_SURNAME);

        String original_record_id = Long.toString(BD_record.getId());
        String original_record_type = BD_record.getString(Types.LABEL);
        String role = "baby";
        String occupation = "";

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname, changed_forename, changed_mothers_maiden_surname, original_record_id, original_record_type, role, occupation);
    }

    public static Person createFatherFromChildsBirthDeath(Person child, Birth BD_record) throws KeyNotFoundException, TypeMismatchFoundException {// TODO rewrite as typed

        if (child.getString(FATHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = child.getString(FATHERS_SURNAME);
        String forename = child.getString(FATHERS_FORENAME);
        String sex = "M"; //  this is the father
        String fathers_forename = ""; // unknown - father of father
        String fathers_surname = ""; //unknown - father of father  - could guess but no

        String fathers_occupation = ""; // unknown - father of father
        String mothers_forename = ""; // unknown - mother of father
        String mothers_surname = ""; //unknown - mother of father  - could guess but no

        String mothers_maiden_surname = ""; // unknown - mother of father
        String changed_surname = ""; // unknown
        String changed_forename = ""; // unknown
        String changed_mothers_maiden_surname = ""; // unknown

        String original_record_id = Long.toString(BD_record.getId());
        String original_record_type = BD_record.getString(Types.LABEL);
        String role = "father";
        String occupation = child.getString(FATHERS_OCCUPATION);

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname, changed_forename, changed_mothers_maiden_surname, original_record_id, original_record_type, role, occupation);
    }

    public static Person createMotherFromChildsBirthDeath(ILXP child, ILXP BD_record) throws KeyNotFoundException, TypeMismatchFoundException {// TODO rewrite as typed

        if (child.getString(FATHERS_SURNAME).equals("")) {
            return null;
        }

        String surname = child.getString(MOTHERS_SURNAME);
        String forename = child.getString(MOTHERS_FORENAME);
        String sex = "F"; //  this is the mother
        String fathers_forename = ""; // unknown - father of mother
        String fathers_surname = ""; //unknown - father of mother  - could guess but no

        String fathers_occupation = ""; // unknown - father of mother
        String mothers_forename = ""; // unknown - mother of mother
        String mothers_surname = ""; //unknown - mother of mother  - could guess but no

        String mothers_maiden_surname = ""; // unknown - mother of mother
        String changed_surname = ""; // unknown
        String changed_forename = ""; // unknown
        String changed_mothers_maiden_surname = ""; // unknown

        String original_record_id = Long.toString(BD_record.getId());
        String original_record_type = BD_record.getString(Types.LABEL);
        String role = "mother";
        String occupation = child.getString(FATHERS_OCCUPATION);

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname, changed_forename, changed_mothers_maiden_surname, original_record_id, original_record_type, role, occupation);

    }

    /**
     * Creates a Person record for the bride for a given marriage record
     *
     * @param marriage_record a record from which to extract person information
     * @return the LXP representing the bride
     */
    public static Person createBrideFromMarriageRecord(ILXP marriage_record) throws KeyNotFoundException, TypeMismatchFoundException {// TODO rewrite as typed

        String surname = marriage_record.getString(BRIDE_SURNAME);
        String forename = marriage_record.getString(BRIDE_FORENAME);
        String sex = "F"; //  this is the bride
        String fathers_forename = marriage_record.getString(BRIDE_FATHERS_FORENAME);

        String fathers_surname = marriage_record.getString(BRIDE_FATHERS_SURNAME);
        if (fathers_surname.equals("0")) {
            fathers_surname = marriage_record.getString(BRIDE_SURNAME); // TODO factor out?
        }

        String fathers_occupation = marriage_record.getString(BRIDE_FATHER_OCCUPATION);
        String mothers_forename = marriage_record.getString(BRIDE_MOTHERS_FORENAME);
        String mothers_surname = marriage_record.getString(BRIDE_FATHERS_SURNAME);   // Assumes the mother's surname is same as father's - OK??
        if (mothers_surname.equals("0")) {
            mothers_surname = marriage_record.getString(SURNAME);
        }

        String mothers_maiden_surname = ""; // unknown??    // TODO check these
        String changed_surname = ""; // unknown
        String changed_forename = ""; // unknown
        String changed_mothers_maiden_surname = ""; // unknown

        String original_record_id = Long.toString(marriage_record.getId());
        String original_record_type = marriage_record.getString(Types.LABEL);
        String role = "bride";
        String occupation = "";

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname, changed_forename, changed_mothers_maiden_surname, original_record_id, original_record_type, role, occupation);
    }

    /**
     * Creates a Person record for the groom for a given marriage record
     *
     * @param marriage_record a record from which to extract person information
     * @return the LXP representing the groom
     */
    public static Person createGroomFromMarriageRecord(ILXP marriage_record) throws KeyNotFoundException, TypeMismatchFoundException {// TODO rewrite as typed

        String surname = marriage_record.getString(GROOM_SURNAME);
        String forename = marriage_record.getString(GROOM_FORENAME);
        String sex = "F"; //  this is the bride
        String fathers_forename = marriage_record.getString(GROOM_FATHERS_FORENAME);

        String fathers_surname = marriage_record.getString(GROOM_FATHERS_SURNAME);
        if (fathers_surname.equals("0")) {
            fathers_surname = marriage_record.getString(GROOM_SURNAME); // TODO factor out?
        }

        String fathers_occupation = marriage_record.getString(GROOM_FATHERS_OCCUPATION);
        String mothers_forename = marriage_record.getString(GROOM_MOTHERS_FORENAME);
        String mothers_surname = marriage_record.getString(GROOM_FATHERS_SURNAME);
        if (mothers_surname.equals("0")) {
            mothers_surname = marriage_record.getString(SURNAME);
        }

        String mothers_maiden_surname = ""; // unknown??    // TODO check these
        String changed_surname = ""; // unknown
        String changed_forename = ""; // unknown
        String changed_mothers_maiden_surname = ""; // unknown

        String original_record_id = Long.toString(marriage_record.getId());
        String original_record_type = marriage_record.getString(Types.LABEL);
        String role = "bride";
        String occupation = "";

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname, changed_forename, changed_mothers_maiden_surname, original_record_id, original_record_type, role, occupation);

    }

    public static Person createBridesFatherFromMarriageRecord(ILXP bride, ILXP marriage_record) throws KeyNotFoundException, TypeMismatchFoundException {// TODO rewrite as typed

        String surname = marriage_record.getString(BRIDE_FATHERS_SURNAME);
        if (surname.equals("0")) {
            surname = marriage_record.getString(BRIDE_SURNAME); // TODO factor out?
        }

        String forename = marriage_record.getString(BRIDE_FATHERS_FORENAME);
        String sex = "M"; //  this is the brides father

        String fathers_forename = ""; // unknown - father of bride's father
        String fathers_surname = ""; //unknown - father of bride's father

        String fathers_occupation = ""; // unknown - father of bride's father
        String mothers_forename = ""; // unknown - mother of bride's father
        String mothers_surname = ""; //unknown - mother of bride's father

        String mothers_maiden_surname = ""; // unknown - mother bride's father
        String changed_surname = ""; // unknown
        String changed_forename = ""; // unknown
        String changed_mothers_maiden_surname = ""; // unknown

        String original_record_id = Long.toString(marriage_record.getId());
        String original_record_type = marriage_record.getString(Types.LABEL);
        String role = "brides_father";
        String occupation = marriage_record.getString(BRIDE_FATHER_OCCUPATION);

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname, changed_forename, changed_mothers_maiden_surname, original_record_id, original_record_type, role, occupation);

    }

    public static Person createBridesMotherFromMarriageRecord(ILXP bride, ILXP marriage_record) throws KeyNotFoundException, TypeMismatchFoundException { // TODO rewrite as typed

        String surname = marriage_record.getString(BRIDE_MOTHERS_MAIDEN_SURNAME);  //<<<<<<<<<<<<< TODO see below
//        if( surname.equals("0") ) {
//            surname = marriage_record.getString(MarriageLabels.BRIDE_SURNAME); // TODO factor out?
//        }

        String forename = marriage_record.getString(BRIDE_MOTHERS_FORENAME);
        String sex = "M"; //  this is the brides father

        String fathers_forename = ""; // unknown - father of bride's mother
        String fathers_surname = ""; //unknown - father of bride's mother

        String fathers_occupation = ""; // unknown - father of bride's mother
        String mothers_forename = ""; // unknown - mother of bride's mother
        String mothers_surname = ""; //unknown - mother of bride's mother

        String mothers_maiden_surname = ""; // unknown - mother bride's mother
        String changed_surname = ""; // unknown
        String changed_forename = ""; // unknown
        String changed_mothers_maiden_surname = ""; // unknown

        String original_record_id = Long.toString(marriage_record.getId());
        String original_record_type = marriage_record.getString(Types.LABEL);
        String role = "brides_mother";
        String occupation = ""; // unknown

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname, changed_forename, changed_mothers_maiden_surname, original_record_id, original_record_type, role, occupation);  // TODO Person should also have maiden name???

    }

    public static Person createGroomsFatherFromMarriageRecord(ILXP groom, ILXP marriage_record) throws KeyNotFoundException, TypeMismatchFoundException {// TODO rewrite as typed

        String surname = marriage_record.getString(GROOM_FATHERS_SURNAME);
        if (surname.equals("0")) {
            surname = marriage_record.getString(GROOM_SURNAME); // TODO factor out?
        }

        String forename = marriage_record.getString(GROOM_FATHERS_FORENAME);
        String sex = "M"; //  this is the brides father

        String fathers_forename = ""; // unknown - father of groom's father
        String fathers_surname = ""; //unknown - father of groom's father

        String fathers_occupation = ""; // unknown - father of groom's father
        String mothers_forename = ""; // unknown - mother of groom's father
        String mothers_surname = ""; //unknown - mother of groom's father

        String mothers_maiden_surname = ""; // unknown - mother groom's father
        String changed_surname = ""; // unknown
        String changed_forename = ""; // unknown
        String changed_mothers_maiden_surname = ""; // unknown

        String original_record_id = Long.toString(marriage_record.getId());
        String original_record_type = marriage_record.getString(Types.LABEL);
        String role = "grooms_father";
        String occupation = marriage_record.getString(GROOM_FATHERS_OCCUPATION);

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname, changed_forename, changed_mothers_maiden_surname, original_record_id, original_record_type, role, occupation);


    }

    public static Person createGroomsMotherFromMarriageRecord(ILXP groom, ILXP marriage_record) throws KeyNotFoundException, TypeMismatchFoundException {// TODO rewrite as typed

        String surname = marriage_record.getString(GROOM_MOTHERS_MAIDEN_SURNAME);  //<<<<<<<<<<<<< TODO see below
//        if( surname.equals("0") ) {
//            surname = marriage_record.getString(MarriageLabels.GROOM_SURNAME); // TODO factor out?
//        }

        String forename = marriage_record.getString(GROOM_MOTHERS_FORENAME);
        String sex = "M"; //  this is the brides father

        String fathers_forename = ""; // unknown - father of bride's mother
        String fathers_surname = ""; //unknown - father of bride's mother

        String fathers_occupation = ""; // unknown - father of bride's mother
        String mothers_forename = ""; // unknown - mother of bride's mother
        String mothers_surname = ""; //unknown - mother of bride's mother

        String mothers_maiden_surname = ""; // unknown - mother bride's mother
        String changed_surname = ""; // unknown
        String changed_forename = ""; // unknown
        String changed_mothers_maiden_surname = ""; // unknown

        String original_record_id = Long.toString(marriage_record.getId());
        String original_record_type = marriage_record.getString(Types.LABEL);
        String role = "grooms_mother";
        String occupation = ""; // unknown

        return new Person(surname, forename, sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname, changed_forename, changed_mothers_maiden_surname, original_record_id, original_record_type, role, occupation);  // TODO Person should also have maiden name???

    }


}
