package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.*;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.EventImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.BlockingFirstLastSexOverPerson;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.visualise.IndexedBucketVisualiser;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;

import java.io.IOException;
import java.util.Iterator;


/**
 * Performs pairwise linkage on babies and fathers
 * <p/>
 * Created by al on 11/05/2014.
 */
public class BassJohnBass {

    private static String store_path = "src/test/resources/STORE";

    private static String input_repo_name = "BDM_repo";          // input repository containing event records
    private static String linkage_repo_name = "linkage_repo";    // repository for linked records
    private static String blocked_people_repo_name = "blocked_people_repo";    // repository for blocked records

    private static String source_base_path = "src/test/resources/BDMSet1";          // Path to source of vital event records in Digitising Scotland format
    private static String births_name = "birth_records";                            // Name of bucket containing birth records (inputs).
    private static String marriages_name = "marriage_records";                      // Name of bucket containing marriage records (inputs).
    private static String people_name = "people";                                   // Name of bucket containing maximal people extracted from birth records
    private static String relationships_name = "relationships";                     // Name of bucket containing relationships between people extracted from birth records
    private static String identities_name = "identity";                             // Name of bucket containing equivalent identities of people
    private static String lineage_name = "lineage";                                 // Name of bucket of pais of (mother/father- baby links).


    private static String births_source_path = source_base_path + "/" + births_name + ".txt";
    private static String marriages_source_path = source_base_path + "/" + marriages_name + ".txt";

    private final IRepository input_repo;
    private final IRepository linkage_repo;
    private final IRepository blocked_people_repo;
    private final Store store
            ;

    // input buckets containing BDM records in LXP format

    private IBucket births;                     // Bucket containing birth records (inputs).
    private IBucket marriages;                     // Bucket containing marriage records (inputs).
    private IBucket people;              // Bucket containing people extracted from birth records
    private IBucket relationships;       // Bucket containing relationships between people
    private IIndexedBucket identity;            // Bucket containing identities of equivalent people in records
    private IIndexedBucket lineage;            // Bucket containing pairs of pontentially linked parents and children

    private int id = 0;

    public BassJohnBass() throws RepositoryException, RecordFormatException, JSONException, IOException, PersistentObjectException, StoreException {

        store = new Store(store_path);

        input_repo = store.makeRepository(input_repo_name);
        linkage_repo = store.makeRepository(linkage_repo_name);
        blocked_people_repo = store.makeRepository(blocked_people_repo_name);

        births = input_repo.makeBucket(births_name);
        marriages = input_repo.makeBucket(marriages_name);

        people = linkage_repo.makeBucket(people_name); // linkage_repo.makeIndexedBucket(people_name);

        relationships = linkage_repo.makeBucket(relationships_name); // linkage_repo.makeIndexedBucket(relationships_name);

        lineage = linkage_repo.makeIndexedBucket(lineage_name);
        lineage.addIndex(SameAsLabels.first);

        // import the birth,death, marriage records
        EventImporter importer = new EventImporter();
        importer.importBirths(births, births_source_path);
        importer.importMarriages(marriages, marriages_source_path);

        createPeopleAndRelationshipsFromBirths();

        try {

            IBlocker blocker = new BlockingFirstLastSexOverPerson( people, blocked_people_repo );
            blocker.apply();
            pairwiseLinkBlockedRecords(blocked_people_repo, lineage );

        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        System.out.println("Identity table:");
        IndexedBucketVisualiser v = new IndexedBucketVisualiser( lineage, people );
        v.show();
    }

    private void pairwiseLinkBlockedRecords( IRepository from, IBucket to ) {

        Iterator<IBucket> blocked_people_iterator = from.getIterator();


        while (blocked_people_iterator.hasNext()) {
            IBucket blocked_records = blocked_people_iterator.next();

            // Iterating over buckets of people with same first and last name and the same sex.


            BirthBirthLinker bdl = new BirthBirthLinker(blocked_records.getInputStream(), to.getOutputStream());
            bdl.pairwiseLink();
        }
    }

    /**
     * Populates the maximal_people_repo with 1 person from each birth record.
     * For each birth record there will be 1 person created - mother, father baby
     */
    private void createPeopleAndRelationshipsFromBirths() {

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

        if( dad_id != -1 ) { // no father
            ILXP is_father = new LXP();
            is_father.put("TYPE", FatherOfLabels.TYPE);
            is_father.put(FatherOfLabels.child_id, Integer.toString(child_id));
            is_father.put(FatherOfLabels.father_id, Integer.toString(dad_id));
            is_father.put(FatherOfLabels.birth_record_id, Integer.toString(birth_record.getId()));
            relationships_stream.add(is_father);
        }

        if( mum_id != -1 ) {
            ILXP is_mother = new LXP();
            is_mother.put("TYPE", MotherOfLabels.TYPE);
            is_mother.put(MotherOfLabels.child_id, Integer.toString(child_id));
            is_mother.put(MotherOfLabels.mother_id, Integer.toString(mum_id));
            is_mother.put(MotherOfLabels.birth_record_id, Integer.toString(birth_record.getId()));
            relationships_stream.add(is_mother);
        }

        // Could add is_child but not now...
    }

    /**
     * @param birth_record  a record from which to extract baby information and add to the stream
     * @param people_stream a stream to which to add a new Person record
     * @return the id of the baby in the birth record
     */
    private int addBabyToOutput(ILXP birth_record, ILXPOutputStream people_stream) {

        ILXP person = new LXP();

        person.put("TYPE", PersonLabels.TYPE);

        person.put(PersonLabels.ORIGINAL_RECORD_ID, Integer.toString(birth_record.getId()));
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
        return person.getId();
    }

    /**
     * @param birth_record  a record from which to extract father information and add to the stream
     * @param people_stream a stream to which to add a new Person record
     * @return the id of the father in the birth record or -1 if there is no father record.
     */
    private int addFatherToOutput(ILXP birth_record, ILXPOutputStream people_stream) {

        String fathersurname = birth_record.get(BirthLabels.FATHERS_SURNAME);

        if( fathersurname.equals("") ) { // no father in record - do not continue with process.
            return -1;
        }

        ILXP person = new LXP();

        person.put("TYPE", PersonLabels.TYPE);

        person.put(PersonLabels.ORIGINAL_RECORD_ID, Integer.toString(birth_record.getId()));
        person.put(PersonLabels.ORIGINAL_RECORD_TYPE, birth_record.get(BirthLabels.TYPE_LABEL));
        person.put(PersonLabels.ROLE, "father");

        if( fathersurname.equals("0") ) {
            person.put(PersonLabels.SURNAME, birth_record.get(BirthLabels.SURNAME));
        } else {
            person.put(PersonLabels.SURNAME,fathersurname );
        }

        person.put(PersonLabels.FORENAME, birth_record.get(BirthLabels.FATHERS_FORENAME));
        person.put(PersonLabels.OCCUPATION, birth_record.get(BirthLabels.FATHERS_OCCUPATION));
        person.put(PersonLabels.SEX, "M");

        people_stream.add(person);
        return person.getId();
    }

    /**
     * @param birth_record  a record from which to extract mother information and add to the stream
     * @param people_stream a stream to which to add a new Person record
     * @return the id of the mother in the birth record or -1 if there is no mother record.
     */
    private int addMotherToOutput(ILXP birth_record, ILXPOutputStream people_stream) {

        String mothersurname = birth_record.get(BirthLabels.MOTHERS_SURNAME);

        if( mothersurname.equals( "" ) ) { // no mother in record - do not continue with process.
            return -1;
        }

        ILXP person = new LXP();

        person.put("TYPE", PersonLabels.TYPE);

        person.put(PersonLabels.ORIGINAL_RECORD_ID, Integer.toString(birth_record.getId()));
        person.put(PersonLabels.ORIGINAL_RECORD_TYPE, birth_record.get(BirthLabels.TYPE_LABEL));
        person.put(PersonLabels.ROLE, "mother");
        person.put(PersonLabels.FORENAME, birth_record.get(BirthLabels.MOTHERS_FORENAME));
        person.put(PersonLabels.MOTHERS_MAIDEN_SURNAME,  birth_record.get(BirthLabels.MOTHERS_MAIDEN_SURNAME) );
        person.put(PersonLabels.SEX, "F");

        if( mothersurname.equals("0") ) {
            person.put(PersonLabels.SURNAME, birth_record.get(BirthLabels.SURNAME));
        } else {
            person.put(PersonLabels.SURNAME,mothersurname );
        }

        people_stream.add(person);
        return person.getId();
    }

    /**
     * **************************************************************************************************************
     */

    public static void main(String[] args) throws Exception {

        new BassJohnBass();
    }
}
