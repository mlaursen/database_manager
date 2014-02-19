package com.github.mlaursen.database.objects;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mlaursen.annotations.DatabaseAnnotationType;
import com.github.mlaursen.annotations.DatabaseField;
import com.github.mlaursen.database.ObjectManager;

/**
 * Basic outline for a DatbaseObject.
 * Every database object must have at least a primary key
 * 
 * @author mikkel.laursen
 *
 */
public abstract class DatabaseObject {
	
	protected final ObjectManager manager = createManager();
	@DatabaseField(values=DatabaseAnnotationType.GET, getPosition=0)
	protected String primaryKey;
	protected String primaryKeyName = "id";
	/**
	 * This is mostly used to access the ObjectManager to do Database
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
	
	@Override
	public String toString() {
		return "DatabaseObject [primaryKey=" + primaryKey + ", primaryKeyName=" + primaryKeyName + "]";
	}
	
	/**
	 * Creates a manager for the database object.
	 * {@link com.github.mlaursen.database.ObjectManager}
	 * Basic implementation is:
	 * @return new ObjectManager(this.getClass());
	 */
	protected ObjectManager createManager() {
		return new ObjectManager(this.getClass());
	}
	
	public String getDatabaseManagerToString() {
		return manager.toString();
	}
	
	
	private Object[] getParameters(DatabaseAnnotationType proc) {
		return getParameters(getParametersMap(proc));
	}
	private Object[] getParameters(Map<Integer, Object> map) {
		int s = map.size();
		Object[] ps = new Object[s];
		for(int i = 0; i < s; i++) {
			ps[i] = map.get(i);
		}
		return ps;
	}
	
	private Map<Integer, Object> getParametersMap(DatabaseAnnotationType proc) {
		return getParametersMap(proc, new HashMap<Integer, Object>(), this.getClass());
	}
	
	private Map<Integer, Object> getParametersMap(DatabaseAnnotationType proc, Map<Integer, Object> ps, Class<?> c) {
		if(c.equals(Object.class)) {
			return ps;
		}
		else {
			for(Field f : c.getDeclaredFields()) {
				if(f.isAnnotationPresent(DatabaseField.class)) {
					DatabaseField a = f.getAnnotation(DatabaseField.class);
					if(Arrays.asList(a.values()).contains(proc)) {
						try {
							Object o = f.get(this);
							int pos = -1;
							if(proc.equals(DatabaseAnnotationType.GET))
								pos = a.getPosition();
							else if(proc.equals(DatabaseAnnotationType.GETALL))
								pos = a.getAllPosition();
							else if(proc.equals(DatabaseAnnotationType.CREATE))
								pos = a.createPosition();
							else if(proc.equals(DatabaseAnnotationType.DELETE))
								pos = a.deletePosition();
							else if(proc.equals(DatabaseAnnotationType.FILTER))
								pos = a.filterPosition();
							if(pos == -1) {
								throw new Exception();
							}
							else {
								ps.put(pos, o);
							}
						}
						catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
						catch (Exception e) {
							System.err.println("The position for " + proc + " has not been initialized.");
							System.err.println("This field[" + f.getName() + "]'s value was not added to the results");
						}
					}
				}
			}
			return getParametersMap(proc, ps, c.getSuperclass());
		}
	}
	
	/**
	 * This is a default implementation for the Getable Interface get Method.
	 * If the database object is not Getable, it will return null
	 * @param primaryKey The primary key to search for in the database
	 * @return Either a null DatabaseObject or a result DatabaseObjet
	 */
	public DatabaseObject get(String primaryKey) {
		return manager.executeCursorProcedure("get", primaryKey).getRow().construct(this.getClass());
	}
	
	/**
	 * This is a default implementation for the GetAllable Interface 'getAll' method.
	 * If the database object i not GetAllable, it will return an empty list.
	 * If you want an additional key to be applied to the getAll method,
	 * add the Annotation @DatabaseField(values={DatabaseField.GETALL}, position={$positionToBeApplied})
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DatabaseObject> getAll() {
		Object[] params = getParameters(DatabaseAnnotationType.GETALL);
		return (List<DatabaseObject>) manager.executeCursorProcedure("getall", params).toListOf(this.getClass());
	}
	
	/**
	 * Default implementation for a DatabaseObject that is createable.
	 * 
	 * @return
	 */
	public boolean create() {
		Object[] params = getParameters(DatabaseAnnotationType.CREATE);
		return manager.executeStoredProcedure("create", params);
	}
	
	/**
	 * Default implmenetation for a database object that is filterable.
	 * 
	 * @param filterBy Array of objects to filter the query by
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DatabaseObject> filter(Object... filterBy) {
		return (List<DatabaseObject>) manager.executeCursorProcedure("fiter", filterBy).toListOf(this.getClass());
	}
	
	/**
	 * Default implementation for a updateable database object
	 * @return
	 */
	public boolean update() {
		Object[] params = getParameters(DatabaseAnnotationType.UPDATE);
		return manager.executeStoredProcedure("update", params);
	}
	
	/**
	 * Default implementation for a deleteable database object.
	 * @return
	 */
	public boolean delete() {
		return manager.executeStoredProcedure("delete", primaryKey);
	}
}
