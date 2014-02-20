/**
 * 
 */
package com.github.mlaursen.database.objecttypes;

import java.util.List;

import com.github.mlaursen.database.objects.DatabaseObject;

/**
 * @author mikkel.laursen
 * 
 */
public interface Filterable {
	List<DatabaseObject> filter(Object... filterBy);
}
