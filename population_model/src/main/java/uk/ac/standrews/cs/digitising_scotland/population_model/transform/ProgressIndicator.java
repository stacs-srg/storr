package uk.ac.standrews.cs.digitising_scotland.population_model.transform;

/**
 * Created by graham on 14/05/2014.
 */
public abstract class ProgressIndicator {

    private final int number_of_updates;
    private int number_of_steps_since_last_update;
    private int total_steps;
    private int number_of_steps_completed;
    private int number_of_steps_per_update;

    public ProgressIndicator(int number_of_updates) {

        this.number_of_updates = number_of_updates;
        number_of_steps_since_last_update = 0;
    }

    public abstract void indicateProgress(double proportion_complete);

    public void setTotalSteps(int total_steps) {

        this.total_steps = total_steps;
        number_of_steps_per_update = total_steps / number_of_updates;
    }

    public void progressStep() {

        number_of_steps_completed++;
        number_of_steps_since_last_update++;

        if (number_of_steps_since_last_update >= number_of_steps_per_update) {

            indicateProgress((double)number_of_steps_completed / (double)total_steps);
            number_of_steps_since_last_update = 0;
        }
    }
}
