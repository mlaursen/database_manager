package com.github.mlaursen.database.objects;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * A representation of a database row
 * 
 * @author mlaursen
 * 
 */
public class MyResultRow {
	private Map<String, String> row;

	public MyResultRow() {
		this.row = new HashMap<String, String>();
	}

	/**
	 * Adds a column and it's value to the results
	 * 
	 * @param column
	 *            The column name
	 * @param value
	 *            The column value
	 */
	public void add(String column, String value) {
		this.row.put(column, value);
	}

	/**
	 * Returns a string from the row
	 * 
	 * @param column
	 *            The column name to get the value from
	 * @return A string
	 */
	public String get(String column) {
		String col = this.row.get(column.toLowerCase());
		return col == null ? this.row.get("test_" + column.toLowerCase()) : col;
	}

	/**
	 * Attempts to get an integer from the row Returns 0 if it can not be parsed
	 * as an integer or it is null
	 * 
	 * @param column
	 *            The column name
	 * @return An integer from the database or 0 if it was null or not a number
	 */
	public int getInt(String column) {
		try {
			return Integer.parseInt(get(column));
		}
		catch (NumberFormatException | NullPointerException e) {
			return 0;
		}
	}

	/**
	 * Attempts to get a double from the row. Returns 0.0 if it can not be
	 * parsed as a double or it is null
	 * 
	 * @param column
	 *            The column name
	 * @return A double from the database or 0.00 if it was null or not a number
	 */
	public double getDouble(String column) {
		try {
			return Double.parseDouble(get(column));
		}
		catch (NullPointerException | NumberFormatException e) {
			return 0;
		}
	}

	public String toString() {
		return this.row.toString();
	}

	/**
	 * Calls the constructor for a Database Object using a MyResultRow
	 * 
	 * @param type
	 *            The database object class to construct
	 * @return A database object or null
	 */
	public <T extends DatabaseObject> T construct(Class<T> type) {
		try {
			return type.cast(type.getConstructor(MyResultRow.class).newInstance(this));
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
}
