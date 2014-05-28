package uk.ac.standrews.cs.digitising_scotland.util;

/**
 * Created by graham on 28/05/2014.
 */
public class PercentageProgressIndicator extends ProgressIndicator {

    public PercentageProgressIndicator(int number_of_progress_updates) {
        super(number_of_progress_updates);
    }

    public void indicateProgress(double proportion_complete) {
        Diagnostic.traceNoSource(Math.round(proportion_complete * 100) + "%");
    }
}
