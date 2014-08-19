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
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.FatherOfLabels;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.MotherOfLabels;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.SameAsLabels;
import uk.ac.standrews.cs.digitising_scotland.linkage.visualise.IndexedBucketVisualiser;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;

import java.io.IOException;
import java.util.Iterator;

import static uk.ac.standrews.cs.digitising_scotland.linkage.record_utilities.LXPConstructors.*;


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
        deaths = input_repo.makeBucket(deaths_name);
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

        createPeopleAndRelationshipsFromBirthsOrDeaths( births );
        createPeopleAndRelationshipsFromBirthsOrDeaths( deaths );
        createPeopleAndRelationshipsFromMarriages( marriages );
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

        Iterator<IBucket> blocked_people_iterator = blocked_people_repo.getIterator();

        while (blocked_people_iterator.hasNext()) {
            IBucket blocked_records = blocked_people_iterator.next();

            // Iterating over buckets of people with same first and last name and the same sex.

            BirthBirthLinker bdl = new BirthBirthLinker(blocked_records.getInputStream(), lineage.getOutputStream());
            bdl.pairwiseLink();
        }
    }



    /**
     * This method populates the people bucket with 1 person from each birth or marriage record (the relevant field names are the same in each)
     * For each record there will be 1 person created - e.g. mother, father baby
     * Thus the people bucket will contain multiple copies of a person - one instance per record that they appear in.
     * @param bucket - the bucket from which to take the inputs records
     */
    private void createPeopleAndRelationshipsFromBirthsOrDeaths( IBucket bucket ) {

        ILXPOutputStream people_stream = people.getOutputStream();
        ILXPOutputStream relationships_stream = relationships.getOutputStream();

        ILXPInputStream stream = bucket.getInputStream();
        for (ILXP birth_record : stream) {

            // add the people

      //      Person baby = createBaby(new Birth(birth_record), people_stream);

            /* Person */ ILXP baby = createPersonFromOwnBirthDeath(birth_record);
            people_stream.add(baby);
            /* Person */ ILXP dad = createFatherFromChildsBirthDeath(baby, birth_record);
            if( dad != null ) {
                people_stream.add(dad);
                addRelationship(birth_record, dad, baby, FatherOfLabels.TYPE, relationships_stream);
            }
            /* Person */ ILXP mum = createMotherFromChildsBirthDeath(baby, birth_record);
            if( mum != null ) {
                people_stream.add(mum);
                addRelationship(birth_record, dad, baby, MotherOfLabels.TYPE, relationships_stream);
            }

            // Could add is_child but not now...
        }
    }

    private void createPeopleAndRelationshipsFromMarriages( IBucket bucket ) {

        ILXPOutputStream people_stream = people.getOutputStream();
        ILXPOutputStream relationships_stream = relationships.getOutputStream();

        ILXPInputStream stream = bucket.getInputStream();
        for (ILXP marriage_record : stream) {

            // add the people

            /* Person */ ILXP bride = createBrideFromMarriageRecord(marriage_record);
            people_stream.add(bride);
            /* Person */ ILXP groom = createGroomFromMarriageRecord(marriage_record);
            people_stream.add(groom);

            /* Person */ ILXP grooms_mother = createGroomsMotherFromMarriageRecord(groom, marriage_record);
            people_stream.add(grooms_mother);
            /* Person */ ILXP grooms_father = createGroomsFatherFromMarriageRecord(groom, marriage_record);
            people_stream.add(grooms_father);
             /* Person */ ILXP brides_mother = createBridesMotherFromMarriageRecord(bride, marriage_record);
            people_stream.add(brides_mother);
             /* Person */ ILXP brides_father = createBridesFatherFromMarriageRecord(bride, marriage_record);
            people_stream.add(brides_father);

            // add the relationships

            addRelationship(marriage_record, grooms_father, groom, FatherOfLabels.TYPE, relationships_stream);
            addRelationship(marriage_record, grooms_mother, groom, MotherOfLabels.TYPE, relationships_stream);
            addRelationship(marriage_record, brides_father, bride, FatherOfLabels.TYPE, relationships_stream);
            addRelationship(marriage_record, brides_mother, bride, MotherOfLabels.TYPE, relationships_stream);
        }
    }


    private void addRelationship(ILXP original_record, ILXP record1, ILXP record2, String relationship_type, ILXPOutputStream output ) {

        if (record1 != null) {
            ILXP is_mother = new LXP();
            is_mother.put("TYPE", relationship_type);
            is_mother.put(MotherOfLabels.child_id, Integer.toString(record2.getId()));
            is_mother.put(MotherOfLabels.mother_id, Integer.toString(record1.getId()));
            is_mother.put(MotherOfLabels.birth_record_id, Integer.toString(original_record.getId()));
            output.add(is_mother);
        }
    }

    /**
     * **************************************************************************************************************
     */

    public static void main(String[] args) throws Exception {

        new AlLinker();
    }
}
