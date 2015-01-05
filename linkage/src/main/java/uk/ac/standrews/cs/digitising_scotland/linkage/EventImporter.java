package uk.ac.standrews.cs.digitising_scotland.linkage;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IReferenceType;
import uk.ac.standrews.cs.digitising_scotland.jstore.types.Types;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Marriage;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * Utility classes for importing records in digitising scotland format
 * Created by al on 25/04/2014.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class EventImporter {

    private static final String SEPARATOR = "\\|";

    /**
     * @param deaths the bucket from which to import
     * @param filename containing the source records in digitising scotland format
     * @param referencetype    the expected type of the records being imported
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws BucketException
     */
    public static int importDigitisingScotlandDeaths(IBucket<Death> deaths, String filename, IReferenceType referencetype) throws RecordFormatException, IOException, BucketException, IllegalKeyException {
        long counter = 0;
        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(filename), FileManipulation.FILE_CHARSET)) {

            int count = 0;

            try {
                while (true) {
                    Death d = new Death();
                    importDigitisingScotlandRecord(d, reader, referencetype);
                    deaths.makePersistent(d);
                    count++;
                }
            } catch (IOException e) {
                // expect this to be thrown when we getObjectById to the end.
            }
            return count;
        }
    }

    /**
     * @param marriages     the bucket from which to import
     * @param filename      containing the source records in digitising scotland format
     * @param referencetype the expected type of the records being imported
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws BucketException
     */
    public static int importDigitisingScotlandMarriages(IBucket<Marriage> marriages, String filename, IReferenceType referencetype) throws RecordFormatException, IOException, BucketException, IllegalKeyException {
        long counter = 0;
        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(filename), FileManipulation.FILE_CHARSET)) {

            int count = 0;

            try {
                while (true) {
                    Marriage m = new Marriage();
                    importDigitisingScotlandRecord(m, reader, referencetype);
                    marriages.makePersistent(m);
                    count++;
                }
            } catch (IOException e) {
                // expect this to be thrown when we getObjectById to the end.
            }
            return count;
        }
    }

    /**
     * @param births        the bucket from which to import
     * @param filename      containing the source records in digitising scotland format
     * @param referencetype the expected type of the records being imported
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws BucketException
     */
    public static int importDigitisingScotlandBirths(IBucket<Birth> births, String filename, IReferenceType referencetype) throws RecordFormatException, IOException, BucketException, IllegalKeyException {
        long counter = 0;
        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(filename), FileManipulation.FILE_CHARSET)) {

            int count = 0;

            try {
                while (true) {
                    Birth b = new Birth();
                    importDigitisingScotlandRecord(b, reader, referencetype);
                    births.makePersistent(b);
                    count++;
                }
            } catch (IOException e) {
                // expect this to be thrown when we getObjectById to the end.
            }
            return count;
        }
    }


    /**
     * Fills in a LXP record data from a file.
     */
    private static void importDigitisingScotlandRecord(final LXP record, final BufferedReader reader, IReferenceType label) throws IOException, RecordFormatException, IllegalKeyException {

        Collection<String> field_names = label.getLabels();
        long record_type = label.getId();
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("read in empty line"); // expected in the way this is called
        }

        try {

            Iterable<String> field_values = Arrays.asList(line.split(SEPARATOR, -1));
            addFields(field_names, field_values, record);

        } catch (NoSuchElementException e) {
            throw new RecordFormatException(e.getMessage());
        }
    }

    private static void addFields(final Iterable<String> field_names, final Iterable<String> field_values, final LXP record) throws IllegalKeyException {

        Iterator<String> value_iterator = field_values.iterator();
        for (String field_name : field_names) {
            addField(value_iterator.next(), field_name, record);
        }
    }

    private static void addField(final String field_value, final String field_name, final LXP record) throws IllegalKeyException {

        if (!Types.getTypeRep(record.getClass()).containsKey(field_name)) {
            throw new IllegalKeyException("Illegal key: " + field_name);
        }
        record.put(field_name, field_value);
    }

}
