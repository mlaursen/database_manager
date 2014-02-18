/**
 * 
 */
package com.github.mlaursen.database.objects;

import java.util.List;

/**
 * @author mikkel.laursen
 *
 */
public interface DatabaseFilterable {
	<T extends DatabaseObject> List<T> filter(Object... filterBy);
	String[] getFilterableParameters();
}
