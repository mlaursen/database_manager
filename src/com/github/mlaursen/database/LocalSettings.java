/**
 * 
 */
package com.github.mlaursen.database;

import java.io.IOException;
import java.util.Properties;

/**
 * Reads the local settings for a database.
 * The properties file must have: with examples
 * 	className=oracle.jdbc.OracleDriver
 *  dbpswd=welcome1
 *  database=jdbc\:oracle\:thin\:@localhost\:1521\:xe
 *  dbuser=testuser
 * 
 * 
 * @author mikkel.laursen
 *
 */
public class LocalSettings {
	private Properties properties = new Properties();
	private String propertiesSource;
	public static final String DATABASE_NAME = "database", USERNAME = "dbuser"
							 , PASSWORD="dbpswd", CLASS_FOR_NAME="className";
	public static final String DEFAULT_PROPERTIES_SOURCE = "/dbconfig.properties";
	public LocalSettings() {
		propertiesSource = DEFAULT_PROPERTIES_SOURCE;
	}
	
	/**
	 * Changes the source url to the given source url
	 * @param pSource
	 */
	public void setpropertiesSource(String pSource) {
		propertiesSource = pSource;
	}
	
	/**
	 * Attempts to load the properties file from the propertiesSource location.
	 * @return
	 * @throws IOException	is thrown if the file does not exist
	 */
	public Properties getLocalSettings() throws IOException {
		properties.load(this.getClass().getResourceAsStream(propertiesSource));
		return properties;
	}
}
