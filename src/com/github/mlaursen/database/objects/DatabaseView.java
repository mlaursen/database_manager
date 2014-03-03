/**
 * 
 */
package com.github.mlaursen.database.objects;

import com.github.mlaursen.database.objecttypes.Getable;

/**
 * @author mikkel.laursen
 *
 */
public class DatabaseView extends DatabaseObject implements Getable {
	
	protected Class<? extends DatabaseObject> managerObject;
	protected DatabaseView(Class<? extends DatabaseObject> managerObject) {
		this.managerObject = managerObject;
	}

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

	public void setManagerObject(Class<? extends DatabaseObject> managerObject) {
		this.managerObject = managerObject;
	}
	
	public Class<? extends DatabaseObject> getManagerObject() {
		return this.managerObject;
	}
}
