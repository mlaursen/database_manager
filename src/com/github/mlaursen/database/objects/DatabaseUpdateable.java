/**
 * 
 */
package com.github.mlaursen.database.objects;

/**
 * @author mikkel.laursen
 *
 */
public interface DatabaseUpdateable {
	boolean update();
	String[] getUpdateableParameters();
}
