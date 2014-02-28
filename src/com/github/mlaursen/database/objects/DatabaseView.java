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
	public DatabaseView() { }
	public DatabaseView(MyResultRow r) {
		super(r);
	}
}
