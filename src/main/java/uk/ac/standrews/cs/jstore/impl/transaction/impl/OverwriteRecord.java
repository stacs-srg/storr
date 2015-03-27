package uk.ac.standrews.cs.jstore.impl.transaction.impl;

import uk.ac.standrews.cs.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.jstore.interfaces.ILXP;

/**
 * Created by al on 03/02/15.
 */
public class OverwriteRecord {

    public IBucket bucket;
    public long oid;

    public <T extends ILXP> OverwriteRecord(IBucket bucket, long oid) {
        this.bucket = bucket;
        this.oid = oid;
    }
}
