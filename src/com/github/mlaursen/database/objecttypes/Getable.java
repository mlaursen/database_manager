/**
 * 
 */
package com.github.mlaursen.database.objecttypes;

import com.github.mlaursen.database.objects.DatabaseObject;

/**
 * @author mikkel.laursen
 * 
 */
public interface Getable {
	DatabaseObject get(String primaryKey);
}
