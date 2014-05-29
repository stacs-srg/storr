package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.Repository;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryIterator;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.EventImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.BlockingBFF_BFL_MPF_MPL;
import uk.ac.standrews.cs.digitising_scotland.linkage.event_records.DeathRecord;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.Birth;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


/**
 * Performs pairwise linkage on babies and fathers
 * <p/>
 * Created by al on 11/05/2014.
 */
public class BabyFatherResolver {

    private static String input_repo_path = "src/test/resources/inputs";
    private static String blocked_repo_path = "src/test/resources/blocked";
    private static String matches_repo_path = "src/test/resources/BFF_BFL_MPF_MPL";

    private static String source_base_path = "src/test/resources/BDMSet1";
    private static String births_name = "birth_records";
    private static String matches_name = "baby_father";

    private static String births_source_path = source_base_path + "/" + births_name + ".txt";

    private final IRepository input_repo;
    private final IRepository blocked_repo;
    private final IRepository matches_repo;

    // input buckets containing BDM records in LXP format

    private IBucket births;
    private IBucket matches;
    private int matched_id = 0;

    public BabyFatherResolver() throws RepositoryException {

        input_repo = new Repository(input_repo_path);
        blocked_repo = new Repository(blocked_repo_path);
        matches_repo = new Repository(matches_repo_path);

        births = input_repo.makeBucket(births_name);
        matches = matches_repo.makeBucket(matches_name);

    }

    public void match() {
        try {
            blockonPFPLMFFF();
        } catch ( Exception e ) {
            ErrorHandling.exceptionError(e, "Error whilst blocking");
        }
        pairwiseLinkBlockedRecords();
        tidyUp();
    }

    public static void tidyUp() {
//        File dir = new File(blocked_repo_path);
//        FileManipulation.recursivelyDeleteFolder(blocked_repo_path);
    }

    public void blockonPFPLMFFF() throws IOException, RecordFormatException, JSONException, RepositoryException {

        EventImporter importer = new EventImporter();

        importer.importBirths(births, births_source_path);

        BlockingBFF_BFL_MPF_MPL blocker = new BlockingBFF_BFL_MPF_MPL(births, blocked_repo);

        blocker.apply();
    }

    private void pairwiseLinkBlockedRecords() {

        RepositoryIterator blocked_record_iterator = blocked_repo.getIterator();

        while (blocked_record_iterator.hasNext()) {
            IBucket blocked_records = blocked_record_iterator.next();
            BabyFatherLinker bfl = new BabyFatherLinker( blocked_records.getInputStream(),matches.getOutputStream() );
            bfl.pairwiseLink();
        }
    }

    public static void main(String[] args) throws Exception {

        BabyFatherResolver r = new BabyFatherResolver();
        r.match();
    }

    /**************************** Pairwise linker ****************************/

    private class BabyFatherLinker extends AbstractPairwiseLinker implements IPairWiseLinker {

        public BabyFatherLinker(ILXPInputStream input, ILXPOutputStream output) {

            super(input,output);
        }

        public boolean compare(Pair pair) {

            // TODO we need to sort out naming and project linkage for fieldnames etc. - come back and look at properly.

            ILXP potential_father = pair.first();
            ILXP potential_child = pair.second();

            // code is a mess but not going to survive long... just an experiment

            // are a birth record of 1 person in a probable father-child relationsip.

            // first make sure that the first is the possible father
            int father_birthYear = Integer.parseInt(potential_father.get(Birth.BIRTH_YEAR));
            int child_birthYear = Integer.parseInt(potential_child.get(Birth.BIRTH_YEAR));

            if( father_birthYear == child_birthYear ) { // can't be in a parent-child relationship.
                return false;
            }
            if( father_birthYear > child_birthYear ) { // swap pontential_father and child
                ILXP temp = potential_father;
                potential_father = potential_child;
                potential_child = temp;
            }
            if( potential_father.get( Birth.SEX ).equals( "F" ) ) {
                return false;
            }

            String fathers_surname = potential_child.get( Birth.FATHERS_SURNAME );
            fathers_surname =  fathers_surname.equals( "0" ) ? potential_child.get( Birth.SURNAME ) : fathers_surname; // fathers surname coded as "0" if same as baby

            return potential_father.get( Birth.SURNAME ).equals( fathers_surname ) &&
                    potential_father.get( Birth.FORENAME ).equals( potential_child.get( Birth.FATHERS_FORENAME ) );
        }

        private Date getdob(ILXP record) throws RecordFormatException {

            //TODO consider this -
//        Object o = record.instatiateJavaInstance();
//        if( o instanceof Birth )
//
//        VitalRecord rec = RecordFactory.createRecord(record);

            if (record.get("TYPE").equals("birth")) {

                String year_string = record.get("birth_year");
                String month_string = record.get("birth_month");
                String day_string = record.get("birth_day");

                return fieldsToDate(year_string, month_string, day_string);
            }

            if (record.get("TYPE").equals("death")) {

                String dob_string = record.get("date_of_birth");
                if( dob_string != null  ) {
                    try {
                        return DeathRecord.parseDate(dob_string);
                    } catch (ParseException e) {
                        throw new RecordFormatException( "error in birth date: " + dob_string );
                    }

                } else {

                    // we don't have a dob - need to approximate

                    String year_string = record.get("death_year");
                    String month_string = record.get("death_month");
                    String day_string = record.get("death_day");

                    Date death_date = fieldsToDate(year_string, month_string, day_string);

                    int age_in_years = Integer.parseInt(record.get("age_at_death"));

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(death_date);
                    cal.add(Calendar.YEAR, -age_in_years);
                    return cal.getTime();
                }
            } else {
                ErrorHandling.error("Found unexpected record type");
                throw new RecordFormatException( "error in date" );
            }
        }

        private Date fieldsToDate(String year_string, String month_string, String day_string) throws RecordFormatException {
            Calendar cal = Calendar.getInstance();
            cal.clear();
            try {
                int year = Integer.parseInt(year_string);
                int month = Integer.parseInt(month_string);
                int day = Integer.parseInt(day_string);
                cal.set(year, month, day);
                return cal.getTime();
            } catch( NumberFormatException e ) {
                ErrorHandling.error("Error parsing date (d/m/y) : " + day_string + "/" + month_string + "/" + year_string );
                throw new RecordFormatException( "error in date" );
            }
        }

        /**
         * Adds a matched result to a potential matched record repository/bucket.
         * @param pair
         */
        public void addToResults(Pair pair, ILXPOutputStream results) {
            ILXP first = pair.first();
            ILXP second = pair.second();

            System.out.println( "Matched : " + first + "with:" + second );

            ILXP result_record = new LXP(matched_id++);
            result_record.put( "first", first.get("id") );
            result_record.put( "first_type", first.get("TYPE") );
            result_record.put( "second", second.get("id") );
            result_record.put( "second_type", second.get("TYPE") );
            result_record.put( "relation", second.get("same-person") );
            result_record.put( "resolver", this.getClass().toString() );

            results.add(result_record );
        }

    }

}
