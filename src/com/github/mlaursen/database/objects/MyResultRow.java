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

	public void add(String key, String value) {
		this.row.put(key, value);
	}

	/**
	 * Returns a string from the row
	 * @param key
	 * @return
	 */
	public String get(String key) {
		String col = this.row.get(key.toLowerCase());
		return col == null ? this.row.get("test_"+key.toLowerCase()) : col;
	}
	
	/**
	 * Attempts to get an integer from the row
	 * Returns 0 if it can not be parsed as an integer
	 * or it is null
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		try {
			return Integer.parseInt(get(key));
		}
		catch(NumberFormatException | NullPointerException e) {
			return 0;
		}
	}
	
	/**
	 * Attempts to get a double from the row.
	 * Returns 0.0 if it can not be parsed as a double or it is
	 * null
	 * @param key
	 * @return
	 */
	public double getDouble(String key) {
		try {
			return Double.parseDouble(get(key));
		}
		catch(NullPointerException | NumberFormatException e) {
			return 0;
		}
	}

	public String toString() {
		return this.row.toString();
	}

	/**
	 * Calls the constructor for a Database Object using the constructor with a
	 * MyResultRow parameter.
	 * 
	 * @param type
	 *            Class to cast the generic to
	 * @return A type that has been created for the class type
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
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
