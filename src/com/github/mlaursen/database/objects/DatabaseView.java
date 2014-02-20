/**
 * 
 */
package com.github.mlaursen.database.objects;

import com.github.mlaursen.database.objecttypes.Getable;

/**
 * This is an extention of a DatabaseObject. The default implementation removes
 * the "View" from the package name. This is if the View uses the same database
 * package as the source table(s)
 * 
 * @author mikkel.laursen
 * 
 */
public class DatabaseView extends DatabaseObject implements Getable {
	{
		setPackageName();
	}

	public DatabaseView() {}

	public DatabaseView(String primaryKey) {
		super(primaryKey);
	}

	public DatabaseView(Integer primaryKey) {
		super(primaryKey);
	}

	public DatabaseView(MyResultRow r) {
		super(r);
	}

	/**
	 * This is if the get procedure name is different than the default "GET".
	 * The get method will still call the method by searching for GET, but the
	 * new name will be supplied to the Database
	 * 
	 * @param newName
	 */
	protected void setGetProcedureName(String newName) {
		manager.getPackage().getProcedure("get").setName(newName);
	}

	protected void setPackageName() {
		Package pkg = manager.getPackage();
		pkg.setName(pkg.getName().replace("_View", ""));
	}

	protected void setPackageName(String name) {
		manager.getPackage().setName(name);
	}
	
	protected void setPackage(DatabaseObject o) {
		manager.setPackage(o.manager.getPackage());
	}

}
