package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBBackedPersonFactory {

    public static DBBackedPerson createDBBackedPerson(final Connection connection, final ResultSet result_set) throws SQLException {

        return new DBBackedPerson(connection, result_set.getInt(PopulationProperties.PERSON_FIELD_ID),
                result_set.getString(PopulationProperties.PERSON_FIELD_NAME),
                result_set.getString(PopulationProperties.PERSON_FIELD_SURNAME),
                result_set.getString(PopulationProperties.PERSON_FIELD_GENDER).charAt(0),
                result_set.getDate(PopulationProperties.PERSON_FIELD_BIRTH_DATE),
                result_set.getDate(PopulationProperties.PERSON_FIELD_DEATH_DATE),
                result_set.getString(PopulationProperties.PERSON_FIELD_OCCUPATION),
                result_set.getString(PopulationProperties.PERSON_FIELD_CAUSE_OF_DEATH),
                result_set.getString(PopulationProperties.PERSON_FIELD_ADDRESS));
    }

    public static DBBackedPerson createDBBackedPerson(final Connection connection, final int person_id) throws SQLException {

        final PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + PopulationProperties.DATABASE_NAME + "." + PopulationProperties.PERSON_TABLE_NAME + " WHERE id = ?");
        statement.setInt(1, person_id);
        final ResultSet resultSet = statement.executeQuery();

        if (!resultSet.first()) {
            throw new SQLException("can't find person id: " + person_id);
        }
        return createDBBackedPerson(connection, resultSet);
    }
}
