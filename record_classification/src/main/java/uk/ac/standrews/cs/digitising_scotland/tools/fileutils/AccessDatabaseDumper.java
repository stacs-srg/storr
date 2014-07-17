package uk.ac.standrews.cs.digitising_scotland.tools.fileutils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

/**
 * The Class AccessDatabaseReader can be used to read an access database file (*.mdb) and dump it to tsv files.
 * Each table is written to it's on *.tsv file in a folder with the same name as the partent database.
 */
public class AccessDatabaseDumper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessDatabaseDumper.class);

    /** The database. */
    private Database database;

    /**
     * Instantiates a new access database dumper.
     *
     * @param databaseLocation the database location
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public AccessDatabaseDumper(final String databaseLocation) throws IOException {

        LOGGER.info("Opening " + databaseLocation);
        File dbFile = new File(databaseLocation);
        database = Database.open(dbFile);

    }

    /**
     * Gets the tables names as a string. Each name is on a new line.
     *
     * @return the tables names
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String getTablesNames() throws IOException {

        LOGGER.info("Getting table names...");
        StringBuilder sb = new StringBuilder();
        Set<String> tablesNames = database.getTableNames();
        sb.append("Number of tables: " + tablesNames.size() + "\n");
        for (String tableName : tablesNames) {
            sb.append(tableName + "/n");
        }

        return sb.toString();
    }

    /**
     * Writes each table in the database to file. Each table has its own file with the table name in
     * a folder with the same name as the parent database.
     * @param baseDirectory Base directory in which to create database folder
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void writeTablesToFile(String baseDirectory) throws IOException {

        final String name = database.getFile().getName();
        final int fileExtensionLength = 4;
        File databaseFolder = new File(baseDirectory + name.substring(0, name.length() - fileExtensionLength));
        if (!databaseFolder.mkdirs()) {
            LOGGER.error("Could not create database storage folder");
        }

        Set<String> tablesNames = database.getTableNames();
        for (String tableName : tablesNames) {
            LOGGER.info("Writing " + tableName + " to " + databaseFolder + "/" + tableName + ".tsv");
            Table table = database.getTable(tableName);
            Utils.writeToFile(table.display(), databaseFolder + "/" + tableName + ".tsv");
        }

        LOGGER.info("Completed");
    }

    /**
     * The main method.
     *
     * @param args The location of the database to read and dump. Only the first argument is read.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void main(final String[] args) throws IOException {

        AccessDatabaseDumper reader = new AccessDatabaseDumper(args[0]);
        reader.writeTablesToFile("target/");
    }
}
