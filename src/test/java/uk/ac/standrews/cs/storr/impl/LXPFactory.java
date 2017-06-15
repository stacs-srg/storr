package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.utilities.JSONReader;

/**
 * Created by al on 15/06/2017.
 */
public class LXPFactory extends TFactory<LXP> implements ILXPFactory<LXP> {

    @Override
    public LXP create(long persistent_object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException {

        return new LXP(persistent_object_id, reader, repository, bucket);
    }

}
