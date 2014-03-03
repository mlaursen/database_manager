/**
 * 
 */
package com.github.mlaursen.annotations;

import com.github.mlaursen.database.objects.DatabaseObject;

/**
 * @author mikkel.laursen
 *
 */
public @interface DatabaseView {
	Class<DatabaseObject> value();
}
