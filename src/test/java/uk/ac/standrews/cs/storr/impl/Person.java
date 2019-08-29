package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.types.JPO_FIELD;

public class Person extends JPO {

    @JPO_FIELD
    private int age;

    @JPO_FIELD
    public String address;

    public Person(int age, String address) {
        this.age = age;
        this.address = address;
    }

    /* Storr stuff */

    private static final JPOMetadata static_metadata;

    @Override
    public JPOMetadata getMetaData() {
        return static_metadata;
    }

    static {
        try {
            static_metadata = new JPOMetadata(Person.class,"JPOPerson");
        } catch (Exception var1) {
            throw new RuntimeException(var1);
        }
    }

}
