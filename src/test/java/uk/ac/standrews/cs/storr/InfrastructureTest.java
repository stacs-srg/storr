/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module storr.
 *
 * storr is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * storr is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with storr. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.storr;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.standrews.cs.storr.impl.*;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.impl.transaction.exceptions.TransactionFailedException;
import uk.ac.standrews.cs.storr.impl.transaction.interfaces.ITransaction;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IReferenceType;
import uk.ac.standrews.cs.utilities.FileManipulation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class InfrastructureTest extends CommonTest {

    private static String generic_bucket_name1 = "BUCKET1";
    private static String generic_bucket_name2 = "BUCKET2";
    private static String generic_bucket_name3 = "BUCKET3";

    private IReferenceType personlabel;
    private IReferenceType personlabel2;
    private IReferenceType personreftuple;

    private AtomicInteger conflict_counter;
    private CountDownLatch latch;

    @Before
    public void setUp() throws RepositoryException, IOException, StoreException, URISyntaxException {

        super.setUp();

        String person_record_type_template = FileManipulation.getResourcePath(InfrastructureTest.class, "PersonRecord.jsn").toString();
        String person_ref_type_template = FileManipulation.getResourcePath(InfrastructureTest.class, "PersonRefRecord.jsn").toString();

        repository.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED);
        repository.makeBucket(generic_bucket_name2, BucketKind.DIRECTORYBACKED);
        repository.makeBucket(generic_bucket_name3, BucketKind.DIRECTORYBACKED);

        TypeFactory type_factory = store.getTypeFactory();

        personlabel = type_factory.createType(person_record_type_template, "Person");
        personlabel2 = type_factory.createType(person_record_type_template, "Person");
        personreftuple = type_factory.createType(person_ref_type_template, "PersonRefTuple");

        conflict_counter = new AtomicInteger(0);
        latch = new CountDownLatch(2);
    }

    @Test
    public synchronized void testLXPCreation() throws RepositoryException, IllegalKeyException, BucketException {
        IBucket b = repository.getBucket(generic_bucket_name1);
        DynamicLXP lxp = new DynamicLXP();
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.makePersistent(lxp);

        long id = lxp.getId();
        LXP retrievedLXP = b.getObjectById(id);
        assertEquals(retrievedLXP, lxp);
    }

    @Test
    public synchronized void testLXPListCreation() throws RepositoryException, IllegalKeyException, BucketException {
        IBucket b = repository.getBucket(generic_bucket_name1);
        DynamicLXP lxp = new DynamicLXP();
        lxp.put("age", "42");
        lxp.put("address", "home");
        List l = new ArrayList();
        l.add("fish");
        l.add(1);
        l.add(3.12);
        lxp.put("mylist", l);
        b.makePersistent(lxp);

        long id = lxp.getId();
        LXP retrievedLXP = b.getObjectById(id);
        assertEquals(retrievedLXP, lxp);
    }

    @Test(expected = BucketException.class)
    public synchronized void testLXPOverwrite() throws RepositoryException, IllegalKeyException, BucketException {
        IBucket b = repository.getBucket(generic_bucket_name1);
        DynamicLXP lxp = new DynamicLXP();
        lxp.put("age", "42");
        lxp.put("address", "home");

        b.makePersistent(lxp);
        b.makePersistent(lxp);
    }

    @Test(expected = BucketException.class)
    public synchronized void testLabelledLXP1() throws RepositoryException, IllegalKeyException, IOException, BucketException {
        IBucket b = repository.getBucket(generic_bucket_name1);
        b.setTypeLabelID(personlabel.getId());

        DynamicLXP lxp = new DynamicLXP();
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.makePersistent(lxp);
    }

    @Test
    public synchronized void testSimpleTransaction() throws RepositoryException, IllegalKeyException, BucketException, StoreException, TransactionFailedException {

        IBucket b = repository.getBucket(generic_bucket_name1);

        DynamicLXP lxp = new DynamicLXP();
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.makePersistent(lxp);
        long oid = lxp.getId();

        ITransaction txn = store.getTransactionManager().beginTransaction();


        LXP lxp2 = b.getObjectById(oid);
        lxp2.put("age", "43");

        b.update(lxp2);

        txn.commit();

        long id = lxp2.getId();
        LXP retrievedLXP2 = b.getObjectById(id);
        String age = (String) retrievedLXP2.get("age");
        assertEquals("43", age);

        // TODO a similar one with abort.
    }

    @Test(expected = BucketException.class)
    public synchronized void updateOutwithTransactionThrowsException() throws RepositoryException, IllegalKeyException, BucketException, StoreException {
        IBucket b = repository.getBucket(generic_bucket_name1);

        DynamicLXP lxp = new DynamicLXP();
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.makePersistent(lxp);
        long oid = lxp.getId();

        LXP lxp2 = b.getObjectById(oid);
        lxp2.put("age", "43");

        b.update(lxp2);
    }

    @Test(expected = BucketException.class)
    public synchronized void updateNonPersistentObjectThrowsException() throws RepositoryException, IllegalKeyException, BucketException, StoreException {
        IBucket b = repository.getBucket(generic_bucket_name1);

        DynamicLXP lxp = new DynamicLXP();
        lxp.put("age", "42");
        lxp.put("address", "home");

        b.update(lxp);
    }

    @Test
    public synchronized void testMultiBucketTransaction() throws RepositoryException, IllegalKeyException, BucketException, StoreException, TransactionFailedException {

        IBucket b1 = repository.getBucket(generic_bucket_name1);
        IBucket b2 = repository.getBucket(generic_bucket_name2);

        DynamicLXP lxp1 = new DynamicLXP();
        lxp1.put("age", "42");
        b1.makePersistent(lxp1);
        long oid1 = lxp1.getId();

        DynamicLXP lxp2 = new DynamicLXP();
        lxp2.put("age", "42");
        b2.makePersistent(lxp2);
        long oid2 = lxp2.getId();

        ITransaction txn = store.getTransactionManager().beginTransaction();

        try {

            LXP lxp11 = b1.getObjectById(oid1);
            LXP lxp22 = b2.getObjectById(oid2);
            lxp11.put("age", "43");
            lxp22.put("age", "43");

            b1.update(lxp11);
            b2.update(lxp22);

            txn.commit();
        } catch (TransactionFailedException e) {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
        // do lookup again and check that it is 43 - if it worked.
        // do a similar one with abort.
    }

    @Test
    @Ignore
    public synchronized void testTransactionConflict() throws RepositoryException, IllegalKeyException, BucketException, StoreException {

        // TODO this test is broken since the fail() calls in UpdateThread won't be detected here.

        IBucket b = repository.getBucket(generic_bucket_name1);

        DynamicLXP lxp = new DynamicLXP();
        lxp.put("age", "42");
        b.makePersistent(lxp);
        long oid = lxp.getId();

        UpdateThread t1 = new UpdateThread(b, oid);
        UpdateThread t2 = new UpdateThread(b, oid);

        t1.start();
        t2.start();

        try {  // wait for the transactions to finish.
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(1,conflict_counter.get() );
    }

    @Test
    public synchronized void testLabelledLXP2() throws Exception {
        IBucket b = repository.getBucket(generic_bucket_name1);
        b.setTypeLabelID(personlabel.getId());

        DynamicLXP lxp = new DynamicLXP();        // correct structure but no label - that is OK!
        lxp.put("name", "al");
        lxp.put("age", 55);
        b.makePersistent(lxp);
    }

    @Test
    public synchronized void testLabelledLXP3() throws Exception {
        IBucket b = repository.getBucket(generic_bucket_name1);
        b.setTypeLabelID(personlabel.getId());

        DynamicLXP lxp = new DynamicLXP();        // correct structure
        lxp.put("name", "al");
        lxp.put("age", 55);
        lxp.getMetaData().setType( personlabel );    // with correct label
        b.makePersistent(lxp); // Should succeed: labels correct and type label identical to bucket
    }

    @Test
    public synchronized void testLabelledLXP4() throws Exception {
        IBucket b = repository.getBucket(generic_bucket_name1);
        b.setTypeLabelID(personlabel2.getId());

        DynamicLXP lxp = new DynamicLXP();        // correct structure
        lxp.put("name", "al");
        lxp.put("age", 55);
        lxp.getMetaData().setType( personlabel ); // structurally equivalent label
        b.makePersistent(lxp); // Should succeed labels correct and type label structurally equivalent
    }

    @Test(expected = Exception.class)
    public synchronized void testLabelledLXP5() throws Exception {
        IBucket b = repository.getBucket(generic_bucket_name1);
        b.setTypeLabelID(personlabel2.getId());

        DynamicLXP lxp = new DynamicLXP();        // incorrect structure
        lxp.put("name", "al");
        lxp.put("address", "home");
        lxp.getMetaData().setType( personlabel ); // correct type but not structurally equivalent type
        b.makePersistent(lxp);
        // should getString an exception due to incorrect structure

    }

    @Test
    public synchronized void testReferenceLabel() throws Exception {
        IBucket b1 = repository.getBucket(generic_bucket_name1);
        b1.setTypeLabelID(personlabel.getId());
        IBucket b2 = repository.getBucket(generic_bucket_name2);
        b2.setTypeLabelID(personreftuple.getId());


        DynamicLXP lxp = new DynamicLXP();        // correct structure
        lxp.put("name", "al");
        lxp.put("age", 55);
        lxp.getMetaData().setType(personlabel); // correct type

        b1.makePersistent(lxp);

        DynamicLXP lxp2 = new DynamicLXP();        // correct structure
        lxp2.put("person_ref", new StoreReference<>(repository, b1, lxp));
        b2.makePersistent(lxp2);
    }

    @Test(expected = BucketException.class)
    public synchronized void testIllegalReferenceLabel() throws Exception {

        IBucket b1 = repository.getBucket(generic_bucket_name1);
        b1.setTypeLabelID(personlabel.getId());
        IBucket b2 = repository.getBucket(generic_bucket_name2);
        b2.setTypeLabelID(personreftuple.getId());

        DynamicLXP lxp = new DynamicLXP();        // correct structure
        lxp.put("name", "al");
        lxp.put("age", 55);
        lxp.getMetaData().setType( personlabel );   // correct label

        b1.makePersistent(lxp);

        DynamicLXP lxp2 = new DynamicLXP();        // correct structure
        lxp2.put("person_ref", Integer.toString(100)); // an illegal reference - not a legal identifier

        b2.makePersistent(lxp2);
    }

    @Test(expected = BucketException.class)
    public synchronized void testIllegalReferenceLabel2() throws RepositoryException, IllegalKeyException, IOException, BucketException {

        IBucket b1 = repository.getBucket(generic_bucket_name1);
        b1.setTypeLabelID(personlabel.getId());
        IBucket b2 = repository.getBucket(generic_bucket_name2);
        b2.setTypeLabelID(personreftuple.getId());
        IBucket b3 = repository.getBucket(generic_bucket_name3);

        DynamicLXP lxp = new DynamicLXP();        // correct structure
        lxp.put("name", "al");
        lxp.put("age", 55);
        lxp.getMetaData().setType(personlabel); // correct type

        b1.makePersistent(lxp);

        DynamicLXP lxp2 = new DynamicLXP();
        b3.makePersistent(lxp2); // just stick something in bucket3
        long lxp2_id = lxp2.getId();

        DynamicLXP lxp3 = new DynamicLXP();        // correct structure
        lxp3.put("person_ref", new StoreReference<>(repository, b3, lxp2)); // an illegal reference to this tuple - wrong reference type

        b2.makePersistent(lxp3);
    }

    @Test
    public synchronized void testLegalTypedRecordInUntypedBucket() throws RepositoryException, IllegalKeyException, BucketException {

        IBucket b1 = repository.getBucket(generic_bucket_name1);

        DynamicLXP lxp = new DynamicLXP();        // incorrect structure
        lxp.put("name", "al");
        lxp.put("age", 55);
        lxp.getMetaData().setType(personlabel); // correct type

        b1.makePersistent(lxp);
    }

    @Ignore
    @Test(expected = BucketException.class)
    public synchronized void testIllegalTypedRecordInUntypedBucket() throws RepositoryException, IllegalKeyException, BucketException {

        IBucket b1 = repository.getBucket(generic_bucket_name1);

        DynamicLXP lxp = new DynamicLXP();        // incorrect structure
        lxp.put("name", "al");
        lxp.put("wrongfield", "55");
        lxp.getMetaData().setType(personlabel); // correct type // TODO this does not fail - see comments in setType in MetaData - needs refactoring.

        b1.makePersistent(lxp);
    }

    @Test
    public synchronized void testLXPFromFile() throws RepositoryException, KeyNotFoundException, TypeMismatchFoundException, IllegalKeyException, BucketException {
        IBucket b = repository.getBucket(generic_bucket_name1);
        DynamicLXP lxp = new DynamicLXP();
        lxp.put("age", "42");
        lxp.put("address", "home");
        b.makePersistent(lxp);       // <<--------- write record **
        long id = lxp.getId();

        LXP lxp2 = b.getObjectById(id);
        assertTrue(lxp2.getMetaData().containsLabel("age"));
        assertEquals(lxp2.get("age"), "42");
        assertTrue(lxp2.getMetaData().containsLabel("address"));
        assertEquals(lxp2.get("address"), "home");
    }

    @Test
    public synchronized void testStreams() throws RepositoryException, KeyNotFoundException, TypeMismatchFoundException, IllegalKeyException, BucketException {
        IBucket b = repository.getBucket(generic_bucket_name1);
        // create a few records
        for (int i = 1; i < 10; i++) {
            DynamicLXP lxp = new DynamicLXP();
            lxp.put("age", "42");
            lxp.put("address", "home");
            b.makePersistent(lxp);
        }

        int count = 1;
        for (Object o : b.getInputStream()) {
            LXP record = (LXP) o;
            assertTrue(record.getMetaData().containsLabel("age"));
            assertEquals(record.get("age"), "42");
            assertTrue(record.getMetaData().containsLabel("address"));
            assertEquals(record.get("address"), "home");
            count++;
        }
        assertEquals(count, 10);
    }

    @Test
    public synchronized void testBaseTypedFields() throws RepositoryException, KeyNotFoundException, TypeMismatchFoundException, IllegalKeyException, BucketException {
        IBucket b = repository.getBucket(generic_bucket_name1);
        DynamicLXP lxp = new DynamicLXP();

        // base types supported are: BOOLEAN, DOUBLE, INTEGER, LONG & STRING
        lxp.put("boolean", true);
        lxp.put("double", 3.14d);
        lxp.put("int", 7);
        lxp.put("long", 23L);
        lxp.put("string", "al");
        b.makePersistent(lxp);       // <<--------- write record **
        long id = lxp.getId();

        LXP lxp2 = b.getObjectById(id);
        assertTrue(lxp2.getMetaData().containsLabel("boolean"));
        assertEquals(lxp2.get("boolean"), true);
        assertTrue(lxp2.getMetaData().containsLabel("double"));
        assertEquals(3.14d, lxp2.get("double"));
        assertTrue(lxp2.getMetaData().containsLabel("int"));
        assertEquals(lxp2.get("int"), 7);
        assertTrue(lxp2.getMetaData().containsLabel("long"));
        assertEquals(lxp2.get("long"), 23L);
        assertTrue(lxp2.getMetaData().containsLabel("string"));
        assertEquals(lxp2.get("string"), "al");
    }

    @Test(expected = IllegalKeyException.class)
    public synchronized void emptyStringInLabelType() throws RepositoryException, BucketException, IllegalKeyException, StoreException {
        IBucket b = repository.getBucket(generic_bucket_name1);
        DynamicLXP lxp = new DynamicLXP();
        lxp.put("", "fish");
        b.makePersistent(lxp);       // <<--------- write record **
    }

    // TODO need a test for illegal field label types e.g. reference to a type that doesn't exist

    @Test(expected = IllegalKeyException.class)
    public synchronized void nullLabelType() throws RepositoryException, BucketException, IllegalKeyException, StoreException {
        IBucket b = repository.getBucket(generic_bucket_name1);
        DynamicLXP lxp = new DynamicLXP();
        lxp.put(null, "fish");
        b.makePersistent(lxp);       // <<--------- write record **
    }

    @Test
    public synchronized void emptyStringValue() throws RepositoryException, BucketException, IllegalKeyException, StoreException {
        IBucket b = repository.getBucket(generic_bucket_name1);
        DynamicLXP lxp = new DynamicLXP();
        lxp.put("string", "");
        b.makePersistent(lxp);       // <<--------- write record **

        long id = lxp.getId();
        LXP retrievedLXP = b.getObjectById(id);
        String retrievedString = (String) retrievedLXP.get("string");
        assertEquals("", retrievedString);
    }

    private class UpdateThread extends Thread {

        private final long oid;
        private final IBucket b;

        public UpdateThread(IBucket b, long oid) {
            this.b = b;
            this.oid = oid;
        }

        public void run() {
            ITransaction txn = null;
            try {
                txn = store.getTransactionManager().beginTransaction();
            } catch (TransactionFailedException e) {
                fail("Exception: " + e.toString());
            }

            try {
                LXP lxp2 = b.getObjectById(oid);
                lxp2.put("age", "43");
                b.update(lxp2);

                // Want both threads to get to here at 'same time'.
                latch.countDown(); // decrement
                latch.await();    // wait for the latch to get to zero.

                try {
                    txn.commit();
                } catch (TransactionFailedException e) {
                    conflict_counter.incrementAndGet();
                }
            } catch (InterruptedException | StoreException | IllegalKeyException | BucketException e) {
                fail("Exception: " + e.toString());
            } finally {
                if (txn.isActive()) {
                    txn.rollback();
                }
            }
        }
    }
}
