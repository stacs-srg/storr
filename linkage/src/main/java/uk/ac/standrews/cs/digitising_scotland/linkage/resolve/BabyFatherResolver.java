package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.Repository;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.stream_operators.sharder.AbstractPairwiseLinker;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPOutputStream;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IPair;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.linkage.EventImporter;
import uk.ac.standrews.cs.digitising_scotland.linkage.RecordFormatException;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.BlockingBFF_BFL_MPF_MPL;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.BirthLabels;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.IOException;
import java.util.Iterator;

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
        } catch (Exception e) {
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

        Iterator<IBucket> blocked_record_iterator = blocked_repo.getIterator();

        while (blocked_record_iterator.hasNext()) {
            IBucket blocked_records = blocked_record_iterator.next();
            BabyFatherLinker bfl = new BabyFatherLinker(blocked_records.getInputStream(), matches.getOutputStream());
            bfl.pairwiseLink();
        }
    }

    public static void main(final String[] args) throws Exception {

        new BabyFatherResolver().match();
    }

    /**
     * ************************* Pairwise linker ***************************
     */
    private static class BabyFatherLinker extends AbstractPairwiseLinker {

        public BabyFatherLinker(final ILXPInputStream input, final ILXPOutputStream output) {

            super(input, output);
        }

        public boolean compare(final IPair pair) {

            // TODO we need to sort out naming and project linkage for fieldnames etc. - come back and look at properly.

            ILXP potential_father = pair.first();
            ILXP potential_child = pair.second();

            // code is a mess but not going to survive long... just an experiment

            // are a birth record of 1 person in a probable father-child relationsip.

            // first make sure that the first is the possible father
            int father_birthYear = Integer.parseInt(potential_father.get(BirthLabels.BIRTH_YEAR));
            int child_birthYear = Integer.parseInt(potential_child.get(BirthLabels.BIRTH_YEAR));

            if (father_birthYear == child_birthYear) { // can't be in a parent-child relationship.
                return false;
            }
            if (father_birthYear > child_birthYear) { // swap pontential_father and child
                ILXP temp = potential_father;
                potential_father = potential_child;
                potential_child = temp;
            }
            if (potential_father.get(BirthLabels.SEX).equals("F")) {
                return false;
            }

            String fathers_surname = potential_child.get(BirthLabels.FATHERS_SURNAME);
            fathers_surname = fathers_surname.equals("0") ? potential_child.get(BirthLabels.SURNAME) : fathers_surname; // fathers surname coded as "0" if same as baby

            return potential_father.get(BirthLabels.SURNAME).equals(fathers_surname) &&
                    potential_father.get(BirthLabels.FORENAME).equals(potential_child.get(BirthLabels.FATHERS_FORENAME));
        }

        /**
         * Adds a matched result to a potential matched record repository/bucket.
         *
         * @param pair
         */
        public void addToResults(final IPair pair, final ILXPOutputStream results) {

            ILXP first = pair.first();
            ILXP second = pair.second();

            System.out.println("Matched : " + first + "with:" + second);

            ILXP result_record = new LXP();
            result_record.put("first", first.get("id"));
            result_record.put("first_type", first.get("TYPE"));
            result_record.put("second", second.get("id"));
            result_record.put("second_type", second.get("TYPE"));
            result_record.put("relation", second.get("same-person"));
            result_record.put("resolver", this.getClass().toString());

            results.add(result_record);
        }
    }
}
