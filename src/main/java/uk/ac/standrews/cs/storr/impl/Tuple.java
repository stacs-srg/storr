package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.interfaces.IStoreReference;

/**
 * Created by al on 27/04/2017.
 */
public class Tuple<T extends ILXP> extends LXP {

    public static final String KEY = "KEY";
    public static final String VALUE = "VALUE";

    public Tuple(String key, IStoreReference<T> ref) throws PersistentObjectException {
        map.put( KEY,key );
        map.put( VALUE, ref );
    }

    public Tuple(String key, T value) throws PersistentObjectException {
        this( key, value.getThisRef() );
    }

    public Long getKey() {
        return Long.getLong( (String) map.get(KEY) );
    }

    public IStoreReference<T> getValue() {
            return (IStoreReference<T>) map.get( VALUE );
    }

}
