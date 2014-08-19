package uk.ac.standrews.cs.digitising_scotland.linkage;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.BirthLabels;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.CommonLabels;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.DeathLabels;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.MarriageLabels;
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

    /**
     * @param b        the bucket from which to import
     * @param filename containing the source records in digitising scotland format
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws JSONException
     */
    public int importBirths(final IBucket b, final String filename) throws IOException, RecordFormatException, JSONException {

        int counter = 0;
        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(filename), FileManipulation.FILE_CHARSET)) {

            ILXP record = importBirthRecord(reader);

            while (record != null) {
                b.put(record);
                record = importBirthRecord(reader);
                counter++;
            }
        }
        return counter;
    }

    /**
     * @param b        the bucket from which to import
     * @param filename containing the source records in digitising scotland format
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws JSONException
     */
    public int importDeaths(final IBucket b, final String filename) throws IOException, RecordFormatException, JSONException {

        int counter = 0;
        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(filename), FileManipulation.FILE_CHARSET)) {

            ILXP record = importDeathRecord(reader);

            while (record != null) {
                b.put(record);
                record = importDeathRecord(reader);
                counter++;
            }
        }
        return counter;
    }

    /**
     * @param b        the bucket from which to import
     * @param filename containing the source records in digitising scotland format
     * @return the number of records read in
     * @throws IOException
     * @throws RecordFormatException
     * @throws JSONException
     */
    public int importMarriages(final IBucket b, final String filename) throws IOException, RecordFormatException, JSONException {

        int counter = 0;
        try (final BufferedReader reader = Files.newBufferedReader(Paths.get(filename), FileManipulation.FILE_CHARSET)) {

            ILXP record = importMarriageRecord(reader);

            while (record != null) {
                b.put(record);
                record = importMarriageRecord(reader);
                counter++;
            }
        }
        return counter;
    }

    /**
     * Creates a LXP birth record from a file.
     */
    private ILXP importBirthRecord(final BufferedReader reader) throws IOException, RecordFormatException {

        return importRecord(reader, BirthLabels.TYPE, BirthLabels.BIRTH_FIELD_NAMES);
    }

    /**
     * Creates a LXP death record from a file of death records.
     */
    private ILXP importDeathRecord(final BufferedReader reader) throws IOException, RecordFormatException {

        return importRecord(reader, DeathLabels.TYPE, DeathLabels.DEATH_FIELD_NAMES);
    }

    /**
     * Creates a LXP marriage record from a file of marriage records.
     */
    private ILXP importMarriageRecord(final BufferedReader reader) throws IOException, RecordFormatException {

        return importRecord(reader, MarriageLabels.TYPE, MarriageLabels.MARRIAGE_FIELD_NAMES);
    }

    /**
     * Creates a LXP record from a file of records.
     */
    private ILXP importRecord(final BufferedReader reader, final String record_type, final Iterable<String> field_names) throws IOException, RecordFormatException {

        String line = reader.readLine();
        if (line == null) {
            return null;
        }

        try {
            LXP record = new LXP();

            record.put(CommonLabels.TYPE_LABEL, record_type);

            Iterable<String> field_values = Arrays.asList(line.split(SEPARATOR, -1));
            addFields(field_names, field_values, record);

            return record;

        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new RecordFormatException(e.getMessage());
        }
    }

    private void addFields(final Iterable<String> field_names, final Iterable<String> field_values, final LXP record) {

        Iterator<String> value_iterator = field_values.iterator();
        for (String field_name : field_names) {
            addField(value_iterator.next(), field_name, record);
        }
    }

    private void addField(final String field_value, final String field_name, final LXP record) {

        record.put(field_name, field_value);
    }
}
