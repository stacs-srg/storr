package uk.ac.standrews.cs.digitising_scotland.parser.datastructures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.CodeTriple;

/**
 * The Class RecordFactoryTest tests the creation of {@link Record} from the {@link RecordFactory}.
 */
public class RecordFactoryTest {

    @Test
    public void makeMultipleCodedTrainingRecords() throws IOException, InputFormatException {

        File codeFile = new File("/testCodeMap.txt");
        CodeFactory.getInstance().loadDictionary(codeFile);

        String file = getClass().getResource("/multipleCauseRecordsTest.csv").getFile();
        File inputFile = new File(file);

        List<Record> records = FormatConverter.convert(inputFile);

        for (Record record : records) {
            System.out.print(record.getOriginalData().getDescription() + "\t");
            for (CodeTriple codeT : record.getOriginalData().getGoldStandardCodeTriples()) {
                System.out.print(codeT + "\t");
            }
            System.out.println();
        }
    }

    /**
     * Tests creating occupation records from the file occupationTestFormatPipe.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testOccupationMakeRecordsFromFile() throws IOException, InputFormatException {

        String line;
        Record record;
        File inputFile = new File(getClass().getResource("/occupationTestFormatPipe.txt").getFile());

        List<Record> listOfRecordsFromFile = RecordFactory.makeUnCodedRecordsFromFile(inputFile);

        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        Iterator<Record> iterator = listOfRecordsFromFile.iterator();

        while ((line = br.readLine()) != null) {
            record = iterator.next();
            checkRecordOccupationRecord(record, line);
        }

        br.close();
    }

    /**
     * Tests creating cause of death records from a file.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InputFormatException the input format exception
     */
    @Test
    public void testCauseOfDeathMakeRecordsFromFile() throws IOException, InputFormatException {

        String line;
        Record record;
        File inputFile = new File(getClass().getResource("/CauseOfDeathTestFileSmall.txt").getFile());

        List<Record> listOfRecordsFromFile = RecordFactory.makeUnCodedRecordsFromFile(inputFile);

        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        Iterator<Record> iterator = listOfRecordsFromFile.iterator();

        while ((line = br.readLine()) != null) {
            record = iterator.next();
            checkRecordCauseOfDeathRecord(record, line);
        }

        br.close();
    }

    /**
     * Asserts all parts of the record match what is in the string.
     * @param record record to be checked
     * @param line containing the line from the file the records were created from
     */
    private void checkRecordOccupationRecord(final Record record, final String line) {

        String[] lineSplit = line.split("\\|");
        String description = lineSplit[0];
        int year = Integer.parseInt(lineSplit[1]);
        int imageQuality = Integer.parseInt(lineSplit[2]);

        Assert.assertEquals(description, record.getOriginalData().getDescription());
        Assert.assertEquals(year, record.getOriginalData().getYear());
        Assert.assertEquals(imageQuality, record.getOriginalData().getImageQuality());

    }

    /**
     * Asserts all parts of the record match what is in the string.
     * @param record record to be checked
     * @param line containing the line from the file the records were created from
     */
    private void checkRecordCauseOfDeathRecord(final Record record, final String line) {

        String[] lineSplit = line.split("\\|");
        String description = lineSplit[5];
        int year = Integer.parseInt(lineSplit[1]);
        int ageGroup = Integer.parseInt(lineSplit[3]);
        int imageQuality = Integer.parseInt(lineSplit[2]);

        Assert.assertEquals(description, record.getOriginalData().getDescription());
        Assert.assertEquals(year, record.getOriginalData().getYear());
        Assert.assertEquals(ageGroup, ((CODOrignalData) record.getOriginalData()).getAgeGroup());
        Assert.assertEquals(imageQuality, record.getOriginalData().getImageQuality());

    }

    @Test
    public void testReadingCODRecordsWithMupltipleCodes() throws IOException, InputFormatException {

        File inputFile = new File(getClass().getResource("/kilmarnockBasedCoDTrainingPipe.txt").getFile());
        File codeList = new File(getClass().getResource("/CodeFactoryCoDFile.txt").getFile());
        File originalCodeList = new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile());

        CodeFactory.getInstance().loadDictionary(codeList);
        List<Record> records = RecordFactory.makeCodedRecordsFromFile(inputFile);
        Bucket codTrainingBucket = new Bucket(records);
        for (Record record : codTrainingBucket) {
            System.out.println(record);
        }

        CodeFactory.getInstance().loadDictionary(originalCodeList);

    }

}
