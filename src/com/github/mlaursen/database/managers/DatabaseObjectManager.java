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
import com.github.mlaursen.database.objects.Package;
import com.github.mlaursen.database.objecttypes.Createable;
import com.github.mlaursen.database.objecttypes.Deleteable;
import com.github.mlaursen.database.objecttypes.Filterable;
import com.github.mlaursen.database.objecttypes.GetAllable;
import com.github.mlaursen.database.objecttypes.Getable;
import com.github.mlaursen.database.objecttypes.Updateable;
import com.github.mlaursen.database.utils.ClassUtil;
/**
 * 
 * 
 * @author mikkel.laursen
 *
 */
public class DatabaseObjectManager {

	protected ConnectionManager connectionManager;
	protected List<Package> packages = new ArrayList<Package>();
	protected Map<String, Integer> packageMap = new HashMap<String, Integer>();
	protected List<String> availablePackages = new ArrayList<String>();
	protected List<Class<? extends DatabaseObject>> databaseObjects = new ArrayList<Class<? extends DatabaseObject>>();
	
	@SafeVarargs
	public DatabaseObjectManager(Class<? extends DatabaseObject>... databaseObjects) {
		connectionManager = new ConnectionManager();
		for(Class<? extends DatabaseObject> c : databaseObjects) {
			addPackage(c);
		}
	}
	
	
	/**
	 * Adds a new package or merges a package based on the class given.
	 * @param c
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
	 * Adds a package to the List of packages.
	 * It also updates the availablePackages list and the packageMap
	 * @param pkg
	 */
	public void addPackage(Package pkg) {
		packages.add(pkg);
		availablePackages.add(pkg.getName());
		packageMap.put(pkg.getName(), packages.size()-1);
	}
	
	/**
	 * Checks if the availablePackages list contains the package Name
	 * @param pkgName
	 * @return
	 */
	public boolean packageIsAvailable(String pkgName) { 
		return this.availablePackages.contains(pkgName);
	}
	
	/**
	 * Checks if a package is avaialbe by checking the availabePackages for the formatted 
	 * Class name
	 * @param type
	 * @return
	 */
	public <T extends DatabaseObject>  boolean packageIsAvailable(Class<T> type) {
		return packageIsAvailable(Package.formatClassName(type));
	}
	
	/**
	 * Returns a package for the DatabaseObject class given.
	 * @param type
	 * @return
	 */
	public <T extends DatabaseObject> Package getPackage(Class<T> type) {
		return getPackage(Package.formatClassName(type));
	}
	
	/**
	 * Returns a package for the package name given
	 * @param pkgName
	 * @return
	 */
	public Package getPackage(String pkgName) {
		return packages.get(packageMap.get(pkgName));
	}
	
	/**
	 * Executes a custom get procedure. This will return a single object and construct it
	 * to the type given.
	 * @param procedureName Custom procedure name
	 * @param type			The Database object find the package for and to return the object as
	 * @param params		Any parameters to help get the database object
	 * @return
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
	 * 
	 * @param procedureName
	 * @param type
	 * @param params
	 * @return
	 */
	public <T extends DatabaseObject> List<T> executeCustomGetAllProcedure(String procedureName, Class<T> type, Object... params) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(pkg.canCallProcedure(procedureName)) {
				return connectionManager.executeCursorProcedure(pkg, procedureName, params).toListOf(type);
			}
		}
		return null;
	}
	
	/**
	 * Executes a custom procedure that modifies the database instead of returning a database object
	 * @param procedureName
	 * @param type
	 * @param params
	 * @return
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
	 * 
	 * @param primaryKey
	 * @param type
	 * @return
	 */
	public <T extends DatabaseObject> T get(Integer primaryKey, Class<T> type) {
		return get(primaryKey.toString(), type);
	}
	
	/**
	 * Returns a signle Database Object by searchign for the primary key
	 * @param primaryKey
	 * @param type
	 * @return
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
	 * Returns  alist of database objects for the given Database Object type
	 * @param type
	 * @return
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
	 * Returns a list of Database Object for the object given.
	 * @param object
	 * @return
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
	 * Creates a database object in the database
	 * @param object
	 * @return
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
	 * Updates an object in the database.
	 * @param object
	 * @return
	 */
	public <T extends DatabaseObject> boolean update(T object) {
		if(packageIsAvailable(object.getClass())) {
			Package pkg = getPackage(object.getClass());
			if(canCallProcedure(object.getClass(), Updateable.class, pkg, "update")) {
				Object[] params = getParameters(DatabaseFieldType.UPDATE, object);
				return connectionManager.executeStoredProcedure(pkg, "update"+ object.getClass().getSimpleName().replace("View", ""), params);
			}
		}
		return false;
	}
	
	/**
	 * Deletes a Database Object from the database.
	 * It checks if there is a package for the database object and if that package
	 * includes a delete procedure.  If it does and the primary key is not null, the stored
	 * procedure is executed.
	 * @param object	The object to delete from the database
	 * @return
	 */
	public <T extends DatabaseObject> boolean delete(T object) {
		return delete(object.getPrimaryKey(), object.getClass());
	}
	
	/**
	 * Deletes an object by primary key
	 * @param primaryKey	A string for the primary key. If the primary key is an integer, it will be converted
	 * @param type	The DatabaseObject class that you want to delete by primary key
	 * @return
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
	 * Filters a result set by the objects given.
	 * This is for limiting the results. each object in the filterBy is pretty much a
	 * WHERE X=filterBy AND ....
	 * @param type
	 * @param filterBy
	 * @return
	 */
	public <T extends DatabaseObject> List<T> filter(Class<T> type, Object... filterBy) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(canCallProcedure(type, Filterable.class, pkg, "filter")) {
				return (List<T>) connectionManager.executeCursorProcedure(pkg, "filter", filterBy).toListOf(type);
			}
		}
		return new ArrayList<T>();
	}
	
	
	/**
	 * Get an array of Object to be passed to a database procedure call. The
	 * array is generated by seraching for all the DatabaseField or
	 * MultipleDatabaseField annotations located in the class starting with the
	 * DatabaseObject and working down to the current class.
	 * 
	 * @param proc
	 *            The procedure to get the parameters for
	 * @return
	 */
	private <T extends DatabaseObject> Object[] getParameters(DatabaseFieldType proc, T object) {
		return getParameters(getParametersMap(proc, object));
	}

	/**
	 * Takes a Map of interger position and objects and sorts them from 0 - max
	 * size of map into an array of objects
	 * 
	 * @param map
	 * @return
	 */
	private Object[] getParameters(Map<Integer, Object> map) {
		int s = map.size();
		Object[] ps = new Object[s];
		for (int i = 0; i < s; i++) {
			ps[i] = map.get(i);
		}
		return ps;
	}

	/**
	 * Takes in a DatabaseFieldType and generates a Map of Integer, Object pairs
	 * to be passed to the database stored procedure.
	 * 
	 * @param proc
	 * @return
	 */
	private <T extends DatabaseObject> Map<Integer, Object> getParametersMap(DatabaseFieldType proc, T object) {
		int counter = 0;
		Map<Integer, Object> params = new HashMap<Integer, Object>();
		List<Class<?>> classes = ClassUtil.getClassList(object.getClass());
		for (Class<?> c : classes) {
			for (Field f : c.getDeclaredFields()) {
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
							catch (IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
							catch (Exception e) {
								String err = "The position for the procedure '" + proc + "' has not been initialized for the field " + "["
										+ f.getName() + "]\nin class [" + c.getName() + "].  This error occured when seraching for the values "
										+ "to add when calling the stored procedure. The value has not been added to the parameter map.";
								System.err.println(err);
							}
						}
					}
				}
				else if (f.isAnnotationPresent(DatabaseField.class)) {
					DatabaseField a = f.getAnnotation(DatabaseField.class);
					if (Arrays.asList(a.values()).contains(proc)) {
						try {
							Object o = f.get(object);
							int pos = a.reorder() ? DatabaseFieldType.getPosition(proc, a) : counter;
							counter++;
							if (pos == -1) {
								throw new Exception();
							}
							else {
								params.put(pos, o);
							}
						}
						catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
						catch (Exception e) {
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

	public <T extends DatabaseObject> boolean canCallProcedure(Class<T> type, Class<?> procedure, Package pkg, String procedureName) {
		return ClassUtil.isClassCallable(type, procedure) && pkg.canCallProcedure(procedureName);
	}

	@Override
	public String toString() {
		String s = "DatabaseObjectManager [\n\tpackages=[";
		for(Package p : packages) {
			s += "\n\t\tpackage=" + p.toString();
		}
		return s + "\n\t]";
	}
}
