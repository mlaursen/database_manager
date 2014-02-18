/**
 * 
 */
package com.github.mlaursen.database.objects;

/**
 * @author mikkel.laursen
 *
 */
public interface DatabaseGetable {
	String[] getGetableParameters();
	<T extends DatabaseObject> T get(String primaryKey, Class<T> type);
}
