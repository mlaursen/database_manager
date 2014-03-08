/**
 * 
 */
package com.github.mlaursen.database.objects;

/**
 * 
 * Hacky way of creating a clob in the database.
 * 
 * @author mlaursen
 * 
 */
public class MyClob {
	
	private String v;
	
	public MyClob(String v) {
		this.v = v;
	}
	
	public String getValue() {
		return v;
	}
	
	public void setValue(String v) {
		this.v = v;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof MyClob && ((MyClob) o).v.equals(v);
	}
	
	@Override
	public String toString() {
		return v;
	}
	
}
