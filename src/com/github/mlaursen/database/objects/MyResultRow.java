package com.github.mlaursen.database.objects;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * A representation of a database row
 * @author mikkel.laursen
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

	public String get(String key) {
		return this.row.get(key);
	}

	public String toString() {
		return this.row.toString();
	}
	
	/**
	 * Calls the constructor for a Database Object using the constructor with a MyResultRow parameter.
	 * 
	 * @param type Class to cast the generic to
	 * @return	A type that has been created for the class type
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
