package uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.digitising_scotland.jstore.types.LXPBaseType;
import uk.ac.standrews.cs.digitising_scotland.jstore.types.LXP_SCALAR;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 03/10/2014.
 */
public class Death extends AbstractLXP {

    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String DEATH_YEAR = "death_year";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String SEX = "sex";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CHANGED_SURNAME = "changed_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String SURNAME = "surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String IMAGE_QUALITY = "image_quality";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String COD_A = "cod_a";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String COD_B = "cod_b";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String COD_C = "cod_c";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String AGE_AT_DEATH = "age_at_death";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CHANGED_DEATH_AGE = "changed_death_age";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String DEATH_MONTH = "death_month";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FATHERS_SURNAME = "fathers_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FATHERS_OCCUPATION = "fathers_occupation";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String SPOUSES_NAMES = "spouses_names";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String DEATH_PLACE = "death_place";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FATHER_DECEASED = "father_deceased";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CERTIFYING_DOCTOR = "certifying_doctor";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String REGISTRATION_DISTRICT_SUFFIX = "REGISTRATION_DISTRICT_SUFFIX";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String ENTRY = "ENTRY";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MOTHERS_SURNAME = "mothers_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MARITAL_STATUS = "marital_status";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String TYPE = "TYPE";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String SPOUSES_OCCUPATIONS = "spouses_occupations";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String OCCUPATION = "occupation";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FORENAME = "forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String BIRTH_DATE = "birth_date";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String REGISTRATION_DISTRICT_NUMBER = "REGISTRATION_DISTRICT_NUMBER";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MOTHERS_MAIDEN_SURNAME = "mothers_maiden_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MOTHER_DECEASED = "mother_deceased";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CORRECTED_ENTRY = "corrected_entry";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CHANGED_MOTHERS_MAIDEN_SURNAME = "changed_mothers_maiden_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String CHANGED_FORENAME = "changed_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String MOTHERS_FORENAME = "mothers_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String DEATH_DAY = "death_day";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String YEAR_OF_REGISTRATION = "YEAR_OF_REGISTRATION";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    public static final String FATHERS_FORENAME = "fathers_forename";

    public Death() {
        super();
    }

    public Death(long label_id, JSONReader reader) throws PersistentObjectException, IllegalKeyException {

        super(Store.getInstance().getNextFreePID(), reader);

    }


}

// When these types were encoded as JSON and read in this was the definition from the file deathType.jsn
//{"death_year":"string",
//        "sex":"string",
//        "changed_surname":"string",
//        "surname":"string",
//        "image_quality":"string",
//        "cod_a":"string",
//        "cod_b":"string",
//        "cod_c":"string",
//        "age_at_death":"string",
//        "changed_death_age":"string",
//        "death_month":"string",
//        "fathers_surname":"string",
//        "fathers_occupation":"string",
//        "spouses_names":"string",
//        "death_place":"string",
//        "father_deceased":"string",
//        "certifying_doctor":"string",
//        "REGISTRATION_DISTRICT_SUFFIX":"string",
//        "ENTRY":"string",
//        "mothers_surname":"string",
//        "marital_status":"string",
//        "TYPE":"string",
//        "spouses_occupations":"string",
//        "occupation":"string",
//        "forename":"string",
//        "birth_date":"string",
//        "REGISTRATION_DISTRICT_NUMBER":"string",
//        "mothers_maiden_surname":"string",
//        "mother_deceased":"string",
//        "corrected_entry":"string",
//        "changed_mothers_maiden_surname":"string",
//        "changed_forename":"string",
//        "mothers_forename":"string",
//        "death_day":"string",
//        "YEAR_OF_REGISTRATION":"string",
//        "fathers_forename":"string"
//        }