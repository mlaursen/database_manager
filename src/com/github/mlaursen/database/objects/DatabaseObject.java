package com.github.mlaursen.database.objects;

import com.github.mlaursen.database.DatabaseObjectManager;
import com.github.mlaursen.database.MyResultRow;

/**
 * Basic outline for a DatbaseObject.
 * Every database object must have at least a primary key
 * 
 * @author mikkel.laursen
 *
 */
public abstract class DatabaseObject {
	
	@SuppressWarnings("rawtypes")
	protected final DatabaseObjectManager manager = createManager();
	protected String primaryKey, primaryKeyName = "id";
	
	/**
	 * This is mostly used to access the DatabaseObjectManager to do Database
	 * calls
	 */
	public DatabaseObject() { }
	public DatabaseObject(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	/**
	 * Sets the primary key to the database column 
	 * described as the primaryKeyName.  The default is 'id'
	 * @param r
	 */
	public DatabaseObject(MyResultRow r) {
		this.primaryKey = r.get(primaryKeyName);
	}
	
	/**
	 * 
	 * @param primaryKey
	 */
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	/**
	 * Sets the primary key to the database column
	 * described as the primaryKeyName. The default is 'id'
	 * @param r
	 */
	public void setPrimaryKey(MyResultRow r) {
		primaryKey = r.get(primaryKeyName);
	}
	
	/**
	 * Get the primaryKey value
	 * @return
	 */
	public String getPrimaryKey() {
		return primaryKey;
	}
	
	/**
	 * Set the primary key name to the new string given.
	 * This will be used for initializing a database object
	 * @param name
	 */
	public void setPrimaryKeyName(String name) {
		primaryKeyName = name;
	}
	
	/**
	 * Returns the primary key name for the database object.
	 * The default is 'id'
	 * @return
	 */
	public String getPrimaryKeyName() {
		return primaryKeyName;
	}
	
	@SuppressWarnings("rawtypes")
	/**
	 * Creates a manager for the database object.
	 * {@link com.github.mlaursen.database.DatabaseObjectManager}
	 * Basic implementation is:
	 * @return new DatabaseObjectManager(this.getClass());
	 */
	protected abstract DatabaseObjectManager createManager();
}
