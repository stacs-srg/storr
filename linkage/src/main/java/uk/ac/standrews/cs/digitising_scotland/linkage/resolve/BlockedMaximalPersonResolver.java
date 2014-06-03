package uk.ac.standrews.cs.digitising_scotland.linkage.resolve;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryIterator;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.blocking.BlockingFirstLastSexOverPerson;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

/**
 * This class is derived from the blockingTests test
 * First attempt to do some linkage.
 * <p/>
 * Created by al on 06/06/2014.
 */
public class BlockedMaximalPersonResolver {

    private final IBucket matches;
    private IRepository blocked_repo;
    private IBucket people;

    private int matched_id = 0;

    public BlockedMaximalPersonResolver( IBucket people, IRepository blocked_repo, IBucket matches ) throws RepositoryException {
        this.people = people;
        this.blocked_repo = blocked_repo;
        this.matches = matches;

    }

    public void match() {
        try {
            IBlocker blocker = new BlockingFirstLastSexOverPerson( people, blocked_repo );
            blocker.apply();
        } catch (Exception e) {
            ErrorHandling.exceptionError(e, "Error whilst blocking");
        }
        pairwiseLinkBlockedRecords();
        tidyUp();
    }

    private static void tidyUp() {

        // delete blocked repo
        // TODO need an interface function to do this.
    }

    private void pairwiseLinkBlockedRecords() {

        RepositoryIterator blocked_record_iterator = blocked_repo.getIterator();

        while (blocked_record_iterator.hasNext()) {
            IBucket blocked_records = blocked_record_iterator.next();
            PersonLinker bdl = new PersonLinker(blocked_records.getInputStream(), matches.getOutputStream());
            bdl.pairwiseLink();
        }
    }

    /**
     * ************************* Pairwise linker ***************************
     */

    private class PersonLinker extends AbstractPairwiseLinker { // TODO need to abstract this out

        public PersonLinker(final ILXPInputStream input, final ILXPOutputStream output) {

            super(input, output);
        }

        @Override
        public boolean compare(final Pair pair) {

            ILXP first = pair.first();
            ILXP second = pair.second();

            return true;  // TODO fix up - for the minute make all blocked records match
        }

        @Override
        public void addToResults(final Pair pair, final ILXPOutputStream results) {

            ILXP first = pair.first();
            ILXP second = pair.second();

            System.out.println("Matched : " + first + "with:" + second);

            ILXP result_record = new LXP(matched_id++);
            result_record.put("first", first.get("id"));
            result_record.put("second", second.get("id"));
            result_record.put("relation", second.get("name-match"));
            result_record.put("resolver", this.getClass().toString());

            results.add(result_record);
        }

    }
}
