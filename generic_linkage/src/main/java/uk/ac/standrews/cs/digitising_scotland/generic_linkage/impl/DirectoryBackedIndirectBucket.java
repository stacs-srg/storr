package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;

import java.io.IOException;

/**
 * Created by al on 03/10/2014.
 */
public class DirectoryBackedIndirectBucket<T extends ILXP> extends DirectoryBackedIndirectBucketLXP implements IBucket {

    private final ILXPFactory<T> tFactory;

    public DirectoryBackedIndirectBucket(String name, String base_path, ILXPFactory<T> tFactory) throws IOException {
        super(name,base_path);
        this.tFactory = tFactory;

    }

    public static <T extends ILXP> IBucket<T> createBucket(String name, Repository repository, ILXPFactory<T> tFactory) {

        try {
            return new DirectoryBackedIndirectBucket( name, repository.getRepo_path(),tFactory );
        } catch (IOException e) {

            ErrorHandling.error("I/O Exception creating bucket");
            return null;
        }
    }

    @Override
    public void put(T record) throws IOException, JSONException {

    }


    @Override
    public IInputStream getInputStreamT() {
        return new TypedInputStream(super.getInputStream(), tFactory);
    }


    @Override
    public IOutputStream getOutputStreamT() {
        return new TypedOutputStream( super.getOutputStream() );
    }

    @Override
    public BucketKind getKind() {
        return BucketKind.INDIRECT;
    }


}
