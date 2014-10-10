package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by al on 03/10/2014.
 */
public class DirectoryBackedIndexedBucket<T> extends DirectoryBackedIndexedBucketLXP implements IIndexedBucket {

    private final ILXPFactory tFactory;

    public DirectoryBackedIndexedBucket(final String name, final String base_path, ILXPFactory tFactory) throws IOException, RepositoryException {
        super( name, base_path );
        // code below here copied from DirectoryBackedBucket
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
            return new DirectoryBackedIndexedBucket(name, repo.getRepo_path(),tFactory );
        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }


    @Override
    public void addIndex(String label) throws IOException {
        super.addIndex(label);

    }

    @Override
    public IBucketIndex getIndexT(String label) {
        return new TypedBucketIndex( super.getIndex(label) );
    }


    public IInputStream getInputStreamT() {
        // We already know that the type is compatible - checked in constructor.
        return new TypedInputStream( getInputStream(), tFactory );
    }


    public IOutputStream getOutputStreamT() {
        // We already know that the type is compatible - checked in constructor.
        return new TypedOutputStream( getOutputStream() );
    }

    @Override
    public BucketKind getKind() {
        return BucketKind.INDEXED;
    }


    private class TypedBucketIndex implements IBucketIndex {
        private final IBucketIndexLXP underlying;

        public TypedBucketIndex(IBucketIndexLXP index) {
             underlying = index;
        }

        @Override
        public Set<String> keySet() throws IOException {
            return underlying.keySet();
        }

        @Override
        public IInputStream records(String value) throws IOException {
            return new TypedInputStream<>( underlying.records(value),tFactory);
        }


        @Override
        public List<Integer> values(String value) throws IOException {
            return null;
        }

        @Override
        public void add(ILXP record) throws IOException {

        }
    }
}
