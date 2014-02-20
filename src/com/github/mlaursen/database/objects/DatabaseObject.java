package com.github.mlaursen.database.objects;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mlaursen.annotations.DatabaseField;
import com.github.mlaursen.annotations.DatabaseFieldType;
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
	@DatabaseField(values={DatabaseFieldType.GET, DatabaseFieldType.UPDATE})
	protected String primaryKey;
	protected String primaryKeyName = "id";
	/**
	 * This is mostly used to access the ObjectManager to do Database
	 * calls
	 */
	public DatabaseObject() { }
	/**
	 * Create a database object by giving it a primary key and then
	 * searching for it in the database.  It then calls all the setters
	 * for that database object where the setter has a MyResultRow as
	 * the only parameter
	 * @param primaryKey
	 */
	public DatabaseObject(String primaryKey) {
		init(primaryKey);
	}
	
	/**
	 * Create a database object by giving it a primary key and then
	 * searching for it in the database.  It then calls all the setters
	 * for that database object where the setter has a MyResultRow as
	 * the only parameter
	 * @param primaryKey
	 */
	public DatabaseObject(Integer primaryKey) {
		this(primaryKey.toString());
	}
	
	/**
	 * Sets the primary key to the database column 
	 * described as the primaryKeyName.  The default is 'id'
	 * @param r
	 */
	public DatabaseObject(MyResultRow r) {
		setAll(r);
	}
	
	/**
	 * 
	 * @param primaryKey
	 */
	protected void init(String primaryKey) {
		MyResultRow r = manager.getFirstRowFromCursorProcedure("get", primaryKey);
		setAll(r);
	}
	
	/**
	 * This finds all the methods that start with 'set'
	 * and have a single parameter of a MyResultRow and then invokes
	 * that method.
	 * @param r
	 */
	protected void setAll(MyResultRow r) {
		Method[] methods = this.getClass().getMethods();
		for(Method m : methods) {
			if(m.getName().startsWith("set") && Arrays.asList(m.getParameterTypes()).contains(MyResultRow.class)) {
				try {
					m.invoke(this, r);
				}
				catch(InvocationTargetException | IllegalAccessException | IllegalArgumentException e) {
					System.err.println("There was a problem trying to invoke " + m.getName());
				}
			}
		}
	}
	
	/**
	 * 
	 * @param primaryKey
	 */
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	/**
	 * 
	 * @param primaryKey
	 */
	public void setPrimaryKey(Integer primaryKey) {
		this.primaryKey = primaryKey.toString();
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
	
	
	private Object[] getParameters(DatabaseFieldType proc) {
		System.out.println("here");
		return getParameters(getParameterMapTail(proc));
	}
	private Object[] getParameters(Map<Integer, Object> map) {
		int s = map.size();
		Object[] ps = new Object[s];
		for(int i = 0; i < s; i++) {
			ps[i] = map.get(i);
		}
		return ps;
	}
	
	private Map<Integer, Object> getParameterMapTail(DatabaseFieldType proc) {
		return getParameterMapTail(proc, new HashMap<Integer, Object>(), this.getClass());
	}
	
	private Map<Integer, Object> getParameterMapTail(DatabaseFieldType proc, Map<Integer, Object> ps, Class<?> c) {
		List<Class<?>> supers = new ArrayList<Class<?>>();
		Class<?> c2 = c;
		while(!c2.equals(Object.class)) {
			supers.add(c2);
			c2 = c2.getSuperclass();
		}
		System.out.println("Supers: " + supers);
		return ps;
	}
	/*
	private Map<Integer, Object> getParametersMap(DatabaseFieldType proc) {
		return getParametersMap(proc, new HashMap<Integer, Object>(), this.getClass());
	}
	
	private void addFieldsToParameterMap(DatabaseFieldType proc, Map<Integer, Object> ps, Class<?> c) {
		for(Field f : c.getDeclaredFields()) {
			if(f.isAnnotationPresent(DatabaseField.class)) {
				DatabaseField a = f.getAnnotation(DatabaseField.class);
				if(Arrays.asList(a.values()).contains(proc)) {
					try {
						Object o = f.get(this);
						int pos = -1;
						if(proc.equals(DatabaseFieldType.GET))
							pos = a.getPosition();
						else if(proc.equals(DatabaseFieldType.GETALL))
							pos = a.getAllPosition();
						else if(proc.equals(DatabaseFieldType.CREATE))
							pos = a.createPosition();
						else if(proc.equals(DatabaseFieldType.DELETE))
							pos = a.deletePosition();
						else if(proc.equals(DatabaseFieldType.FILTER))
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
	}
	
	private Map<Integer, Object> getParametersMap(DatabaseFieldType proc, Map<Integer, Object> ps, Class<?> c) {
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
							if(proc.equals(DatabaseFieldType.GET))
								pos = a.getPosition();
							else if(proc.equals(DatabaseFieldType.GETALL))
								pos = a.getAllPosition();
							else if(proc.equals(DatabaseFieldType.CREATE))
								pos = a.createPosition();
							else if(proc.equals(DatabaseFieldType.DELETE))
								pos = a.deletePosition();
							else if(proc.equals(DatabaseFieldType.FILTER))
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
	 * This is a implementation for using generics if you need a specific Database Object
	 * sub class
	 * @param primaryKey
	 * @param type	Sub class to cast to
	 * @return
	 */
	public <T extends DatabaseObject> T get(String primaryKey, Class<T> type) {
		return manager.executeCursorProcedure("get", primaryKey).getRow().construct(type);
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
		Object[] params = getParameters(DatabaseFieldType.GETALL);
		return (List<DatabaseObject>) manager.executeCursorProcedure("getall", params).toListOf(this.getClass());
	}
	
	/**
	 * This is a implementation for using generics if you need a specific list of Database Object
	 * sub class
	 * @param primaryKey
	 * @param type	Sub class to cast to
	 * @return
	 */
	public <T extends DatabaseObject> List<T> getAll(Class<T> type) {
		Object[] params = getParameters(DatabaseFieldType.GETALL);
		return manager.executeCursorProcedure("getall", params).toListOf(type);
	}
	
	/**
	 * Default implementation for a DatabaseObject that is createable.
	 * 
	 * @return
	 */
	public boolean create() {
		Object[] params = getParameters(DatabaseFieldType.CREATE);
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
	 * This is a implementation for using generics if you need a specific Database Object
	 * sub class
	 * @param type	Sub class to cast to 
	 * @param filterBy	The objects to filter the result set with
	 * @return
	 */
	public <T extends DatabaseObject> List<T> filter(Class<T> type, Object... filterBy) {
		return manager.executeCursorProcedure("filter", filterBy).toListOf(type);
	}
	
	/**
	 * Default implementation for a updateable database object
	 * @return
	 */
	public boolean update() {
		Object[] params = getParameters(DatabaseFieldType.UPDATE);
		return manager.executeStoredProcedure("update", params);
	}
	
	/**
	 * Default implementation for a deleteable database object.
	 * @return
	 */
	public boolean delete() {
		return manager.executeStoredProcedure("delete", primaryKey);
	}
	
	

	/**
	 * This is the default toString
	 */
	@Override
	public String toString() {
		return "DatabaseObject [primaryKey=" + primaryKey + ", primaryKeyName=" + primaryKeyName + "]";
	}
}
