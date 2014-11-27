package uk.ac.standrews.cs.digitising_scotland.linkage;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IReferenceType;
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
     * @param b        the bucket from which to import
     * @param filename containing the source records in digitising scotland format
     * @param label    the expected type of the records being imported
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws JSONException
     */
    public static int importDigitisingScotlandRecords(final IBucket b, final String filename, IReferenceType label) throws BucketException, IOException, RecordFormatException, JSONException {

        int counter = 0;
        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(filename), FileManipulation.FILE_CHARSET)) {

            ILXP record = importDigitisingScotlandRecord(reader, label);

            while (record != null) {
                b.put(record);
                record = importDigitisingScotlandRecord(reader, label);
                counter++;
            }
        }
        return counter;
    }

    /**
     * Creates a LXP birth record from a file.
     */
    private static ILXP importDigitisingScotlandRecord(final BufferedReader reader, IReferenceType label) throws IOException, RecordFormatException {

        Collection<String> field_names = label.getLabels();
        int record_type = label.getId();
        String line = reader.readLine();
        if (line == null) {
            return null;
        }

        try {
            LXP record = new LXP();

            Iterable<String> field_values = Arrays.asList(line.split(SEPARATOR, -1));
            addFields(field_names, field_values, record);

            return record;

        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new RecordFormatException(e.getMessage());
        }
    }

    private static void addFields(final Iterable<String> field_names, final Iterable<String> field_values, final LXP record) {

        Iterator<String> value_iterator = field_values.iterator();
        for (String field_name : field_names) {
            addField(value_iterator.next(), field_name, record);
        }
    }

    private static void addField(final String field_value, final String field_name, final LXP record) {

        record.put(field_name, field_value);
    }
}
