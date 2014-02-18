/**
 * 
 */
package com.github.mlaursen.annotations;

/**
 * @author mikkel.laursen
 *
 */
public enum ComplexityLevel {
	CREATEABLE, DELETEABLE, FILTERABLE, GETABLE, LISTABLE, UPDATEABLE;
	
	public String toString() {
		return this.name();
	}
}
