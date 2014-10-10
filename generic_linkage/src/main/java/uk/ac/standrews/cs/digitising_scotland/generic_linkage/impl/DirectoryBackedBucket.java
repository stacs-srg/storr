package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * Created by al on 19/09/2014.
 */
public class DirectoryBackedBucket<T extends ILXP> extends DirectoryBackedBucketLXP implements IBucket<T> {

    private final ILXPFactory<T> tFactory;

    public DirectoryBackedBucket(final String name, final String base_path, ILXPFactory<T> tFactory) throws RepositoryException, IOException {
        super( name, base_path );
        this.tFactory = tFactory;
        int type_label_id = this.getTypeLabelID();
        if( type_label_id == -1 ) { // no index
            throw new RepositoryException("no type label associated with bucket");
        }
        try {
            if (!tFactory.checkConsistentWith(type_label_id)) {
                throw new RepositoryException("incompatible types");
            }
        } catch (PersistentObjectException e) {
                throw new RepositoryException( e.getMessage() );
        }
    }

    public static IBucket createBucket(final String name, IRepository repo, ILXPFactory tFactory ) throws RepositoryException  {
        IBucketLXP base = DirectoryBackedBucketLXP.createBucket(name, repo);
        try {
            return new DirectoryBackedBucket(name, repo.getRepo_path(),tFactory );
        } catch (IOException e) {
           throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public T get(int id) throws IOException, PersistentObjectException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath(id)), FileManipulation.FILE_CHARSET)) {

            return tFactory.create(id, new JSONReader(reader));
        }
    }


    public IInputStream<T> getInputStreamT() {
        // We already know that the type is compatible - checked in constructor.
        return new TypedInputStream( getInputStream(), tFactory );
    }


    public IOutputStream<T> getOutputStreamT() {
        // We already know that the type is compatible - checked in constructor.
        return new TypedOutputStream( getOutputStream() );
    }

    @Override
    public BucketKind getKind() {
        return BucketKind.DIRECTORYBACKED;
    }





}
