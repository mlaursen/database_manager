/**
 * 
 */
package com.github.mlaursen.database.objects;

import com.github.mlaursen.annotations.DatabaseViewClass;
import com.github.mlaursen.database.objecttypes.Getable;

/**
 * @author mikkel.laursen
 *
 */
public class DatabaseView extends DatabaseObject implements Getable {
	
	public DatabaseView() {}
	/**
	 * @param primaryKey
	 */
	public DatabaseView(String primaryKey) {
		super(primaryKey);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param r
	 */
	public DatabaseView(MyResultRow r) {
		super(r);
	}
}
