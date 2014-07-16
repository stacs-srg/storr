package uk.ac.standrews.cs.digitising_scotland.tools.fileutils;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

public class AccessDatabaseReader {

    Database db;

    public AccessDatabaseReader(String databaseLocation) throws IOException {

        File dbFile = new File(databaseLocation);
        db = Database.create(dbFile);
        Set<String> tablesNames = db.getTableNames();
        for (String string : tablesNames) {
            Table table = db.getTable(string);
            System.out.println("tabel: " + table.display());
        }

        System.out.println(db.toString());
    }

    public static void main(String[] args) throws IOException {

        AccessDatabaseReader reader = new AccessDatabaseReader("db.mdb");
    }
}
