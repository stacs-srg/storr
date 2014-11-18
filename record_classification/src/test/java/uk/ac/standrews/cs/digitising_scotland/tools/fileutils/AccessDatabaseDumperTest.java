/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.tools.fileutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.healthmarketscience.jackcess.ColumnBuilder;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.TableBuilder;

public class AccessDatabaseDumperTest {

    private Database db;
    private static String databaseName = "testDatabase";
    private static String tableName = "testTable";
    private static String databaseLocation = "target/" + databaseName + ".mdb";

    @Before
    public void setUp() throws IOException {

        db = Database.create(new File(databaseLocation));

        Table table = new TableBuilder(tableName).addColumn(new ColumnBuilder("ID", DataType.INT)).addColumn(new ColumnBuilder("Foo", DataType.TEXT)).addColumn(new ColumnBuilder("Bar", DataType.TEXT)).toTable(db);

        table.addRow(Integer.valueOf(0), "foo", "bar");
        table.addRow(Integer.valueOf(1), "foo1", "bar1");
        table.addRow(Integer.valueOf(2), "foo2", "bar2");
    }

    /**
     * Tear down, cleans up temp files.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @AfterClass
    public static void tearDown() throws IOException {

        File tempFiles = new File("target/" + databaseName);
        File databaseFile = new File(databaseLocation);
        if (tempFiles.exists()) {
            FileUtils.deleteDirectory(tempFiles);
            FileUtils.deleteQuietly(databaseFile);
        }
    }

    @Test
    public void testDumpingOfDatabase() throws IOException {

        ArrayList<String> expectedLines = new ArrayList<>();
        ArrayList<String> actualLines = new ArrayList<>();

        AccessDatabaseDumper dumper = new AccessDatabaseDumper(databaseLocation);
        dumper.writeTablesToFile("target/");
        File actualTsv = new File("target/" + databaseName + "/" + tableName + ".tsv");
        File expectedTsv = new File("src/test/resources/databaseDumperExpected.tsv");
        BufferedReader br = new BufferedReader(new FileReader(expectedTsv));
        String line;
        while ((line = br.readLine()) != null) {
            expectedLines.add(line);
        }
        br.close();

        br = new BufferedReader(new FileReader(actualTsv));
        while ((line = br.readLine()) != null) {
            expectedLines.add(line);
        }

        int count = 0;
        for (String actual : actualLines) {
            Assert.assertEquals(expectedLines.get(count), actual);
            count++;
        }

        br.close();

    }
}
