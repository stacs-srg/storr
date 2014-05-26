package uk.ac.standrews.cs.digitising_scotland.population_model.tools;

import uk.ac.standrews.cs.digitising_scotland.population_model.database.DBInitialiser;
import uk.ac.standrews.cs.nds.util.Diagnostic;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Sets up a database ready to contain population data, overwriting any existing data.
 *
 * @author Ilia Shumailov (is33@st-andrews.ac.uk)
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class InitialiseDB {

    /**
     * Sets up a database ready to contain population data.
     *
     * @param args ignored
     * @throws java.io.IOException
     */
    public static void main(final String[] args) throws IOException, SQLException {

        new DBInitialiser().setupDB();

        Diagnostic.traceNoSource("Database initialised");
    }
}
