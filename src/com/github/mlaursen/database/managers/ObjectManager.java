/**
 * 
 */
package com.github.mlaursen.database.managers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mlaursen.annotations.DatabaseField;
import com.github.mlaursen.annotations.DatabaseFieldType;
import com.github.mlaursen.annotations.MultipleDatabaseField;
import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.DatabaseView;
import com.github.mlaursen.database.objects.Package;
import com.github.mlaursen.database.procedures.Createable;
import com.github.mlaursen.database.procedures.Deleteable;
import com.github.mlaursen.database.procedures.Filterable;
import com.github.mlaursen.database.procedures.GetAllable;
import com.github.mlaursen.database.procedures.Getable;
import com.github.mlaursen.database.procedures.Updateable;
import com.github.mlaursen.database.utils.ClassUtil;

/**
 * THe Object Manager class generates the packages and procedure for the database objects given. THe object manager does NOT create these
 * packages and procedures in the database. It is expected that a DBA or someone else has created these packages/procedures.
 * 
 * @author mlaursen
 * 
 */
public class ObjectManager {
	
	protected ConnectionManager connectionManager;
	protected List<Package> packages = new ArrayList<Package>();
	protected Map<String, Integer> packageMap = new HashMap<String, Integer>();
	protected List<String> availablePackages = new ArrayList<String>();
	protected List<Class<? extends DatabaseObject>> databaseObjects = new ArrayList<Class<? extends DatabaseObject>>();
	
	/**
	 * Creates a new connectionManager and generates the packages for all the databaseObjects given
	 * 
	 * @param databaseObjects
	 *            The DatabaseObjects to generate packages for
	 */
	@SafeVarargs
	public ObjectManager(Class<? extends DatabaseObject>... databaseObjects) {
		connectionManager = new ConnectionManager();
		for(Class<? extends DatabaseObject> c : databaseObjects) {
			addPackage(c);
		}
	}
	
	/**
	 * Adds a new package or merges a package based on the class given.
	 * 
	 * @param c
	 *            The database object class
	 */
	public void addPackage(Class<? extends DatabaseObject> c) {
		this.databaseObjects.add(c);
		Package pkg = new Package(c);
		if(packageIsAvailable(pkg.getName())) {
			Package pkgOld = getPackage(pkg.getName());
			pkgOld.mergeProcedures(pkg);
		}
		else {
			this.addPackage(pkg);
		}
	}
	
	/**
	 * Adds a package to the List of packages. It also updates the availablePackages list and the packageMap
	 * 
	 * @param pkg
	 *            The package to add
	 */
	public void addPackage(Package pkg) {
		packages.add(pkg);
		availablePackages.add(pkg.getName());
		packageMap.put(pkg.getName(), packages.size() - 1);
	}
	
	/**
	 * This adds a databaseObject / databaseView pair to the object manager and merges all of their procedures.
	 * 
	 * @param baseClass
	 *            The database object
	 * @param view
	 *            The database view
	 */
	public void addPackageWithView(Class<? extends DatabaseObject> baseClass, Class<? extends DatabaseView> view) {
		Package pkg = new Package(baseClass);
		this.databaseObjects.add(baseClass);
		this.databaseObjects.add(view);
		if(packageIsAvailable(pkg.getName())) {
			Package pkgOld = getPackage(pkg.getName());
			pkgOld.mergeProcedures(pkg);
		}
		else {
			this.addPackage(pkg);
		}
	}
	
	/**
	 * Checks if the availablePackages list contains the package Name
	 * 
	 * @param pkgName
	 *            The String package name
	 * @return True if the package has been created for this manager
	 */
	public boolean packageIsAvailable(String pkgName) {
		return this.availablePackages.contains(pkgName);
	}
	
	/**
	 * Checks if a package is available by checking the availabePackages for the formatted Class name
	 * 
	 * @param type
	 *            The database object class to check if the package is available for
	 * @return True if the package for the class is available
	 */
	public <T extends DatabaseObject> boolean packageIsAvailable(Class<T> type) {
		return packageIsAvailable(Package.formatClassName(type));
	}
	
	/**
	 * Returns a package for the DatabaseObject class given.
	 * 
	 * @param type
	 *            The Database Object class to get the package for
	 * @return The package for the database object. It will be null if it does not exist
	 */
	public <T extends DatabaseObject> Package getPackage(Class<T> type) {
		return getPackage(Package.formatClassName(type));
	}
	
	/**
	 * Returns a package for the package name given
	 * 
	 * @param pkgName
	 *            THe package to look up
	 * @return A package or null
	 */
	public Package getPackage(String pkgName) {
		return packages.get(packageMap.get(pkgName));
	}
	
	/**
	 * Executes a custom get procedure. This will return a single object and construct it to the type given.
	 * 
	 * @param procedureName
	 *            Custom procedure name
	 * @param type
	 *            The Database object find the package for and to return the object as
	 * @param params
	 *            Any parameters to help get the database object
	 * @return A DatabaseObject or null
	 */
	public <T extends DatabaseObject> T executeCustomGetProcedure(String procedureName, Class<T> type, Object... params) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(pkg.canCallProcedure(procedureName)) {
				return connectionManager.executeCursorProcedure(pkg, procedureName, params).getRow().construct(type);
			}
		}
		return null;
	}
	
	/**
	 * Calls a custom GetAll proceudre. This is for returning a list of DatabaseObjects versus a single database object
	 * 
	 * @param procedureName
	 *            The procedure to call
	 * @param type
	 *            The database object type to call the procedure from
	 * @param params
	 *            The optional parameters to pass to the procedure
	 * @return A list of DatabaseObjects or an empty List
	 */
	public <T extends DatabaseObject> List<T> executeCustomGetAllProcedure(String procedureName, Class<T> type, Object... params) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(pkg.canCallProcedure(procedureName)) {
				return connectionManager.executeCursorProcedure(pkg, procedureName, params).toListOf(type);
			}
		}
		return new ArrayList<T>();
	}
	
	/**
	 * Executes a custom procedure that modifies the database instead of returning a database object
	 * 
	 * @param procedureName
	 *            The procedure to call
	 * @param type
	 *            The database object to modify
	 * @param params
	 *            The optional parameters to pass to the procedure
	 * @return A boolean of if at least 1 row was modified in the database
	 */
	public <T extends DatabaseObject> boolean executeCustomProcedure(String procedureName, Class<T> type, Object... params) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(pkg.canCallProcedure(procedureName)) {
				return connectionManager.executeStoredProcedure(pkg, procedureName, params);
			}
		}
		return false;
	}
	
	/**
	 * Returns a single Database Object by searching for the primary key
	 * 
	 * @param primaryKey
	 *            An integer primaryKey
	 * @param type
	 *            The database object type to search for
	 * @return A possible database object
	 */
	public <T extends DatabaseObject> T get(Integer primaryKey, Class<T> type) {
		return get(primaryKey.toString(), type);
	}
	
	/**
	 * Returns a single Database Object by searching for the primary key
	 * 
	 * @param primaryKey
	 *            An string primaryKey
	 * @param type
	 *            The database object type to search for
	 * @return A possible database object
	 */
	public <T extends DatabaseObject> T get(String primaryKey, Class<T> type) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(canCallProcedure(type, Getable.class, pkg, "get")) {
				return connectionManager.executeCursorProcedure(pkg, "get", primaryKey).getRow().construct(type);
			}
		}
		return null;
	}
	
	/**
	 * Returns a list of database objects for the given Database Object type
	 * 
	 * @param type
	 *            The Database Object class to return a list for
	 * @return a List of database objects or an empty List
	 */
	public <T extends DatabaseObject> List<T> getAll(Class<T> type) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(canCallProcedure(type, GetAllable.class, pkg, "getall")) {
				return connectionManager.executeCursorProcedure(pkg, "getall").toListOf(type);
			}
		}
		return new ArrayList<T>();
	}
	
	/**
	 * Returns a list of Database Object for the object given. This version will also generate a list of parameters to be passed to the
	 * getall procedure.
	 * 
	 * @param object
	 *            The database object to get all for
	 * @return A List of Database Objects or an empty List
	 */
	public <T extends DatabaseObject> List<T> getAll(T object) {
		@SuppressWarnings("unchecked")
		Class<T> c = (Class<T>) object.getClass();
		if(packageIsAvailable(c)) {
			Package pkg = getPackage(c);
			if(canCallProcedure(c, GetAllable.class, pkg, "getall")) {
				Object[] params = getParameters(DatabaseFieldType.GETALL, object);
				return connectionManager.executeCursorProcedure(pkg, "getall", params).toListOf(c);
			}
		}
		return new ArrayList<T>();
	}
	
	/**
	 * Creates a database object in the database. It will retrieve all parameters that have the Annotation DatabaseFieldType.NEW for the
	 * given object. If there are no parameters, it will only insert the primary key for the given object.
	 * 
	 * @param object
	 *            The object to create.
	 * @return True if at least 1 object was inserted into the database
	 */
	public <T extends DatabaseObject> boolean create(T object) {
		if(packageIsAvailable(object.getClass())) {
			Package pkg = getPackage(object.getClass());
			if(canCallProcedure(object.getClass(), Createable.class, pkg, "new")) {
				Object[] params = getParameters(DatabaseFieldType.NEW, object);
				if(params.length == 0) {
					return connectionManager.executeStoredProcedure(pkg, "new", object.getPrimaryKey());
				}
				else {
					return connectionManager.executeStoredProcedure(pkg, "new", params);
				}
			}
		}
		return false;
	}
	
	/**
	 * Updates an object in the database. It will generate an array of parameters for the given object and pas them to the procedure
	 * 
	 * @param object
	 *            The object to update
	 * @return True if at least 1 row was updated in the database
	 */
	public <T extends DatabaseObject> boolean update(T object) {
		@SuppressWarnings("unchecked")
		Class<T> c = (Class<T>) object.getClass();
		String update = "update" + c.getSimpleName().replace("View", "");
		if(packageIsAvailable(object.getClass())) {
			Package pkg = getPackage(object.getClass());
			if(canCallProcedure(object.getClass(), Updateable.class, pkg, update)) {
				Object[] params = getParameters(DatabaseFieldType.UPDATE, object);
				return connectionManager.executeStoredProcedure(pkg, update, params);
			}
		}
		return false;
	}
	
	/**
	 * Deletes a Database Object from the database. It checks if there is a package for the database object and if that package includes a
	 * delete procedure. If it does and the primary key is not null, the stored procedure is executed.
	 * 
	 * @param object
	 *            The object to delete from the database
	 * @return True if at least 1 row was deleted from the database
	 */
	public <T extends DatabaseObject> boolean delete(T object) {
		return delete(object.getPrimaryKey(), object.getClass());
	}
	
	/**
	 * Deletes an object by primary key
	 * 
	 * @param primaryKey
	 *            A string for the primary key. If the primary key is an integer, it will be converted
	 * @param type
	 *            The DatabaseObject class that you want to delete by primary key
	 * @return True if at least 1 row was deleted from the database
	 */
	public <T extends DatabaseObject> boolean delete(String primaryKey, Class<T> type) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(canCallProcedure(type, Deleteable.class, pkg, "delete")) {
				return connectionManager.executeStoredProcedure(pkg, "delete", primaryKey);
			}
		}
		return false;
	}
	
	/**
	 * Filters a result set by the objects given. This is for limiting the results. each object in the filterBy is pretty much a WHERE
	 * X=filterBy AND ....
	 * 
	 * @param type
	 *            The database object to filter
	 * @param filterBy
	 *            The optional parameters to pass to the filter procedure
	 * @return A List of DatabaseObjects or an empty List
	 */
	public <T extends DatabaseObject> List<T> filter(Class<T> type, Object... filterBy) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(canCallProcedure(type, Filterable.class, pkg, "filter")) {
				return connectionManager.executeCursorProcedure(pkg, "filter", filterBy).toListOf(type);
			}
		}
		return new ArrayList<T>();
	}
	
	/**
	 * Get an array of Object to be passed to a database procedure call. The array is generated by seraching for all the DatabaseField or
	 * MultipleDatabaseField annotations located in the class starting with the DatabaseObject and working down to the current class.
	 * 
	 * @param proc
	 *            The procedure to get the parameters for
	 * @return An Array of Object Parameters
	 */
	private <T extends DatabaseObject> Object[] getParameters(DatabaseFieldType proc, T object) {
		return getParameters(getParametersMap(proc, object));
	}
	
	/**
	 * Takes a Map of integer position and objects and sorts them from 0 - max size of map into an array of objects
	 * 
	 * @param map
	 *            A Integer, Object pair
	 * @return The map as an array in order
	 */
	private Object[] getParameters(Map<Integer, Object> map) {
		int s = map.size();
		Object[] ps = new Object[s];
		for(int i = 0; i < s; i++) {
			ps[i] = map.get(i);
		}
		return ps;
	}
	
	/**
	 * Takes in a DatabaseFieldType and generates a Map of Integer, Object pairs to be passed to the database stored procedure.
	 * 
	 * @param proc
	 *            The Database Field Type to get the parameters for
	 * @return A Integer, Object pair for the position of the object for the procedure
	 */
	private <T extends DatabaseObject> Map<Integer, Object> getParametersMap(DatabaseFieldType proc, T object) {
		int counter = 0;
		Map<Integer, Object> params = new HashMap<Integer, Object>();
		List<Class<?>> classes = ClassUtil.getClassList(object.getClass());
		for(Class<?> c : classes) {
			for(Field f : c.getDeclaredFields()) {
				f.setAccessible(true);
				if(f.isAnnotationPresent(MultipleDatabaseField.class)) {
					MultipleDatabaseField a = f.getAnnotation(MultipleDatabaseField.class);
					if(Arrays.asList(a.values()).contains(proc)) {
						for(String n : a.names()) {
							try {
								Object o = f.get(object);
								Class<?> oClass = o.getClass();
								String oClassName = oClass.getSimpleName();
								String searchName = n.substring(oClassName.length());
								for(Method m : oClass.getMethods()) {
									String mName = m.getName();
									if(mName.startsWith("get") && mName.matches("(?i)get" + searchName)) {
										Object ret = m.invoke(o);
										params.put(counter, ret);
										counter++;
									}
								}
							}
							catch(IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
							catch(Exception e) {
								String err = "The position for the procedure '" + proc + "' has not been initialized for the field " + "["
										+ f.getName() + "]\nin class [" + c.getName()
										+ "].  This error occured when seraching for the values "
										+ "to add when calling the stored procedure. The value has not been added to the parameter map.";
								System.err.println(err);
							}
						}
					}
				}
				else if(f.isAnnotationPresent(DatabaseField.class)) {
					DatabaseField a = f.getAnnotation(DatabaseField.class);
					if(Arrays.asList(a.values()).contains(proc)) {
						try {
							Object o = f.get(object);
							int pos = a.reorder() ? DatabaseFieldType.getPosition(proc, a) : counter;
							counter++;
							if(pos == -1) {
								throw new Exception();
							}
							else {
								params.put(pos, o);
							}
						}
						catch(IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
						catch(Exception e) {
							String err = "The position for the procedure '" + proc + "' has not been initialized for the field " + "["
									+ f.getName() + "]\nin class [" + c.getName() + "].  This error occured when seraching for the values "
									+ "to add when calling the stored procedure. The value has not been added to the parameter map.";
							System.err.println(err);
						}
					}
				}
				f.setAccessible(false);
			}
		}
		return params;
	}
	
	/**
	 * Checks if a database object can call the procedure. It first checks that the DatabaseObject implements the Procedure interface, and
	 * then checks that the package can call the procedure name given
	 * 
	 * @param type
	 *            The database object
	 * @param procedure
	 *            The Procedure interface
	 * @param pkg
	 *            The package
	 * @param procedureName
	 *            The procedure name to check
	 * @return True if the DatabaseObject implments the procedure interface and the package has the procedure name given
	 */
	public <T extends DatabaseObject> boolean canCallProcedure(Class<T> type, Class<?> procedure, Package pkg, String procedureName) {
		return ClassUtil.isClassCallable(type, procedure) && pkg.canCallProcedure(procedureName);
	}
	
	/**
	 * Renames a package from a given class to the new class. It will format the class with the default regex and default combine with.
	 * 
	 * @param from
	 *            Class to change package for
	 * @param to
	 *            The package name
	 */
	public void renamePackage(Class<? extends DatabaseObject> from, Class<? extends DatabaseObject> to) {
		this.getPackage(from).setName(ClassUtil.formatClassName(to));
	}
	
	@Override
	public String toString() {
		String s = "ObjectManager [\n\tpackages=[";
		for(Package p : packages) {
			s += "\n\t\tpackage=" + p.toString();
		}
		return s + "\n\t]";
	}
}
