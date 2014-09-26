package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

/**
 * Provides filtering over an ILXPInputStream to populate an ILXPOutputStream containing records selected by the predicate select.
 * Created by al on 29/04/2014.
 */
public interface IFilter<T extends ILXP> {

    /**
     * @return the ILXPInputStream over which filtering is being performed.
     */
    ILXPInputStreamTypedOld<T> getInput();

    /**
     * @return the ILXPOutputStream to which selected records are being written
     */
    ILXPOutputStreamTypedOLD<T> getOutput();

    /**
     * Determines whether a record from the input stream should be written to the output stream
     * @param record to be selected or otherwise
     * @return true if the record is to be chosen for copying to the output stream.
     */
    boolean select(T record);
}
