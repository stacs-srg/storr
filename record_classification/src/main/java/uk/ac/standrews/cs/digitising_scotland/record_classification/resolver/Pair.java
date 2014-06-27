package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;

/**
 * Generic class representing a pair of objects.
 *
 * @param <L> the generic type held in 'left'
 * @param <R> the generic type held in 'right'
 */
public class Pair<L, R> {

    /** The left. */
    private final L left;

    /** The right. */
    private final R right;

    /**
     * Instantiates a new pair.
     *
     * @param left the left
     * @param right the right
     */
    public Pair(final L left, final R right) {

        this.left = left;
        this.right = right;
    }

    /**
     * Gets the left object.
     *
     * @return the left
     */
    public L getLeft() {

        return left;
    }

    /**
     * Gets the right object.
     *
     * @return the right
     */
    public R getRight() {

        return right;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return left.hashCode() ^ right.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {

        if (o == null) { return false; }
        if (!(o instanceof Pair)) { return false; }
        Pair<?, ?> pairo = (Pair<?, ?>) o;
        return this.left.equals(pairo.getLeft()) && this.right.equals(pairo.getRight());
    }

}
