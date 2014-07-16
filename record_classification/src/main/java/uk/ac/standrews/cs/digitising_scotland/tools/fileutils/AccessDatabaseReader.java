package uk.ac.standrews.cs.digitising_scotland.tools.fileutils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

/**
 * The Class AccessDatabaseReader can be used to read an access database file (*.mdb) and dump it to csv.
 */
public class AccessDatabaseReader {

    /** The db. */
    private Database db;

    /**
     * Instantiates a new access database reader.
     *
     * @param databaseLocation the database location
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public AccessDatabaseReader(final String databaseLocation) throws IOException {

        File dbFile = new File(databaseLocation);
        db = Database.create(dbFile);
        Set<String> tablesNames = db.getTableNames();
        for (String string : tablesNames) {
            Table table = db.getTable(string);
            System.out.println("tabel: " + table.display());
        }

        System.out.println(db.toString());
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void main(final String[] args) throws IOException {

        AccessDatabaseReader reader = new AccessDatabaseReader("db.mdb");
    }
}
