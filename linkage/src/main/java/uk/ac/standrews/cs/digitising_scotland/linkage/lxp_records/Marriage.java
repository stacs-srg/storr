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
public class Marriage extends AbstractLXP {

    @LXP_SCALAR(type = LXPBaseType.STRING)
    String groom_mothers_maiden_surname = "groom_mothers_maiden_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String groom_surname = "groom_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String GROOM_OCCUPATION = "groom_occupation";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String BRIDE_FATHER_OCCUPATION = "bride_father_occupation";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String GROOM_FATHERS_FORENAME = "groom_fathers_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String CHANGED_GROOM_FORENAME = "changed_groom_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String IMAGE_QUALITY = "image_quality";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String BRIDE_FATHERS_FORENAME = "bride_fathers_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String BRIDE_ADDRESS = "bride_address";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String BRIDE_MOTHERS_MAIDEN_SURNAME = "bride_mothers_maiden_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String GROOM_FATHERS_OCCUPATION = "groom_fathers_occupation";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String ENTRY = "ENTRY";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String GROOM_ADDRESS = "groom_address";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String MARRIAGE_MONTH = "marriage_month";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String MARRIAGE_YEAR = "marriage_year";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String GROOM_DID_NOT_SIGN = "groom_did_not_sign";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String BRIDE_MARITAL_STATUS = "bride_marital_status";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String DENOMINATION = "denomination";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String BRIDE_FATHER_DECEASED = "bride_father_deceased";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String GROOM_FORENAME = "groom_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String BRIDE_OCCUPATION = "bride_occupation";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String CHANGED_GROOM_SURNAME = "changed_groom_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String BRIDE_DID_NOT_SIGN = "bride_did_not_sign";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String BRIDE_MOTHERS_FORENAME = "bride_mothers_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String BRIDE_MOTHER_DECEASED = "bride_mother_deceased";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String GROOM_MOTHERS_FORENAME = "groom_mothers_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String GROOM_MOTHER_DECEASED = "groom_mother_deceased";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String REGISTRATION_DISTRICT_NUMBER = "REGISTRATION_DISTRICT_NUMBER";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String BRIDE_SURNAME = "bride_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String GROOM_MARITAL_STATUS = "groom_marital_status";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String BRIDE_FATHERS_SURNAME = "bride_fathers_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String BRIDE_AGE_OR_DATE_OF_BIRTH = "bride_age_or_date_of_birth";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String CHANGED_BRIDE_SURNAME = "changed_bride_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String CORRECTED_ENTRY = "corrected_entry";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String CHANGED_BRIDE_FORENAME = "changed_bride_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String BRIDE_FORENAME = "bride_forename";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String GROOM_FATHERS_SURNAME = "groom_fathers_surname";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String GROOM_FATHER_DECEASED = "groom_father_deceased";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String GROOM_AGE_OR_DATE_OF_BIRTH = "groom_age_or_date_of_birth";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String YEAR_OF_REGISTRATION = "YEAR_OF_REGISTRATION";
    @LXP_SCALAR(type = LXPBaseType.STRING)
    String MARRIAGE_DAY = "marriage_day";

    public Marriage() {
        super();
    }

    public Marriage(long label_id, JSONReader reader) throws PersistentObjectException, IllegalKeyException {

        super(Store.getInstance().getNextFreePID(), reader);
    }

}

// When these types were encoded as JSON and read in this was the definition from the file marriageType.jsn
//{"groom_mothers_maiden_surname":"string",
//        "groom_surname":"string",
//        "groom_occupation":"string",
//        "bride_father_occupation":"string",
//        "groom_fathers_forename":"string",
//        "changed_groom_forename":"string",
//        "image_quality":"string",
//        "bride_fathers_forename":"string",
//        "bride_address":"string",
//        "bride_mothers_maiden_surname":"string",
//        "groom_fathers_occupation":"string",
//        "ENTRY":"string",
//        "groom_address":"string",
//        "marriage_month":"string",
//        "marriage_year":"string",
//        "groom_did_not_sign":"string",
//        "bride_marital_status":"string",
//        "denomination":"string",
//        "bride_father_deceased":"string",
//        "groom_forename":"string",
//        "bride_occupation":"string",
//        "changed_groom_surname":"string",
//        "bride_did_not_sign":"string",
//        "bride_mothers_forename":"string",
//        "bride_mother_deceased":"string",
//        "groom_mothers_forename":"string",
//        "groom_mother_deceased":"string",
//        "REGISTRATION_DISTRICT_NUMBER":"string",
//        "bride_surname":"string",
//        "groom_marital_status":"string",
//        "bride_fathers_surname":"string",
//        "bride_age_or_date_of_birth":"string",
//        "changed_bride_surname":"string",
//        "corrected_entry":"string",
//        "changed_bride_forename":"string",
//        "bride_forename":"string",
//        "groom_fathers_surname":"string",
//        "groom_father_deceased":"string",
//        "groom_age_or_date_of_birth":"string",
//        "YEAR_OF_REGISTRATION":"string",
//        "marriage_day":"string"}
