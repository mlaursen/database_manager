/**
 * 
 */
package com.github.mlaursen.database.objects;

/**
 * This class does not really offer anything beneficial yet. The annotation DatabaseViewClass pretty much takes care of everything
 * 
 * @author mlaursen
 * 
 */
public class DatabaseView extends DatabaseObject {
	
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
