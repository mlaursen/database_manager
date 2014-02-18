/**
 * 
 */
package com.github.mlaursen.database.objects;

/**
 * @author mikkel.laursen
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
	public String toString() {
		return v;
	}

}
