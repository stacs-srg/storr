package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.StoreException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.EventImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.MultipleBlockerOverPerson;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.visualise.IndexedBucketVisualiser;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;

import java.io.IOException;
import java.util.Iterator;


/**
 * Attempt to create a linking framework
 * Created by al on 6/8/2014.
 */
public class AlLinker {

    // Repositories and stores

    private static String store_path = "src/test/resources/STORE";
    private static String input_repo_name = "BDM_repo";                         // input repository containing event records
    private static String linkage_repo_name = "linkage_repo";                   // repository for linked records
    private static String blocked_people_repo_name = "blocked_people_repo";     // repository for blocked records

    private Store store;
    private IRepository input_repo;             // Repository containing buckets of BDM records
    private IRepository linkage_repo;
    private IRepository blocked_people_repo;

    // Bucket declarations

    private IBucket births;                     // Bucket containing birth records (inputs).
    private IBucket marriages;                  // Bucket containing marriage records (inputs).
    private IBucket deaths;                     // Bucket containing death records (inputs).
    private IBucket people;                     // Bucket containing people extracted from birth records
    private IBucket relationships;              // Bucket containing relationships between people
    private IIndexedBucket lineage;             // Bucket containing pairs of potentially linked parents and child_ids

    // Paths to sources

    private static String source_base_path = "src/test/resources/BDMSet1";          // Path to source of vital event records in Digitising Scotland format

    private static String births_name = "birth_records";                            // Name of bucket & input file containing birth records (inputs).
    private static String marriages_name = "marriage_records";                      // Name of bucket & input file containing marriage records (inputs).
    private static String deaths_name = "death_records";                            // Name of bucket & input file containing marriage records (inputs).
    private static String births_source_path = source_base_path + "/" + births_name + ".txt";
    private static String marriages_source_path = source_base_path + "/" + marriages_name + ".txt";
    private static String deaths_source_path = source_base_path + "/" + deaths_name + ".txt";

    // Names of buckets

    private static String people_name = "people";                                   // Name of bucket containing maximal people extracted from birth records
    private static String relationships_name = "relationships";                     // Name of bucket containing relationships between people
    private static String lineage_name = "lineage";


    public AlLinker() throws RepositoryException, RecordFormatException, JSONException, IOException, PersistentObjectException, StoreException {

        initialise();
        injestBDMRecords();
        block();
        link();

        System.out.println("Identity table:");
        IndexedBucketVisualiser v = new IndexedBucketVisualiser( lineage, people );
        v.show();
    }

    private void initialise() throws StoreException, IOException, RepositoryException, RecordFormatException, JSONException {
        store = new Store(store_path);

        input_repo = store.makeRepository(input_repo_name);
        linkage_repo = store.makeRepository(linkage_repo_name);
        blocked_people_repo = store.makeRepository(blocked_people_repo_name);  // a repo of Buckets of records blocked by  first name, last name, sex

        births = input_repo.makeBucket(births_name);
        marriages = input_repo.makeBucket(marriages_name);

        people = linkage_repo.makeBucket(people_name); // linkage_repo.makeIndexedBucket(people_name);

        relationships = linkage_repo.makeBucket(relationships_name); // linkage_repo.makeIndexedBucket(relationships_name);

        lineage = linkage_repo.makeIndexedBucket(lineage_name);  // a bucket of Pairs of ids of records for people with the same first name, last name, sex, indexed by first id.
        lineage.addIndex(SameAsLabels.first);
    }

    /**
     *  Import the birth,death, marriage records
     *  Initialises the people bucket with the people injected - one record for each person referenced in the original record
     *  Initialises the known(100% certain) relationships between people and stores the relationships in the relationships bucket
     */
    private void injestBDMRecords() throws RecordFormatException, JSONException, IOException {

        EventImporter importer = new EventImporter();
        importer.importBirths(births, births_source_path);
        importer.importMarriages(marriages, marriages_source_path);
        importer.importDeaths(deaths, deaths_source_path);

        createPeopleAndRelationshipsFromBirths();
        createPeopleAndRelationshipsFromMarriages();
        createPeopleAndRelationshipsFromDeaths();
    }

    private void block() {
        try {

            IBlocker blocker = new MultipleBlockerOverPerson( people, blocked_people_repo );
            blocker.apply();

        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    private void link() {

            pairwiseLinkBlockedRecords(blocked_people_repo, lineage );
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
     * Populates the people bucket with 1 person from each birth record.
     * For each birth record there will be 1 person created - mother, father baby
     * Thus the people bucket will contain multiple copies of a person - one instance per record that they appear in.
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

    private void createPeopleAndRelationshipsFromMarriages() {
        //TODO Al is here
    }

    private void createPeopleAndRelationshipsFromDeaths() {
        //TODO Al is here
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
     * Populates the people bucket with 1 baby for a given birth record.
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
     * Populates the people bucket with 1 father for a given birth record.
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
     * Populates the people bucket with 1 mother for a given birth record.
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

        new AlLinker();
    }
}
