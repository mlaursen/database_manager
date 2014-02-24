/**
 * 
 */
package com.github.mlaursen.database.objects;

import java.util.Arrays;

import com.github.mlaursen.database.Util;

/**
 * @author mikkel.laursen
 * 
 */
public class Package {

	private String name;
	private Procedure[] procedures;

	public Package(String n, Procedure... procedures) {
		setName(n);
		this.procedures = procedures;
	}

	/**
	 * Takes in a class and creates a name of the class name without the package
	 * and splits the name with underscores for each capital letter in the name.
	 * 
	 * TestTestTest would return test_test_test
	 * 
	 * @param c
	 * @param procedures
	 */
	public Package(Class<?> c, Procedure... procedures) {
		setName(Util.combineWith(Util.splitOnUpper(c.getSimpleName())));
		this.procedures = procedures;
	}

	public String getName() {
		return name;
	}

	/**
	 * Adds a procedure to the end of the Procedure array
	 * 
	 * @param p
	 */
	public void addProcedure(Procedure p) {
		int l = procedures.length;
		Procedure[] procs = new Procedure[l + 1];
		for (int i = 0; i < l; i++) {
			procs[i] = procedures[i];
		}
		procs[l] = p;
		this.procedures = procs;
	}

	public void addParametersToProcedure(String procName, String... params) {
		Procedure p = getProcedure(procName);
		p.addParams(params);
	}

	/**
	 * @return the procedures
	 */
	public Procedure[] getProcedures() {
		return procedures;
	}

	/**
	 * @param procedures
	 *            the procedures to set
	 */
	public void setProcedures(Procedure[] procedures) {
		this.procedures = procedures;
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
		return "Package [name=" + name.toUpperCase() + ", procedures=" + Arrays.toString(procedures) + "]";
	}

}
