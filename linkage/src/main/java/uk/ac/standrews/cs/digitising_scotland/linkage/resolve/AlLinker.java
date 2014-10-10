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
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.BirthFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.PersonFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.factory.TypeFactory;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.FatherOfTypeLabel;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.MotherOfTypeLabel;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.SameAsTypeLabel;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.*;
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

    private static final String BIRTHRECORDTYPETEMPLATE = "src/test/resources/BirthRecord.jsn";
    private static final String DEATHRECORDTYPETEMPLATE = "src/test/resources/DeathRecord.jsn";
    private static final String MARRIAGERECORDTYPETEMPLATE = "src/test/resources/MarriageRecord.jsn";
    private static final String PERSONRECORDTYPETEMPLATE = "src/test/resources/PersonRecord.jsn";   // TODO write this spec.

    private Store store;
    private IRepository input_repo;             // Repository containing buckets of BDM records
    private IRepository linkage_repo;
    private IRepository blocked_people_repo;

    // Bucket declarations

    private IBucket<Birth> births;                     // Bucket containing birth records (inputs).
    private IBucket<Marriage> marriages;                  // Bucket containing marriage records (inputs).
    private IBucket<Death> deaths;                     // Bucket containing death records (inputs).
    private IBucket<Person> people;                     // Bucket containing people extracted from birth records
    private IBucket<Relationship> relationships;              // Bucket containing relationships between people
    private IBucketLXP types;
    private IIndexedBucket<IPair<Person>> lineage;             // Bucket containing pairs of potentially linked parents and child_ids

    // Paths to sources

    private static String source_base_path = "src/test/resources/BDMSet1";          // Path to source of vital event records in Digitising Scotland format

    private static String births_name = "birth_records";                            // Name of bucket & input file containing birth records (inputs).
    private static String marriages_name = "marriage_records";                      // Name of bucket & input file containing marriage records (inputs).
    private static String deaths_name = "death_records";                            // Name of bucket & input file containing marriage records (inputs).
    private static String types_name = "types";

    private static String births_source_path = source_base_path + "/" + births_name + ".txt";
    private static String marriages_source_path = source_base_path + "/" + marriages_name + ".txt";
    private static String deaths_source_path = source_base_path + "/" + deaths_name + ".txt";

    // Names of buckets

    private static String people_name = "people";                                   // Name of bucket containing maximal people extracted from birth records
    private static String relationships_name = "relationships";                     // Name of bucket containing relationships between people
    private static String lineage_name = "lineage";

    private ITypeLabel birthlabel;
    private ITypeLabel deathlabel;
    private ITypeLabel marriageLabel;
    private ITypeLabel personLabel;
    private TypeFactory tf;

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

        types =  input_repo.makeLXPBucket(types_name, BucketKind.DIRECTORYBACKED); // generic

        tf = TypeFactory.getInstance();
        initialiseTypes( types );

        initialiseFactories();

        births = input_repo.makeBucket(births_name,BucketKind.DIRECTORYBACKED,LXP.getInstance());   // TODO make these type specific
        deaths = input_repo.makeBucket(deaths_name,BucketKind.DIRECTORYBACKED,LXP.getInstance());   // TODO look for all occurances and change them to typed or generic

        marriages = input_repo.makeBucket(marriages_name, BucketKind.DIRECTORYBACKED, LXP.getInstance());

        people = linkage_repo.makeBucket(people_name,BucketKind.DIRECTORYBACKED,LXP.getInstance()); // linkage_repo.makeIndexedBucket(people_name);

        relationships = linkage_repo.makeBucket(relationships_name,BucketKind.DIRECTORYBACKED,LXP.getInstance()); // linkage_repo.makeIndexedBucket(relationships_name);

        lineage = (IIndexedBucket<IPair<Person>>) linkage_repo.makeBucket(lineage_name,BucketKind.INDEXED,LXP.getInstance());  // a bucket of Pairs of ids of records for people with the same first name, last name, sex, indexed by first id.
        lineage.addIndex(SameAsTypeLabel.first);


    }

    private void initialiseTypes( IBucketLXP types_bucket ) {

        birthlabel = tf.createType(BIRTHRECORDTYPETEMPLATE, "BIRTH", types_bucket);
        deathlabel = tf.createType(DEATHRECORDTYPETEMPLATE,"DEATH",  types_bucket);
        marriageLabel = tf.createType(MARRIAGERECORDTYPETEMPLATE, "MARRIAGE", types_bucket);
        personLabel = tf.createType(PERSONRECORDTYPETEMPLATE, "PERSON", types_bucket);
    }

    private void initialiseFactories() {
        BirthFactory birthFactory = new BirthFactory( birthlabel.getId() );
    }

    /**
     *  Import the birth,death, marriage records
     *  Initialises the people bucket with the people injected - one record for each person referenced in the original record
     *  Initialises the known(100% certain) relationships between people and stores the relationships in the relationships bucket
     */
    private void injestBDMRecords() throws RecordFormatException, JSONException, IOException {

        EventImporter.importDigitisingScotlandRecords(births, births_source_path, birthlabel);
        EventImporter.importDigitisingScotlandRecords(marriages, marriages_source_path, deathlabel);
        EventImporter.importDigitisingScotlandRecords(deaths, deaths_source_path, marriageLabel);

        createPeopleAndRelationshipsFromBirthsOrDeaths( births );
        createPeopleAndRelationshipsFromBirthsOrDeaths( deaths );
        createPeopleAndRelationshipsFromMarriages( marriages );
    }

    private void block() {
        try {

            IBlocker blocker = new MultipleBlockerOverPerson( people, blocked_people_repo, LXP.getInstance() );
            blocker.apply();

        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }

    private void link() {

        Iterator<IBucket<Person>> blocked_people_iterator = blocked_people_repo.getIterator(new PersonFactory(personLabel.getId()));


        while (blocked_people_iterator.hasNext()) {
            IBucket<Person> blocked_records = blocked_people_iterator.next();

            // Iterating over buckets of people with same first and last name and the same sex.

            BirthBirthLinker bdl = new BirthBirthLinker(blocked_records.getInputStreamT(), lineage.getOutputStreamT());
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

        IOutputStream<Person> people_stream = people.getOutputStreamT();
        IOutputStream<Relationship> relationships_stream = relationships.getOutputStreamT();

        IInputStream<Birth> stream = bucket.getInputStreamT();
        for (Birth birth_record : stream) {

            // add the people

      //      Person baby = createBaby(new Birth(birth_record), people_stream);

            Person baby = createPersonFromOwnBirthDeath(birth_record);
            people_stream.add(baby);
            Person dad = createFatherFromChildsBirthDeath(baby, birth_record);
            if( dad != null ) {
                people_stream.add(dad);
                addRelationship(birth_record, dad, baby, FatherOfTypeLabel.TYPE, relationships_stream);
            }
            Person mum = createMotherFromChildsBirthDeath(baby, birth_record);
            if( mum != null ) {
                people_stream.add(mum);
                addRelationship(birth_record, dad, baby, MotherOfTypeLabel.TYPE, relationships_stream);
            }

            // Could add is_child but not now...
        }
    }

    private void createPeopleAndRelationshipsFromMarriages( IBucket<Marriage> bucket ) {

        IOutputStream<Person> people_stream = people.getOutputStreamT();
        IOutputStream<Relationship> relationships_stream = relationships.getOutputStreamT();

        IInputStream<Marriage> stream = bucket.getInputStreamT();
        for (Marriage marriage_record : stream) {

            // add the people

            Person bride = createBrideFromMarriageRecord(marriage_record);
            people_stream.add(bride);
            Person groom = createGroomFromMarriageRecord(marriage_record);
            people_stream.add(groom);

            Person grooms_mother = createGroomsMotherFromMarriageRecord(groom, marriage_record);
            people_stream.add(grooms_mother);
            Person grooms_father = createGroomsFatherFromMarriageRecord(groom, marriage_record);
            people_stream.add(grooms_father);
            Person brides_mother = createBridesMotherFromMarriageRecord(bride, marriage_record);
            people_stream.add(brides_mother);
            Person brides_father = createBridesFatherFromMarriageRecord(bride, marriage_record);
            people_stream.add(brides_father);

            // add the relationships

            addRelationship(marriage_record, grooms_father, groom, FatherOfTypeLabel.TYPE, relationships_stream);
            addRelationship(marriage_record, grooms_mother, groom, MotherOfTypeLabel.TYPE, relationships_stream);
            addRelationship(marriage_record, brides_father, bride, FatherOfTypeLabel.TYPE, relationships_stream);
            addRelationship(marriage_record, brides_mother, bride, MotherOfTypeLabel.TYPE, relationships_stream);
        }
    }


    private void addRelationship(ILXP original_record, ILXP record1, ILXP record2, String relationship_type, IOutputStream output ) {

        if (record1 != null) {
            ILXP is_mother = new LXP();
            is_mother.put("TYPE", relationship_type);
            is_mother.put(MotherOfTypeLabel.child_id, Integer.toString(record2.getId()));
            is_mother.put(MotherOfTypeLabel.mother_id, Integer.toString(record1.getId()));
            is_mother.put(MotherOfTypeLabel.birth_record_id, Integer.toString(original_record.getId()));
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
