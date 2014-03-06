/**
 * 
 */
package com.github.mlaursen.database.objects;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mlaursen.annotations.DatabaseField;
import com.github.mlaursen.annotations.DatabaseFieldType;
import com.github.mlaursen.annotations.DatabaseViewClass;
import com.github.mlaursen.annotations.MultipleDatabaseField;
import com.github.mlaursen.database.objecttypes.Createable;
import com.github.mlaursen.database.objecttypes.Deleteable;
import com.github.mlaursen.database.objecttypes.Filterable;
import com.github.mlaursen.database.objecttypes.GetAllable;
import com.github.mlaursen.database.objecttypes.Getable;
import com.github.mlaursen.database.objecttypes.NoCursor;
import com.github.mlaursen.database.objecttypes.Updateable;
import com.github.mlaursen.database.utils.ClassUtil;

/**
 * This is a java representation of an Oracle Package.
 * A package has:
 * 	a Name
 * 	Stored procedures.
 * 
 * 
 * @author mlaursen
 * 
 */
public class Package {

	private String name;
	private List<Procedure> procedures = new ArrayList<Procedure>();
	private Map<String, Integer> procedureMap = new HashMap<String, Integer>();
	private List<String> availableProcedures = new ArrayList<String>();
	
	/**
	 * The basic constructor for a Package.
	 * It takes in a DatabaseObject class to generate a package for and generates
	 * all the stored procedures
	 * @param databaseObject
	 */
	public Package(Class<? extends DatabaseObject> databaseObject) { this(databaseObject, false); }
	
	/**
	 * Creates a Package with all the stored procedures.
	 * If the boolean test is true, it appends the packagename with test_ to be used for test
	 * cases.
	 * @param databaseObject
	 * @param test
	 */
	@SuppressWarnings("unchecked")
	public Package(Class<? extends DatabaseObject> databaseObject, boolean test) {
		if(ClassUtil.objectAssignableFrom(databaseObject, DatabaseView.class)) {
			this.addAllCustomProcedures(databaseObject);
			databaseObject = (Class<? extends DatabaseObject>) databaseObject.getAnnotation(DatabaseViewClass.class).value();
		}
		this.name = (test ? "test_" : "") + formatClassName(databaseObject);
		generateProcedures(databaseObject);
		this.addAllCustomProcedures(databaseObject);
	}
	
	/**
	 * Adds all custom stored procedures for a Database Object.
	 * It creates a new instance with the basic constructor and executes the inheritated method
	 * getCustomProcedures and then adds each procedure from that list.
	 * @param databaseObject
	 */
	protected void addAllCustomProcedures(Class<? extends DatabaseObject> databaseObject) {
		try {
			DatabaseObject dbo = databaseObject.newInstance();
			List<Procedure> procedures = dbo.getCustomProcedures();
			for(Procedure p : procedures) {
				this.addProcedure(p);
			}
		}
		catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This merges all the procedures for another package into the current package.
	 * The example is if you have a Database Object and a Database View. The Database View
	 * has some extra stored procedures so you add the additional stored procedures to the package.
	 * @param pkg
	 */
	public void mergeProcedures(Package pkg) {
		mergeProcedures(pkg.getProcedures());
	}
	
	/**
	 * Merges a list of procedures to the current package.
	 * @param procedures
	 */
	public void mergeProcedures(List<Procedure> procedures) {
		for(Procedure p : procedures) {
			if(!this.canCallProcedure(p.getName())) {
				addProcedure(p);
			}
		}
	}
	
	private void generateProcedures(Class<? extends DatabaseObject> databaseObject) {
		generateProcedure(databaseObject, Getable.class);
		generateProcedure(databaseObject, GetAllable.class);
		generateProcedure(databaseObject, Filterable.class);
		generateProcedure(databaseObject, Updateable.class);
		generateProcedure(databaseObject, Deleteable.class);
		generateProcedure(databaseObject, Createable.class);
	}
	
	private void generateProcedure(Class<? extends DatabaseObject> databaseObject, Class<?> objectType) {
		if(ClassUtil.objectAssignableFrom(databaseObject, objectType)) {
			String procedureName = objectType.getSimpleName().toLowerCase().replace("able", "");
			procedureName = procedureName.equals("create") 
								? "new" 
								: procedureName + (procedureName.equals("update") 
										? databaseObject.getSimpleName().replace("View", "") 
										: "");
			Procedure p = new Procedure(procedureName, getParametersFromClass(DatabaseFieldType.classToType(objectType), databaseObject));
			if(procedureName.equals("new") && p.getParams().length == 0) {
				p.addParams(new String[]{"primarykey"});
			}
			if (ClassUtil.objectAssignableFrom(objectType, NoCursor.class)) {
				p.setHasCursor(false);
			}
			if (objectType.equals(GetAllable.class)) {
				p.setDisplayName("getall");
				p.setName("get");
			}
			this.addProcedure(p);
		}
	}

	/**
	 * Converts the key/value pair of parameters into an ordered array of
	 * parameters
	 * 
	 * @param proc
	 * @param c
	 * @return
	 */
	private String[] getParametersFromClass(DatabaseFieldType proc, Class<?> c) {
		Map<Integer, String> map = getParametersFromClassHelper(proc, c);
		int s = map.size();
		String[] ps = new String[s];
		for (int i = 0; i < s; i++) {
			ps[i] = map.get(i);
		}
		return ps;
	}

	/**
	 * Phew. Big Helper. Gets all the super classes for a class and starts from
	 * superclass down to current class adding each annotation within that class
	 * for the correspoding procedure as parameters.
	 * 
	 * @param proc
	 *            Procedure type to lookup and possibly add parameters to the
	 *            results
	 * @param c
	 *            A class to check for annotations
	 * @param current
	 *            A result set
	 * @param counter
	 *            Interger for the position to place the field in the procedure
	 *            string
	 * @return
	 */
	private Map<Integer, String> getParametersFromClassHelper(DatabaseFieldType proc, Class<?> clss) {
		int counter = 0;
		Map<Integer, String> current = new HashMap<Integer, String>();
		List<Class<?>> classes = ClassUtil.getClassList(clss);
		for (Class<?> c : classes) {
			for (Field f : c.getDeclaredFields()) {
				if (f.isAnnotationPresent(MultipleDatabaseField.class)) { // Handle
																			// a
																			// MultipleDatabaseField
					MultipleDatabaseField m = f.getAnnotation(MultipleDatabaseField.class);
					if (Arrays.asList(m.values()).contains(proc)) {
						for (String n : m.names()) {
							current.put(counter, n);
							counter++;
						}
					}
				}
				else if (f.isAnnotationPresent(DatabaseField.class)) { // Handle
																		// DatabaseField
					DatabaseField a = f.getAnnotation(DatabaseField.class);
					if (Arrays.asList(a.values()).contains(proc)) {
						try {
							int pos;
							if (a.reorder()) {
								pos = DatabaseFieldType.getPosition(proc, a);
							}
							else {
								pos = counter;
							}
							counter++;
							if (pos == -1) {
								throw new Exception();
							}
							else {
								current.put(pos, f.getName());
							}
						}
						catch (Exception e) {
							String err = "The position for the procedure '" + proc + "' has not been initialized for the field " + "["
									+ f.getName() + "] in class [" + c.getName() + "].  The value has not been added to the parameter map.";
							System.err.println(err);
						}
					}
				}
			}
		}
		return current;
	}
	
	
	public String getName() {
		return name;
	}

	

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name + (name.toLowerCase().contains("_pkg") ? "" : "_pkg");
	}

	/**
	 * Looks up the procedure name with ignoring case. returns a String to be
	 * used in {call ... } of the PACKAGENAME.PROCEDURENAME(:PARAMS, ...) All
	 * upper case;
	 * 
	 * @param n
	 * @return
	 */
	private String callProcedure(String n) {
		Procedure p = getProcedure(n);
		return p == null ? "" : p.toString();
	}

	/**
	 * Returns a Procedure by procedure name
	 * This looks for the displayName instead of the name
	 * For example the procedure GETALL will have a displayName of GETALL
	 * while the name would be GET
	 * 
	 * @param pName
	 * @return
	 */
	public Procedure getProcedure(String pName) {
		for (Procedure p : procedures) {
			if (p.getDisplayName().equalsIgnoreCase(pName))
				return p;
		}
		return null;
	}

	/**
	 * Creates an uppercase tring to be used in a {call ...}
	 * 
	 * @param procedureName
	 * @return
	 */
	public String call(String procedureName) {
		return name.toUpperCase() + "." + callProcedure(procedureName);
	}

	@Override
	public String toString() {
		return "Package [name=" + name.toUpperCase() + ", procedures=" + procedures + "]";
	}

	/**
	 * @return the procedures
	 */
	public List<Procedure> getProcedures() {
		return procedures;
	}

	/**
	 * @param procedures the procedures to set
	 */
	public void setProcedures(List<Procedure> procedures) {
		this.procedures = procedures;
	}
	
	/**
	 * Adds a procedure to the package and updates the availableProcedures and procedureMap
	 * @param p
	 */
	public void addProcedure(Procedure p) {
		this.procedures.add(p);
		this.availableProcedures.add(p.getName());
		this.procedureMap.put(p.getName(), procedures.size()-1);
	}
	
	/**
	 * Formats the class name for a DatabaseObject.
	 * 
	 * @param c
	 * @return
	 */
	public static String formatClassName(Class<?> c) {
		if(c.isAnnotationPresent(DatabaseViewClass.class)) {
			c = c.getAnnotation(DatabaseViewClass.class).value();
		}
		String name = ClassUtil.combineWith(ClassUtil.splitOnUpper(c.getSimpleName()));
		return name + (name.toLowerCase().contains("_pkg") ? "" : "_pkg");
	}
	
	/**
	 * Checks if a procedure exists by name
	 * @param n
	 * @return
	 */
	public boolean canCallProcedure(String n) {
		return getProcedure(n) != null;
	}

}
