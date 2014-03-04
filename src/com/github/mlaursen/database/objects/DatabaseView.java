/**
 * 
 */
package com.github.mlaursen.database.objects;


/**
 * @author mikkel.laursen
 *
 */
public class DatabaseView extends DatabaseObject {
	
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
