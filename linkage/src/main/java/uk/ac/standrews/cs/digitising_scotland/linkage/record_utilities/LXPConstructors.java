package uk.ac.standrews.cs.digitising_scotland.linkage.record_utilities;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.BirthLabels;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.CommonLabels;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.MarriageLabels;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.PersonLabels;

/**
 * Domain specific views over LXP records
 * Created by al on 13/08/2014.
 */
public class LXPConstructors {

    public static ILXP createPerson(String surname, String forename, String sex, String fathers_forename, String fathers_surname, String fathers_occupation, String mothers_forename, String mothers_surname, String mothers_maiden_surname, String changed_surname, String changed_forename, String changed_mothers_maiden_surname, String original_record_id, String original_record_type, String role, String occupation) {

        ILXP record = new LXP();

        record.put(CommonLabels.TYPE_LABEL, PersonLabels.TYPE);

        record.put(PersonLabels.SURNAME, surname );
        record.put(PersonLabels.FORENAME, forename );
        record.put(PersonLabels.SEX, sex );
        record.put(PersonLabels.FATHERS_FORENAME, fathers_forename );
        record.put(PersonLabels.FATHERS_SURNAME, fathers_surname );
        record.put(PersonLabels.FATHERS_OCCUPATION, fathers_occupation );
        record.put(PersonLabels.MOTHERS_FORENAME, mothers_forename );
        record.put(PersonLabels.MOTHERS_SURNAME, mothers_surname );
        record.put(PersonLabels.MOTHERS_MAIDEN_SURNAME, mothers_maiden_surname );
        record.put(PersonLabels.CHANGED_SURNAME, changed_surname );
        record.put(PersonLabels.CHANGED_FORENAME, changed_forename );
        record.put(PersonLabels.CHANGED_MOTHERS_MAIDEN_SURNAME, changed_mothers_maiden_surname );
        record.put(PersonLabels.ORIGINAL_RECORD_ID, original_record_id );
        record.put(PersonLabels.ORIGINAL_RECORD_TYPE, original_record_type );
        record.put(PersonLabels.ROLE, role );
        record.put(PersonLabels.OCCUPATION, occupation );

        return record;
    }

    public static ILXP createPersonFromOwnBirthDeath( ILXP BD_record ) {

        String surname = BD_record.get(BirthLabels.SURNAME);
        String forename = BD_record.get(BirthLabels.FORENAME);
        String sex = BD_record.get(BirthLabels.SEX);
        String fathers_forename = BD_record.get(BirthLabels.FATHERS_FORENAME);

        String fathers_surname = BD_record.get(BirthLabels.FATHERS_SURNAME);
        if( fathers_surname.equals("0") ) {
            fathers_surname = BD_record.get(BirthLabels.SURNAME);           // TODO move this code elsewhere
        }

        String fathers_occupation = BD_record.get(BirthLabels.FATHERS_OCCUPATION);
        String mothers_forename = BD_record.get(BirthLabels.MOTHERS_FORENAME);

        String mothers_surname = BD_record.get(BirthLabels.MOTHERS_SURNAME);
        if( mothers_surname.equals("0") ) {
                mothers_surname = BD_record.get(BirthLabels.SURNAME);        // TODO move this code elsewhere
        }

        String mothers_maiden_surname = BD_record.get(BirthLabels.MOTHERS_MAIDEN_SURNAME);
        String changed_surname = BD_record.get(BirthLabels.CHANGED_SURNAME);
        String changed_forename = BD_record.get(BirthLabels.CHANGED_FORENAME);
        String changed_mothers_maiden_surname = BD_record.get(BirthLabels.CHANGED_MOTHERS_MAIDEN_SURNAME);

        String original_record_id = Integer.toString(BD_record.getId());
        String original_record_type = BD_record.get(BirthLabels.TYPE_LABEL);
        String role = "baby";
        String occupation = "";

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);
    }

    public static ILXP createFatherFromChildsBirthDeath( ILXP child, ILXP BD_record ) {

        if( child.get( PersonLabels.FATHERS_SURNAME ).equals( "" ) ) {
            return null;
        }

        String surname = child.get( PersonLabels.FATHERS_SURNAME );
        String forename = child.get(PersonLabels.FATHERS_FORENAME);
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
        String original_record_type = BD_record.get(BirthLabels.TYPE_LABEL);
        String role = "father";
        String occupation = child.get(PersonLabels.FATHERS_OCCUPATION);

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);
    }

    public static ILXP createMotherFromChildsBirthDeath(  ILXP child, ILXP BD_record ) {

        if( child.get( PersonLabels.FATHERS_SURNAME ).equals( "" ) ) {
            return null;
        }

        String surname = child.get( PersonLabels.MOTHERS_SURNAME );
        String forename = child.get(PersonLabels.MOTHERS_FORENAME);
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
        String original_record_type = BD_record.get(BirthLabels.TYPE_LABEL);
        String role = "mother";
        String occupation = child.get(PersonLabels.FATHERS_OCCUPATION);

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);

    }

    /**
     * Creates a Person record for the bride for a given marriage record
     * @param marriage_record  a record from which to extract person information
     * @return the LXP representing the bride
     */
    public static ILXP createBrideFromMarriageRecord(ILXP marriage_record) {

        String surname = marriage_record.get(MarriageLabels.BRIDE_SURNAME);
        String forename = marriage_record.get(MarriageLabels.BRIDE_FORENAME);
        String sex = "F"; //  this is the bride
        String fathers_forename = marriage_record.get(MarriageLabels.BRIDE_FATHERS_FORENAME);

        String fathers_surname = marriage_record.get(MarriageLabels.BRIDE_FATHERS_SURNAME);
        if( fathers_surname.equals("0") ) {
            fathers_surname = marriage_record.get(MarriageLabels.BRIDE_SURNAME); // TODO factor out?
        }

        String fathers_occupation = marriage_record.get(MarriageLabels.BRIDE_FATHER_OCCUPATION);
        String mothers_forename =  marriage_record.get(MarriageLabels.BRIDE_MOTHERS_FORENAME);
        String mothers_surname = marriage_record.get(MarriageLabels.BRIDE_FATHERS_SURNAME);   // Assumes the mother's surname is same as father's - OK??
        if( mothers_surname.equals("0") ) {
            mothers_surname =  marriage_record.get(MarriageLabels.SURNAME);
        }

        String mothers_maiden_surname = ""; // unknown??    // TODO check these
        String changed_surname = ""; // unknown
        String changed_forename = ""; // unknown
        String changed_mothers_maiden_surname = ""; // unknown

        String original_record_id = Integer.toString(marriage_record.getId());
        String original_record_type = marriage_record.get(MarriageLabels.TYPE_LABEL);
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
    public static ILXP createGroomFromMarriageRecord(ILXP marriage_record) {

        String surname = marriage_record.get(MarriageLabels.GROOM_SURNAME);
        String forename = marriage_record.get(MarriageLabels.GROOM_FORENAME);
        String sex = "F"; //  this is the bride
        String fathers_forename = marriage_record.get(MarriageLabels.GROOM_FATHERS_FORENAME);

        String fathers_surname = marriage_record.get(MarriageLabels.GROOM_FATHERS_SURNAME);
        if( fathers_surname.equals("0") ) {
            fathers_surname = marriage_record.get(MarriageLabels.GROOM_SURNAME); // TODO factor out?
        }

        String fathers_occupation = marriage_record.get(MarriageLabels.GROOM_FATHERS_OCCUPATION);
        String mothers_forename =  marriage_record.get(MarriageLabels.GROOM_MOTHERS_FORENAME);
        String mothers_surname = marriage_record.get(MarriageLabels.GROOM_FATHERS_SURNAME);
        if( mothers_surname.equals("0") ) {
            mothers_surname =  marriage_record.get(MarriageLabels.SURNAME);
        }

        String mothers_maiden_surname = ""; // unknown??    // TODO check these
        String changed_surname = ""; // unknown
        String changed_forename = ""; // unknown
        String changed_mothers_maiden_surname = ""; // unknown

        String original_record_id = Integer.toString(marriage_record.getId());
        String original_record_type = marriage_record.get(MarriageLabels.TYPE_LABEL);
        String role = "bride";
        String occupation = "";

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);

    }

    public static ILXP createBridesFatherFromMarriageRecord(ILXP bride, ILXP marriage_record) {// TODO write me

        String surname = marriage_record.get(MarriageLabels.BRIDE_FATHERS_SURNAME);
        if( surname.equals("0") ) {
            surname = marriage_record.get(MarriageLabels.BRIDE_SURNAME); // TODO factor out?
        }

        String forename = marriage_record.get(MarriageLabels.BRIDE_FATHERS_FORENAME);
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
        String original_record_type = marriage_record.get(MarriageLabels.TYPE_LABEL);
        String role = "brides_father";
        String occupation = marriage_record.get(MarriageLabels.BRIDE_FATHER_OCCUPATION);

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);

    }

    public static ILXP createBridesMotherFromMarriageRecord(ILXP bride, ILXP marriage_record) { // TODO write me

        String surname = marriage_record.get(MarriageLabels.BRIDE_MOTHERS_MAIDEN_SURNAME);  //<<<<<<<<<<<<< TODO see below
//        if( surname.equals("0") ) {
//            surname = marriage_record.get(MarriageLabels.BRIDE_SURNAME); // TODO factor out?
//        }

        String forename = marriage_record.get(MarriageLabels.BRIDE_MOTHERS_FORENAME);
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
        String original_record_type = marriage_record.get(MarriageLabels.TYPE_LABEL);
        String role = "brides_mother";
        String occupation = ""; // unknown

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);  // TODO Person should also have maiden name???

    }

    public static ILXP createGroomsFatherFromMarriageRecord(ILXP groom, ILXP marriage_record) {// TODO write me

        String surname = marriage_record.get(MarriageLabels.GROOM_FATHERS_SURNAME);
        if( surname.equals("0") ) {
            surname = marriage_record.get(MarriageLabels.GROOM_SURNAME); // TODO factor out?
        }

        String forename = marriage_record.get(MarriageLabels.GROOM_FATHERS_FORENAME);
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
        String original_record_type = marriage_record.get(MarriageLabels.TYPE_LABEL);
        String role = "grooms_father";
        String occupation = marriage_record.get(MarriageLabels.GROOM_FATHERS_OCCUPATION);

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);


    }

    public static ILXP createGroomsMotherFromMarriageRecord(ILXP groom, ILXP marriage_record) {// TODO write me

        String surname = marriage_record.get(MarriageLabels.GROOM_MOTHERS_MAIDEN_SURNAME);  //<<<<<<<<<<<<< TODO see below
//        if( surname.equals("0") ) {
//            surname = marriage_record.get(MarriageLabels.GROOM_SURNAME); // TODO factor out?
//        }

        String forename = marriage_record.get(MarriageLabels.GROOM_MOTHERS_FORENAME);
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
        String original_record_type = marriage_record.get(MarriageLabels.TYPE_LABEL);
        String role = "grooms_mother";
        String occupation = ""; // unknown

        return createPerson( surname, forename,  sex, fathers_forename, fathers_surname,
                fathers_occupation, mothers_forename, mothers_surname, mothers_maiden_surname,
                changed_surname,  changed_forename,  changed_mothers_maiden_surname, original_record_id, original_record_type,  role, occupation);  // TODO Person should also have maiden name???

    }


}
