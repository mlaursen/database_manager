/**
 * 
 */
package com.github.mlaursen.database.objects;

/**
 * @author mlaursen
 * 
 */
public class DatabaseView extends DatabaseObject {
	
	private static final long serialVersionUID = -864055880160819780L;

	public DatabaseView() {}
	
	protected DatabaseView(String primaryKey) {
		super(primaryKey);
	}
	
	protected DatabaseView(String primaryKey, String primaryKeyName) {
		super(primaryKey, primaryKeyName);
	}
	
	public DatabaseView(MyResultRow r) {
		super(r);
	}
}
