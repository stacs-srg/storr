package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.OLR;

/**
 * The Class ModelDoublePair.
 * Generally used to track which models in the {@link OLRCrossFold} are performing well, and which are not.
 */
public class ModelDoublePair implements Comparable<ModelDoublePair> {

    /** The number of correct classifications. */
    private double correct;

    /** The {@link OLRShuffled} model. */
    private OLRShuffled model;

    /**
     * Instantiates a new model double pair.
     *
     * @param model the model
     * @param correct the correct
     */
    public ModelDoublePair(final OLRShuffled model, final double correct) {

        this.correct = correct;
        this.model = model;
    }

    protected double getPropCorrect() {

        return correct;
    }

    @Override
    public int compareTo(final ModelDoublePair pair) {

        return Double.compare(this.correct, pair.correct);
    }

    /**
     * Gets the model.
     * @return the model
     */
    public OLRShuffled getModel() {

        return model;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(correct);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((model == null) ? 0 : model.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        ModelDoublePair other = (ModelDoublePair) obj;
        if (Double.doubleToLongBits(correct) != Double.doubleToLongBits(other.correct)) { return false; }
        if (model == null) {
            if (other.model != null) { return false; }
        }
        else if (!model.equals(other.model)) { return false; }
        return true;
    }

}
