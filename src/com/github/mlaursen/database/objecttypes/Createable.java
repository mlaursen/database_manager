package com.github.mlaursen.database.objecttypes;

public interface Createable extends NoCursor {
	/**
	 * Creates an DatabaseObect in the database
	 * @return boolean of if the create procedure successfully created at least 1 row
	 */
	boolean create();
}
