package uk.ac.standrews.cs.digitising_scotland.linkage;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.Birth;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.Death;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.Marriage;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
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

    int id = 1; // use this to uniquely stamp all items imported - might need something more sophisticated in future.

    public void importBirths(IBucket bucket, String filename) throws IOException, RecordFormatException, JSONException {

        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(filename), FileManipulation.FILE_CHARSET)) {

            ILXP record = importBirthRecord(reader);
            while (record != null) {
                bucket.save(record);
                record = importBirthRecord(reader);
            }
        }
    }

    public void importDeaths(IBucket bucket, String filename) throws IOException, RecordFormatException, JSONException {

        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(filename), FileManipulation.FILE_CHARSET)) {

            ILXP record = importDeathRecord(reader);
            while (record != null) {
                bucket.save(record);
                record = importDeathRecord(reader);
            }
        }
    }

    public void importMarriages(IBucket bucket, String filename) throws IOException, RecordFormatException, JSONException {

        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(filename), FileManipulation.FILE_CHARSET)) {

            ILXP record = importMarriageRecord(reader);
            while (record != null) {
                bucket.save(record);
                record = importMarriageRecord(reader);
            }
        }
    }

    /**
     * Creates a LXP birth record from a file.
     */
    private ILXP importBirthRecord(BufferedReader reader) throws IOException, RecordFormatException {

        return importRecord(reader, "birth", Birth.BIRTH_FIELD_NAMES);
    }

    /**
     * Creates a LXP death record from a file of death records.
     */
    private ILXP importDeathRecord(BufferedReader reader) throws IOException, RecordFormatException {

        return importRecord(reader, "death", Death.DEATH_FIELD_NAMES);
    }

    /**
     * Creates a LXP marriage record from a file of marriage records.
     */
    private ILXP importMarriageRecord(BufferedReader reader) throws IOException, RecordFormatException {
        
        return importRecord(reader, "marriage", Marriage.MARRIAGE_FIELD_NAMES);
    }

    /**
     * Creates a LXP marriage record from a file of marriage records.
     */
    private ILXP importRecord(BufferedReader reader, String record_type, Iterable<String> field_names) throws IOException, RecordFormatException {

        String line = reader.readLine();
        if (line == null) {
            return null;
        }

        try {
            LXP record = new LXP(id++);

            record.put("TYPE", record_type);

            Iterable<String> field_values = Arrays.asList(line.split(SEPARATOR, -1));
            addFields(field_names, field_values, record);

            return record;

        } catch (NoSuchElementException e) {
            throw new RecordFormatException(e.getMessage());
        }
    }
    
    private void addFields(Iterable<String> field_names, Iterable<String> field_values, LXP record) {

        Iterator<String> value_iterator = field_values.iterator();
        for (String field_name : field_names) {
            addField(value_iterator.next(), field_name, record);
        }
    }

    private void addField(String field_value, String field_name, LXP record) {

        record.put(field_name, field_value);
    }
}
