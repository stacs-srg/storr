package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.Repository;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryIterator;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPOutputStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.EventImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.BlockingPFPLMFFFoverBDMrecords;
import uk.ac.standrews.cs.digitising_scotland.linkage.events.DeathRecord;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


/**
 * This class is derived from the blockingTests test
 * First attempt to do some linkage.
 * <p/>
 * Created by al on 11/05/2014.
 */
public class Birth_Death_Resolver {

    private static String input_repo_path = "../digitising_scotland_linkage/src/test/resources/inputs";
    private static String blocked_repo_path = "../digitising_scotland_linkage/src/test/resources/blocked";
    private static String matches_repo_path = "../digitising_scotland_linkage/src/test/resources/PFPLMFFF";

    private static String source_base_path = "../digitising_scotland_linkage/src/test/resources/BDMSet1";;
    private static String births_name = "birth_records";
    private static String deaths_name = "death_records";
    private static String marriages_name = "marriage_records";
    private static String matches_name = "baby-father";

    private static String births_source_path = source_base_path + "/" + births_name + ".txt";
    private static String deaths_source_path = source_base_path + "/" + deaths_name + ".txt";
    private static String marriages_source_path = source_base_path + "/" + marriages_name + ".txt";

    private final IRepository input_repo;
    private final IRepository blocked_repo;
    private final IRepository matches_repo;

    // input buckets containing BDM records in LXP format

    private IBucket births;
    private IBucket deaths;
    private IBucket marriages;
    private IBucket matches;
    private IBucket results;
    private int matched_id = 0;

    public Birth_Death_Resolver() throws RepositoryException {

        input_repo = new Repository(input_repo_path);
        blocked_repo = new Repository(blocked_repo_path);
        matches_repo = new Repository(matches_repo_path);

        births = input_repo.makeBucket(births_name);
        deaths = input_repo.makeBucket(deaths_name);
        marriages = input_repo.makeBucket(marriages_name);
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

    private static void tidyUp() {

        try {
            FileManipulation.deleteDirectory(blocked_repo_path);
        } catch (IOException e) {
            ErrorHandling.exceptionError(e, "Error whilst tidying up");
        }
    }

    public void blockonPFPLMFFF() throws IOException, RecordFormatException, JSONException, RepositoryException {

        EventImporter importer = new EventImporter();

        importer.importBirths(births, births_source_path);
        importer.importDeaths(deaths, deaths_source_path);
        //    importer.importMarriages(marriages, marriages_source_path);

        BlockingPFPLMFFFoverBDMrecords blocker = new BlockingPFPLMFFFoverBDMrecords(births, deaths, marriages, blocked_repo);

        blocker.apply();
    }

    private void pairwiseLinkBlockedRecords() {

        RepositoryIterator blocked_record_iterator = blocked_repo.getIterator();

        while (blocked_record_iterator.hasNext()) {
            IBucket blocked_records = blocked_record_iterator.next();
            BirthDeathLinker bdl = new BirthDeathLinker( blocked_records.getInputStream(),matches.getOutputStream() );
            bdl.pairwise_link();
        }
    }


       public static void main(String[] args) throws Exception {

        Birth_Death_Resolver r = new Birth_Death_Resolver();
        r.match();
    }

    /**************************** Pairwise linker ****************************/

    private class BirthDeathLinker extends AbstractPairwiseLinker implements IPairWiseLinker {

        public BirthDeathLinker(ILXPInputStream input, ILXPOutputStream output) {

            super(input,output);
         }

        @Override
        public boolean compare(Pair pair) {
            // TODO we need to sort out naming and project linkage for fieldnames etc. - come back and look at properly.

            ILXP first = pair.first();
            ILXP second = pair.second();

            try {
                return islike(getdob(first), getdob(second));
            } catch (RecordFormatException e) {
                // treat as a not match
                return false;
            }
        }

        private boolean islike(Date dob1, Date dob2) {
            // code is a mess but not going to survive long... just an experiment
            if (dob1.equals(dob2)) { // exact match
                return true;
            }

            int dob1_days = DateManipulation.dateToDay(dob1);
            int dob2_days = DateManipulation.dateToDay(dob2);

            return dob1_days > dob2_days ? dob1_days - dob2_days < 365 : dob2_days - dob1_days < 365;   // within 1 year of each other (very liberal)
        }

        @Override
        public void add_to_results(Pair pair, ILXPOutputStream results) {
            ILXP first = pair.first();
            ILXP second = pair.second();

            System.out.println( "Matched : " + first + "with:" + second );

            ILXP result_record = new LXP(matched_id++);
            result_record.put( "first", first.get("id") );
            result_record.put( "second", second.get("id") );
            result_record.put( "relation", second.get("baby-father") );
            result_record.put( "resolver", this.getClass().toString() );

            results.add(result_record );
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


    }

}
