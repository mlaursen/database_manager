/**
 * 
 */
package com.github.mlaursen.database.objects;

/**
 * A Java representation of an Oracle Stored Procedure.
 * 
 * @author mlaursen
 * 
 */
public class Procedure {
	
	private String name, displayName;
	private boolean hasCursor;
	private String[] params;
	
	/**
	 * {@link #Procedure(String, String, boolean, String...)} The display name is set to the name and the procedure automatically has a
	 * cursor
	 * 
	 * @param n
	 *            The procedure name
	 * @param params
	 *            The optional parameters for the procedure
	 */
	public Procedure(String n, String... params) {
		this(n, n, true, params);
	}
	
	/**
	 * Creates an Oracle Stored Procedure representation
	 * 
	 * @param n
	 *            The procedure name
	 * @param displayName
	 *            The procedure display name. The display name is used when attempting to call a procedure as a user. The {@link #name} will
	 *            be used when executing the stored procedure
	 * @param hasCursor
	 *            Boolean if the procedure has a cursor
	 * @param params
	 *            The optional parameters for the procedure
	 */
	public Procedure(String n, String displayName, boolean hasCursor, String... params) {
		name = n;
		this.displayName = displayName;
		this.hasCursor = hasCursor;
		this.params = params;
	}
	
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * @param displayName
	 *            The display name to be set. This is the name that is used to find a procedure within a package.
	 * 
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * THIS AUTOMATICALLY ADDS A :CURSOR AS THE FINAL PARAMETER IF HASCURSOR IS TRUE Turns everything to uppercase
	 */
	@Override
	public String toString() {
		String s = name + "(";
		for(int i = 0; i < params.length; i++) {
			String p = params[i];
			s += ":" + p + (i + 1 < params.length ? ", " : "");
			
		}
		s += hasCursor ? (params.length == 0 ? "" : ", ") + ":CURSOR" : "";
		return (s + ")").toUpperCase();
	}
	
	/**
	 * @return the params
	 */
	public String[] getParams() {
		return params;
	}
	
	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(String[] params) {
		this.params = params;
	}
	
	/**
	 * Adds parameters to the stored procedure
	 * 
	 * @param params
	 *            Parameters to add
	 */
	public void addParams(String... params) {
		int psize = this.params.length;
		int ssize = params.length;
		int nsize = psize + ssize;
		String[] ps = new String[nsize];
		for(int i = 0; i < psize; i++) {
			ps[i] = this.params[i];
		}
		for(int i = psize; i < nsize; i++) {
			ps[i] = params[i - psize];
		}
		this.params = ps;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the hasCursor
	 */
	public boolean isHasCursor() {
		return hasCursor;
	}
	
	/**
	 * @param hasCursor
	 *            the hasCursor to set
	 */
	public void setHasCursor(boolean hasCursor) {
		this.hasCursor = hasCursor;
	}
	
}
