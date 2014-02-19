/**
 * 
 */
package com.github.mlaursen.database;

import java.util.Arrays;

/**
 * @author mikkel.laursen
 *
 */
public class DatabasePackage {

	private String name;
	private DatabaseProcedure[] databaseProcedures;
	public DatabasePackage(String n, DatabaseProcedure... procedures) {
		setName(n);
		this.databaseProcedures = procedures;
	}
	

	/**
	 * Takes in a class and creates a name of the class name without the package and splits
	 * the name with underscores for each capital letter in the name.
	 * 
	 * TestTestTest would return test_test_test
	 * 
	 * @param c
	 * @param databaseProcedures
	 */
	public DatabasePackage(Class<?> c, DatabaseProcedure... procedures) {
		setName(Util.combineWith(Util.splitOnUpper(c.getSimpleName())));
		this.databaseProcedures = procedures;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Adds a procedure to the end of the DatabaseProcedure array
	 * @param p
	 */
	public void addProcedure(DatabaseProcedure p) {
		int l = databaseProcedures.length;
		DatabaseProcedure[] procs = new DatabaseProcedure[l+1];
		for(int i = 0; i < l; i++) {
			procs[i] = databaseProcedures[i];
		}
		procs[l] = p;
		this.databaseProcedures = procs;
	}
	
	public void addParametersToProcedure(String procName, String... params) {
		DatabaseProcedure p = getProcedure(procName);
		p.addParams(params);
	}
	/**
	 * @return the databaseProcedures
	 */
	public DatabaseProcedure[] getProcedures() {
		return databaseProcedures;
	}

	/**
	 * @param databaseProcedures the databaseProcedures to set
	 */
	public void setProcedures(DatabaseProcedure[] procedures) {
		this.databaseProcedures = procedures;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name + (name.toLowerCase().contains("_pkg") ? "" : "_pkg");
	}
	
	/**
	 * Looks up the procedure name with ignoring case.
	 * returns a String to be used in {call ... } of the PACKAGENAME.PROCEDURENAME(:PARAMS, ...)
	 * All upper case;
	 * @param n
	 * @return
	 */
	private String callProcedure(String n) {
		DatabaseProcedure p = getProcedure(n);
		return p == null ? "" : p.toString();
	}
	
	/**
	 * Returns a DatabaseProcedure by procedure name
	 * @param pName
	 * @return
	 */
	public DatabaseProcedure getProcedure(String pName) {
		for(DatabaseProcedure p : databaseProcedures) {
			if(p.getDisplayName().equalsIgnoreCase(pName))
				return p;
		}
		return null;
	}
	
	/**
	 * Creates an uppercase tring to be used in a {call ...} 
	 * @param procedureName
	 * @return
	 */
	public String call(String procedureName) {
		return name.toUpperCase() + "." + callProcedure(procedureName);
	}
	
	
	@Override
	public String toString() {
		return "DatabasePackage [name=" + name.toUpperCase() + ", databaseProcedures=" + Arrays.toString(databaseProcedures) + "]";
	}

}
