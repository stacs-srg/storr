package uk.ac.standrews.cs.digitising_scotland.tools.fileutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.healthmarketscience.jackcess.ColumnBuilder;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.TableBuilder;

public class AccessDatabaseDumperTest {

    Database db;
    String databaseName = "testdb";
    String tableName = "testDB";
    private String databaseLocation = "target/" + databaseName + ".mdb";

    @Before
    public void setUp() throws IOException {

        db = Database.create(new File(databaseLocation));

        Table table = new TableBuilder(tableName).addColumn(new ColumnBuilder("ID", DataType.INT)).addColumn(new ColumnBuilder("Foo", DataType.TEXT)).addColumn(new ColumnBuilder("Bar", DataType.TEXT)).toTable(db);

        table.addRow(new Integer(0), "foo", "bar");
        table.addRow(new Integer(1), "foo1", "bar1");
        table.addRow(new Integer(2), "foo2", "bar2");
    }

    @Test
    public void testDumpingOfDatabase() throws IOException {

        ArrayList<String> expectedLines = new ArrayList<>();
        ArrayList<String> actualLines = new ArrayList<>();

        AccessDatabaseDumper dumper = new AccessDatabaseDumper(databaseLocation);
        dumper.writeTablesToFile("target/");
        File actualTsv = new File("target/" + databaseName + "/" + databaseName + ".tsv");
        File expectedTsv = new File("src/test/resources/databaseDumperExpected.tsv");
        BufferedReader br = new BufferedReader(new FileReader(expectedTsv));
        String line;

        while ((line = br.readLine()) != null) {
            expectedLines.add(line);
        }
        br = new BufferedReader(new FileReader(actualTsv));
        while ((line = br.readLine()) != null) {
            expectedLines.add(line);
        }

        int count = 0;
        for (String actual : actualLines) {
            Assert.assertEquals(expectedLines.get(count), actual);
            count++;
        }

    }
}
