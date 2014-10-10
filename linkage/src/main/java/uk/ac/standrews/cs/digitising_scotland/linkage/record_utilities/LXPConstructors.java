package uk.ac.standrews.cs.digitising_scotland.linkage.record_utilities;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.BirthTypeLabel;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.CommonTypeLabel;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.MarriageTypeLabel;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.PersonTypeLabel;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Person;

/**
 * Domain specific views over LXP records
 * Created by al on 13/08/2014.
 */
public class LXPConstructors { // TODO refactor into factories

    public static Person createPerson(String surname, String forename, String sex, String fathers_forename, String fathers_surname, String fathers_occupation, String mothers_forename, String mothers_surname, String mothers_maiden_surname, String changed_surname, String changed_forename, String changed_mothers_maiden_surname, String original_record_id, String original_record_type, String role, String occupation)  {

        Person record = new Person();

        record.put(CommonTypeLabel.TYPE_LABEL, PersonTypeLabel.TYPE);

        record.put(PersonTypeLabel.SURNAME, surname );
        record.put(PersonTypeLabel.FORENAME, forename );
        record.put(PersonTypeLabel.SEX, sex );
        record.put(PersonTypeLabel.FATHERS_FORENAME, fathers_forename );
        record.put(PersonTypeLabel.FATHERS_SURNAME, fathers_surname );
        record.put(PersonTypeLabel.FATHERS_OCCUPATION, fathers_occupation );
        record.put(PersonTypeLabel.MOTHERS_FORENAME, mothers_forename );
        record.put(PersonTypeLabel.MOTHERS_SURNAME, mothers_surname );
        record.put(PersonTypeLabel.MOTHERS_MAIDEN_SURNAME, mothers_maiden_surname );
        record.put(PersonTypeLabel.CHANGED_SURNAME, changed_surname );
        record.put(PersonTypeLabel.CHANGED_FORENAME, changed_forename );
        record.put(PersonTypeLabel.CHANGED_MOTHERS_MAIDEN_SURNAME, changed_mothers_maiden_surname );
        record.put(PersonTypeLabel.ORIGINAL_RECORD_ID, original_record_id );
        record.put(PersonTypeLabel.ORIGINAL_RECORD_TYPE, original_record_type );
        record.put(PersonTypeLabel.ROLE, role );
        record.put(PersonTypeLabel.OCCUPATION, occupation );

        return record;
    }

    public static Person createPersonFromOwnBirthDeath( ILXP BD_record ) {

        String surname = BD_record.get(BirthTypeLabel.SURNAME);
        String forename = BD_record.get(BirthTypeLabel.FORENAME);
        String sex = BD_record.get(BirthTypeLabel.SEX);
        String fathers_forename = BD_record.get(BirthTypeLabel.FATHERS_FORENAME);

        String fathers_surname = BD_record.get(BirthTypeLabel.FATHERS_SURNAME);
        if( fathers_surname.equals("0") ) {
            fathers_surname = BD_record.get(BirthTypeLabel.SURNAME);           // TODO move this code elsewhere
        }

        String fathers_occupation = BD_record.get(BirthTypeLabel.FATHERS_OCCUPATION);
        String mothers_forename = BD_record.get(BirthTypeLabel.MOTHERS_FORENAME);

        String mothers_surname = BD_record.get(BirthTypeLabel.MOTHERS_SURNAME);
        if( mothers_surname.equals("0") ) {
                mothers_surname = BD_record.get(BirthTypeLabel.SURNAME);        // TODO move this code elsewhere
        }

        String mothers_maiden_surname = BD_record.get(BirthTypeLabel.MOTHERS_MAIDEN_SURNAME);
        String changed_surname = BD_record.get(BirthTypeLabel.CHANGED_SURNAME);
        String changed_forename = BD_record.get(BirthTypeLabel.CHANGED_FORENAME);
        String changed_mothers_maiden_surname = BD_record.get(BirthTypeLabel.CHANGED_MOTHERS_MAIDEN_SURNAME);

        String original_record_id = Integer.toString(BD_record.getId());
        String original_record_type = BD_record.get(BirthTypeLabel.TYPE_LABEL);
        String role = "baby";
        String occupation = "";

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);
    }

    public static Person createFatherFromChildsBirthDeath( Person child, Birth BD_record ) {

        if( child.get( PersonTypeLabel.FATHERS_SURNAME ).equals( "" ) ) {
            return null;
        }

        String surname = child.get( PersonTypeLabel.FATHERS_SURNAME );
        String forename = child.get(PersonTypeLabel.FATHERS_FORENAME);
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

        String original_record_id = Integer.toString(BD_record.getId());
        String original_record_type = BD_record.get(BirthTypeLabel.TYPE_LABEL);
        String role = "father";
        String occupation = child.get(PersonTypeLabel.FATHERS_OCCUPATION);

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);
    }

    public static Person createMotherFromChildsBirthDeath(  ILXP child, ILXP BD_record ) {

        if( child.get( PersonTypeLabel.FATHERS_SURNAME ).equals( "" ) ) {
            return null;
        }

        String surname = child.get( PersonTypeLabel.MOTHERS_SURNAME );
        String forename = child.get(PersonTypeLabel.MOTHERS_FORENAME);
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

        String original_record_id = Integer.toString(BD_record.getId());
        String original_record_type = BD_record.get(BirthTypeLabel.TYPE_LABEL);
        String role = "mother";
        String occupation = child.get(PersonTypeLabel.FATHERS_OCCUPATION);

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);

    }

    /**
     * Creates a Person record for the bride for a given marriage record
     * @param marriage_record  a record from which to extract person information
     * @return the LXP representing the bride
     */
    public static Person createBrideFromMarriageRecord(ILXP marriage_record) {

        String surname = marriage_record.get(MarriageTypeLabel.BRIDE_SURNAME);
        String forename = marriage_record.get(MarriageTypeLabel.BRIDE_FORENAME);
        String sex = "F"; //  this is the bride
        String fathers_forename = marriage_record.get(MarriageTypeLabel.BRIDE_FATHERS_FORENAME);

        String fathers_surname = marriage_record.get(MarriageTypeLabel.BRIDE_FATHERS_SURNAME);
        if( fathers_surname.equals("0") ) {
            fathers_surname = marriage_record.get(MarriageTypeLabel.BRIDE_SURNAME); // TODO factor out?
        }

        String fathers_occupation = marriage_record.get(MarriageTypeLabel.BRIDE_FATHER_OCCUPATION);
        String mothers_forename =  marriage_record.get(MarriageTypeLabel.BRIDE_MOTHERS_FORENAME);
        String mothers_surname = marriage_record.get(MarriageTypeLabel.BRIDE_FATHERS_SURNAME);   // Assumes the mother's surname is same as father's - OK??
        if( mothers_surname.equals("0") ) {
            mothers_surname =  marriage_record.get(MarriageTypeLabel.SURNAME);
        }

        String mothers_maiden_surname = ""; // unknown??    // TODO check these
        String changed_surname = ""; // unknown
        String changed_forename = ""; // unknown
        String changed_mothers_maiden_surname = ""; // unknown

        String original_record_id = Integer.toString(marriage_record.getId());
        String original_record_type = marriage_record.get(MarriageTypeLabel.TYPE_LABEL);
        String role = "bride";
        String occupation = "";

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);
    }

    /**
     * Creates a Person record for the groom for a given marriage record
     * @param marriage_record  a record from which to extract person information
     * @return the LXP representing the groom
     */
    public static Person createGroomFromMarriageRecord(ILXP marriage_record) {

        String surname = marriage_record.get(MarriageTypeLabel.GROOM_SURNAME);
        String forename = marriage_record.get(MarriageTypeLabel.GROOM_FORENAME);
        String sex = "F"; //  this is the bride
        String fathers_forename = marriage_record.get(MarriageTypeLabel.GROOM_FATHERS_FORENAME);

        String fathers_surname = marriage_record.get(MarriageTypeLabel.GROOM_FATHERS_SURNAME);
        if( fathers_surname.equals("0") ) {
            fathers_surname = marriage_record.get(MarriageTypeLabel.GROOM_SURNAME); // TODO factor out?
        }

        String fathers_occupation = marriage_record.get(MarriageTypeLabel.GROOM_FATHERS_OCCUPATION);
        String mothers_forename =  marriage_record.get(MarriageTypeLabel.GROOM_MOTHERS_FORENAME);
        String mothers_surname = marriage_record.get(MarriageTypeLabel.GROOM_FATHERS_SURNAME);
        if( mothers_surname.equals("0") ) {
            mothers_surname =  marriage_record.get(MarriageTypeLabel.SURNAME);
        }

        String mothers_maiden_surname = ""; // unknown??    // TODO check these
        String changed_surname = ""; // unknown
        String changed_forename = ""; // unknown
        String changed_mothers_maiden_surname = ""; // unknown

        String original_record_id = Integer.toString(marriage_record.getId());
        String original_record_type = marriage_record.get(MarriageTypeLabel.TYPE_LABEL);
        String role = "bride";
        String occupation = "";

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);

    }

    public static Person createBridesFatherFromMarriageRecord(ILXP bride, ILXP marriage_record) {// TODO write me

        String surname = marriage_record.get(MarriageTypeLabel.BRIDE_FATHERS_SURNAME);
        if( surname.equals("0") ) {
            surname = marriage_record.get(MarriageTypeLabel.BRIDE_SURNAME); // TODO factor out?
        }

        String forename = marriage_record.get(MarriageTypeLabel.BRIDE_FATHERS_FORENAME);
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

        String original_record_id = Integer.toString(marriage_record.getId());
        String original_record_type = marriage_record.get(MarriageTypeLabel.TYPE_LABEL);
        String role = "brides_father";
        String occupation = marriage_record.get(MarriageTypeLabel.BRIDE_FATHER_OCCUPATION);

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);

    }

    public static Person createBridesMotherFromMarriageRecord(ILXP bride, ILXP marriage_record) { // TODO write me

        String surname = marriage_record.get(MarriageTypeLabel.BRIDE_MOTHERS_MAIDEN_SURNAME);  //<<<<<<<<<<<<< TODO see below
//        if( surname.equals("0") ) {
//            surname = marriage_record.get(MarriageLabels.BRIDE_SURNAME); // TODO factor out?
//        }

        String forename = marriage_record.get(MarriageTypeLabel.BRIDE_MOTHERS_FORENAME);
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

        String original_record_id = Integer.toString(marriage_record.getId());
        String original_record_type = marriage_record.get(MarriageTypeLabel.TYPE_LABEL);
        String role = "brides_mother";
        String occupation = ""; // unknown

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);  // TODO Person should also have maiden name???

    }

    public static Person createGroomsFatherFromMarriageRecord(ILXP groom, ILXP marriage_record) {// TODO write me

        String surname = marriage_record.get(MarriageTypeLabel.GROOM_FATHERS_SURNAME);
        if( surname.equals("0") ) {
            surname = marriage_record.get(MarriageTypeLabel.GROOM_SURNAME); // TODO factor out?
        }

        String forename = marriage_record.get(MarriageTypeLabel.GROOM_FATHERS_FORENAME);
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

        String original_record_id = Integer.toString(marriage_record.getId());
        String original_record_type = marriage_record.get(MarriageTypeLabel.TYPE_LABEL);
        String role = "grooms_father";
        String occupation = marriage_record.get(MarriageTypeLabel.GROOM_FATHERS_OCCUPATION);

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);


    }

    public static Person createGroomsMotherFromMarriageRecord(ILXP groom, ILXP marriage_record) {// TODO write me

        String surname = marriage_record.get(MarriageTypeLabel.GROOM_MOTHERS_MAIDEN_SURNAME);  //<<<<<<<<<<<<< TODO see below
//        if( surname.equals("0") ) {
//            surname = marriage_record.get(MarriageLabels.GROOM_SURNAME); // TODO factor out?
//        }

        String forename = marriage_record.get(MarriageTypeLabel.GROOM_MOTHERS_FORENAME);
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

        String original_record_id = Integer.toString(marriage_record.getId());
        String original_record_type = marriage_record.get(MarriageTypeLabel.TYPE_LABEL);
        String role = "grooms_mother";
        String occupation = ""; // unknown

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);  // TODO Person should also have maiden name???

    }


}
