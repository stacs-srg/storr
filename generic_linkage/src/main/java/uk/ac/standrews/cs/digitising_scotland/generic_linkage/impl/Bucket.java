package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by al on 19/09/2014.
 */
public class Bucket<T extends ILXP> extends DirectoryBackedLXPBucket implements IBucket<T> {

    private final ILXPFactory<T> tFactory;

    public Bucket(final String name, final String base_path, ILXPFactory<T> tFactory) throws Exception {  //TODO AL HERE ***

        super( name, base_path );
        // Check the types of the labels and the factory - make sure it is compatible with tFactory
        ITypeLabel tl = getBucketContentType();
        int basetype = tl.getId();

        this.tFactory = tFactory;
    }

    @Override
    public T get(int id) throws IOException, PersistentObjectException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath(id)), FileManipulation.FILE_CHARSET)) {

            return tFactory.create(id, new JSONReader(reader));
        }
    }


    public ILXPInputStreamTypedNew<T> getTypedInputStream() {  //TODO write these
        return null;
    }


    public ILXPOutputStreamTypedNew<T> getTypedOutputStream() { //TODO write theses
        return null;
    }
}
