/**
 * 
 */
package com.github.mlaursen.database.objects;

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
import com.github.mlaursen.database.ConnectionManager;
import com.github.mlaursen.database.DatabaseObjectClassUtil;
/**
 * @author mikkel.laursen
 *
 */
public class ObjectManager {

	protected ConnectionManager connectionManager;
	protected List<Package> packages = new ArrayList<Package>();
	protected Map<String, Integer> packageMap = new HashMap<String, Integer>();
	protected List<String> availablePackages = new ArrayList<String>();
	protected Class<? extends DatabaseObject>[] databaseObjects;
	
	@SafeVarargs
	public ObjectManager(Class<? extends DatabaseObject>... databaseObjects) {
		connectionManager = new ConnectionManager();
		this.databaseObjects = databaseObjects;
		int i = 0;
		for(Class<? extends DatabaseObject> c : databaseObjects) {
			Package pkg = new Package(c);
			packages.add(pkg);
			availablePackages.add(pkg.getName());
			packageMap.put(pkg.getName(), i);
			i++;
		}
	}
	
	public <T extends DatabaseObject>  boolean packageIsAvailable(Class<T> type) {
		return availablePackages.contains(Package.formatClassName(type));
	}
	
	public <T extends DatabaseObject> Package getPackage(Class<T> type) {
		return packages.get(packageMap.get(Package.formatClassName(type)));
	}
	
	public <T extends DatabaseObject> T getCustom(String procedureName, Class<T> type, Object... params) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(pkg.canCallProcedure(procedureName)) {
				return connectionManager.executeCursorProcedure(pkg, procedureName, params).getRow().construct(type);
			}
		}
		return null;
	}
	
	public <T extends DatabaseObject> List<T> getCustomList(String procedureName, Class<T> type, Object... params) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(pkg.canCallProcedure(procedureName)) {
				return connectionManager.executeCursorProcedure(pkg, procedureName, params).toListOf(type);
			}
		}
		return null;
	}
	
	public <T extends DatabaseObject> boolean executeCustomProcedure(String procedureName, Class<T> type, Object... params) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(pkg.canCallProcedure(procedureName)) {
				return connectionManager.executeStoredProcedure(pkg, procedureName, params);
			}
		}
		return false;
	}
	
	public <T extends DatabaseObject> T get(Integer primaryKey, Class<T> type) {
		return get(primaryKey.toString(), type);
	}
	
	public <T extends DatabaseObject> T get(String primaryKey, Class<T> type) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(pkg.canCallProcedure("get")) {
				return connectionManager.executeCursorProcedure(pkg, "get", primaryKey).getRow().construct(type);
			}
		}
		return null;
	}
	public <T extends DatabaseObject> List<T> getAll(Class<T> type) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(pkg.canCallProcedure("getall")) {
				return connectionManager.executeCursorProcedure(pkg, "getall").toListOf(type);
			}
		}
		return new ArrayList<T>();
	}
	
	public <T extends DatabaseObject> List<T> getAll(T object) {
		if(packageIsAvailable(object.getClass())) {
			Package pkg = getPackage(object.getClass());
			if(pkg.canCallProcedure("getall")) {
				Object[] params = getParameters(DatabaseFieldType.GETALL, object);
				return (List<T>) connectionManager.executeCursorProcedure(pkg, "getall", params).toListOf(object.getClass());
			}
		}
		return new ArrayList<T>();
	}
	
	public <T extends DatabaseObject> boolean create(T object) {
		if(packageIsAvailable(object.getClass())) {
			Package pkg = getPackage(object.getClass());
			if(pkg.canCallProcedure("new")) {
				Object[] params = getParameters(DatabaseFieldType.NEW, object);
				return connectionManager.executeStoredProcedure(pkg, "new", params);
			}
		}
		return false;
	}
	
	public <T extends DatabaseObject> boolean update(T object) {
		if(packageIsAvailable(object.getClass())) {
			Package pkg = getPackage(object.getClass());
			if(pkg.canCallProcedure("update")) {
				Object[] params = getParameters(DatabaseFieldType.UPDATE, object);
				return connectionManager.executeStoredProcedure(pkg, "update"+ object.getClass().getSimpleName().replace("View", ""), params);
			}
		}
		return false;
	}
	
	public <T extends DatabaseObject> boolean delete(T object) {
		if(packageIsAvailable(object.getClass())) {
			Package pkg = getPackage(object.getClass());
			if(pkg.canCallProcedure("delete")) {
				return connectionManager.executeStoredProcedure(pkg, "delete", object.getPrimaryKey());
			}
		}
		return false;
	}
	
	public <T extends DatabaseObject> boolean delete(String primaryKey, Class<T> type) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(pkg.canCallProcedure("delete")) {
				return connectionManager.executeStoredProcedure(pkg, "delete", primaryKey);
			}
		}
		return false;
	}
	
	public <T extends DatabaseObject> List<T> filter(Class<T> type, Object... filterBy) {
		if(packageIsAvailable(type)) {
			Package pkg = getPackage(type);
			if(pkg.canCallProcedure("filter")) {
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
		List<Class<?>> classes = DatabaseObjectClassUtil.getClassList(object.getClass());
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

	@Override
	public String toString() {
		String s = "ObjectManager [\n\tpackages=[";
		for(Package p : packages) {
			s += "\n\t\tpackage=" + p.toString();
		}
		return s + "\n\t]";
	}
}
