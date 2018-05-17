package org.lihao.demo.core;

import org.flywaydb.core.Flyway;

import java.io.IOException;
import java.util.Properties;

public class DemoCoreFlywayInit {

    public static void initDatabase() throws Exception{
	    // Create the Flyway instance
	    Flyway flyway = new Flyway();
	    // set configure
	    Properties property = new Properties();
	    try {
		    property.load(DemoCoreFlywayInit.class.getClassLoader().getSystemResourceAsStream("./db/flyway.conf"));
	    } catch (IOException e) {
		    throw e;
	    }
	    flyway.configure(property);
	    // Start the migration
	    flyway.migrate();
    }

	public static void main(String[] args) throws Exception{
		DemoCoreFlywayInit.initDatabase();
	}
}