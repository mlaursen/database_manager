/**
 * 
 */
package com.github.mlaursen.database.objects;

/**
 * @author mikkel.laursen
 *
 */
public interface DatabaseDeleteable {
	String[] getDeleteableParameters();
	boolean delete();
}
