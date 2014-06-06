package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.Repository;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.EventImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.*;

import java.io.IOException;


/**
 * Performs pairwise linkage on babies and fathers
 * <p/>
 * Created by al on 11/05/2014.
 */
public class BassJohnBass {

    private static String input_repo_path = "src/test/resources/BDM_repo";          // input repository containing event records
    private static String linkage_repo_path = "src/test/resources/linkage_repo";    // repository for linked records
    private static String blocked_people_repo_path = "src/test/resources/blocked_people_repo";    // repository for blocked records

    private static String source_base_path = "src/test/resources/BDMSet1";          // Path to source of vital event records in Digitising Scotland format
    private static String births_name = "birth_records";                            // Name of bucket containing birth records (inputs).
    private static String people_name = "people";                                   // Name of bucket containing maximal people extracted from birth records
    private static String relationships_name = "relationships";                     // Name of bucket containing relationships between people extracted from birth records
    private static String identities_name = "identity";                             // Name of bucket containing equivalent identities of people

    private static String births_source_path = source_base_path + "/" + births_name + ".txt";

    private final IRepository input_repo;
    private final IRepository linkage_repo;
    private final IRepository blocked_people_repo;

    // input buckets containing BDM records in LXP format

    private IBucket births;                     // Bucket containing birth records (inputs).
    private IBucket people;              // Bucket containing people extracted from birth records
    private IBucket relationships;       // Bucket containing relationships between people
    private IIndexedBucket identity;            // Bucket containing identities of equivalent people in records
    private int id = 0;

    public BassJohnBass() throws RepositoryException, RecordFormatException, JSONException, IOException {

        input_repo = new Repository(input_repo_path);
        linkage_repo = new Repository(linkage_repo_path);
        blocked_people_repo = new Repository(blocked_people_repo_path);

        births = input_repo.makeBucket(births_name);

        people = linkage_repo.makeBucket(people_name); // linkage_repo.makeIndexedBucket(people_name);

        relationships = linkage_repo.makeBucket(relationships_name); // linkage_repo.makeIndexedBucket(relationships_name);

        identity = linkage_repo.makeIndexedBucket(identities_name);
        identity.addIndex(SameAsLabels.record_id1);

        // import the birth records
        EventImporter importer = new EventImporter();
        importer.importBirths(births, births_source_path);

        // populate the people and relationships bucket
        populateMaximalPeople();

        try {
            BlockedMaximalPersonResolver r = new BlockedMaximalPersonResolver(people, blocked_people_repo, identity);
            r.match();

        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the maximal_people_repo with 1 person from each birth record.
     * For each birth record there will be 1 person created - mother, father baby
     */
    private void populateMaximalPeople() {

        ILXPOutputStream people_stream = people.getOutputStream();
        ILXPOutputStream relationships_stream = relationships.getOutputStream();

        ILXPInputStream stream = births.getInputStream();
        for (ILXP birth_record : stream) {

            int baby_id = addBabyToOutput(birth_record, people_stream);
            int dad_id = addFatherToOutput(birth_record, people_stream);
            int mum_id = addMotherToOutput(birth_record, people_stream);

            addBMF(birth_record, baby_id, dad_id, mum_id, relationships_stream);
        }
    }

    /**
     * Adds the baby-mother-father relationships to the relationships bucket
     *
     * @param birth_record
     * @param child_id
     * @param dad_id
     * @param mum_id
     * @param relationships_stream
     */
    private void addBMF(ILXP birth_record, int child_id, int dad_id, int mum_id, ILXPOutputStream relationships_stream) {

        ILXP is_father = new LXP(getNextId());
        is_father.put("TYPE", FatherOfLabels.TYPE);
        is_father.put(FatherOfLabels.child_id, child_id);
        is_father.put(FatherOfLabels.father_id, dad_id);
        is_father.put(FatherOfLabels.birth_record_id, birth_record.getId());
        relationships_stream.add(is_father);

        ILXP is_mother = new LXP(getNextId());
        is_mother.put("TYPE", MotherOfLabels.TYPE);
        is_mother.put(MotherOfLabels.child_id, child_id);
        is_mother.put(MotherOfLabels.mother_id, mum_id);
        is_mother.put(MotherOfLabels.birth_record_id, birth_record.getId());
        relationships_stream.add(is_mother);

        // Could add is_child but not now...
    }

    /**
     * @param birth_record  a record from which to extract baby information and add to the stream
     * @param people_stream a stream to which to add a new Person record
     * @return the id of the baby in the birth record
     */
    private int addBabyToOutput(ILXP birth_record, ILXPOutputStream people_stream) {

        int person_id = getNextId();
        ILXP person = new LXP(person_id);

        person.put("TYPE", PersonLabels.TYPE);

        person.put(PersonLabels.ORIGINAL_RECORD_ID, birth_record.getId());
        person.put(PersonLabels.ORIGINAL_RECORD_TYPE, birth_record.get(BirthLabels.TYPE_LABEL));  //<<<<<<<<<<<<<<<<<< Problem....
        person.put(PersonLabels.ROLE, "baby");

        person.put(PersonLabels.SURNAME, birth_record.get(BirthLabels.SURNAME));
        person.put(PersonLabels.FORENAME, birth_record.get(BirthLabels.FORENAME));
        person.put(PersonLabels.SURNAME, birth_record.get(BirthLabels.SURNAME));
        person.put(PersonLabels.SEX, birth_record.get(BirthLabels.SEX));
        person.put(PersonLabels.FATHERS_FORENAME, birth_record.get(BirthLabels.FATHERS_FORENAME));

        String fathersurname = birth_record.get(BirthLabels.FATHERS_SURNAME);

        if( fathersurname.equals("0") ) {
            person.put(PersonLabels.FATHERS_SURNAME, birth_record.get(BirthLabels.SURNAME));
        } else {
            person.put(PersonLabels.FATHERS_SURNAME,fathersurname );
        }

        person.put(PersonLabels.FATHERS_OCCUPATION, birth_record.get(BirthLabels.FATHERS_OCCUPATION));
        person.put(PersonLabels.MOTHERS_FORENAME, birth_record.get(BirthLabels.MOTHERS_FORENAME));
        person.put(PersonLabels.MOTHERS_SURNAME, birth_record.get(BirthLabels.MOTHERS_SURNAME));

        String mothersurname = birth_record.get(BirthLabels.MOTHERS_SURNAME);

        if( mothersurname.equals("0") ) {
            person.put(PersonLabels.MOTHERS_SURNAME, birth_record.get(BirthLabels.SURNAME));
        } else {
            person.put(PersonLabels.MOTHERS_SURNAME,mothersurname );
        }

        person.put(PersonLabels.MOTHERS_MAIDEN_SURNAME, birth_record.get(BirthLabels.MOTHERS_MAIDEN_SURNAME));
        person.put(PersonLabels.CHANGED_SURNAME, birth_record.get(BirthLabels.CHANGED_SURNAME));
        person.put(PersonLabels.CHANGED_FORENAME, birth_record.get(BirthLabels.CHANGED_FORENAME));
        person.put(PersonLabels.CHANGED_MOTHERS_MAIDEN_SURNAME, birth_record.get(BirthLabels.CHANGED_MOTHERS_MAIDEN_SURNAME));

        people_stream.add(person);
        return person_id;
    }

    /**
     * @param birth_record  a record from which to extract father information and add to the stream
     * @param people_stream a stream to which to add a new Person record
     * @return the id of the father in the birth record
     */
    private int addFatherToOutput(ILXP birth_record, ILXPOutputStream people_stream) {

        int banana = 3;
        String foo = "foo";

        int person_id = getNextId();
        ILXP person = new LXP(person_id);

        person.put("TYPE", PersonLabels.TYPE);

        person.put(PersonLabels.ORIGINAL_RECORD_ID, birth_record.getId());
        person.put(PersonLabels.ORIGINAL_RECORD_TYPE, birth_record.get(BirthLabels.TYPE_LABEL));
        person.put(PersonLabels.ROLE, "father");

        String fathersurname = birth_record.get(BirthLabels.FATHERS_SURNAME);

        if( fathersurname.equals("0") ) {
            person.put(PersonLabels.SURNAME, birth_record.get(BirthLabels.SURNAME));
        } else {
            person.put(PersonLabels.SURNAME,fathersurname );
        }

        person.put(PersonLabels.FORENAME, birth_record.get(BirthLabels.FATHERS_FORENAME));
        person.put(PersonLabels.OCCUPATION, birth_record.get(BirthLabels.FATHERS_OCCUPATION));
        person.put(PersonLabels.SEX, "M");

        people_stream.add(person);
        return person_id;
    }

    /**
     * @param birth_record  a record from which to extract mother information and add to the stream
     * @param people_stream a stream to which to add a new Person record
     * @return the id of the mother in the birth record
     */
    private int addMotherToOutput(ILXP birth_record, ILXPOutputStream people_stream) {

        int person_id = getNextId();
        ILXP person = new LXP(person_id);

        person.put("TYPE", PersonLabels.TYPE);

        person.put(PersonLabels.ORIGINAL_RECORD_ID, birth_record.getId());
        person.put(PersonLabels.ORIGINAL_RECORD_TYPE, birth_record.get(BirthLabels.TYPE_LABEL));
        person.put(PersonLabels.ROLE, "mother");

        String mothersurname = birth_record.get(BirthLabels.MOTHERS_SURNAME);

        if( mothersurname.equals("0") ) {
            person.put(PersonLabels.SURNAME, birth_record.get(BirthLabels.SURNAME));
        } else {
            person.put(PersonLabels.SURNAME,mothersurname );
        }

        person.put(PersonLabels.FORENAME, birth_record.get(BirthLabels.FORENAME));
        person.put(PersonLabels.SEX, "F");

        people_stream.add(person);
        return person_id;
    }

    private int getNextId() {
        return id++;
    }

    /**
     * **************************************************************************************************************
     */

    public static void main(String[] args) throws Exception {

        new BassJohnBass();
    }
}
