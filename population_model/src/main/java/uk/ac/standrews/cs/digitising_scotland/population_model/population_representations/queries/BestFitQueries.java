/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.population_model.population_representations.queries;

import java.sql.Connection;
import java.sql.SQLException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.AbstractPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.database.DBConnector;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.database.DBPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;


/**
 * Provides methods to make block queries to database.
 * 
 * @author Tom Dalton
 */
public class BestFitQueries {
	
	private Connection connection = null;
    
	public static void main(String[] args) {
		BestFitQueries bestFit = new BestFitQueries();
		AbstractPerson person = bestFit.getPerson(1);
		System.out.println(person.getPartnerships().get(0));
	}

	public BestFitQueries() {

        try {
        	connection = new DBConnector(PopulationProperties.getDatabaseName()).createConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        
        
	}
	
	public AbstractPerson getPerson(int person_id) {
		DBPerson p = null;
		try {
			p = new DBPerson(connection, 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return p;
	}
	
	public int getPartnerOf(int person_id) {
		
		return 0;
	}

	public int getFatherOf(int person_id) {

		return 0;
	}

	public int getMotherOf(int person_id) {

		return 0;
	}

	public int[] getChildrenOf(int person_id) {

		return new int[0];
	}

	public int[] getChildrenOf(int father_id, int mother_id) {

		return new int[0];
	}


}
