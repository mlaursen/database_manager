/**
 * 
 */
package com.github.mlaursen.examples.PersonJobExample;

import com.github.mlaursen.annotations.DatabaseField;
import com.github.mlaursen.annotations.DatabaseFieldType;
import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.MyResultRow;
import com.github.mlaursen.database.objecttypes.Createable;
import com.github.mlaursen.database.objecttypes.GetAllable;
import com.github.mlaursen.database.objecttypes.Getable;

/**
 * This is a DatabaseObject representation for the database table named JOB_TYPE.
 * 
 * There are no fields requried in this class because the JOB_TYPE table only has one
 * column that is the primary key.  However, since the primary key name is not 'id', you must set the primary
 * key name to 'name'. In addition, the constructor for a JobType with a MyResultRow must be changed to call super() with no 
 * parameters and then the method setAll() should be called.  If the call to super is passed the MyResultRow, the manager
 * will try to use 'id' as the primaryKeyName instead of 'name'.
 * 
 * The manager created for this class will be as follows:
 * 	Package name: JOB_TYPE_PKG
 * 	Procedures: GET(:PRIMARYKEY, :CURSOR) --> The get only one procedure
 * 				GET(:CURSOR) --> Get All job types
 * 				NEW(:PRIMARYKEY) - > Creates a new Job Type
 * 
 * @author mikkel.laursen
 *
 */
public class JobType extends DatabaseObject implements Getable, GetAllable, Createable {
	{
		primaryKeyName="name";
	}
	public JobType() {}
	public JobType(String name) {
		super(name);
	}

	/**
	 * @param r
	 */
	public JobType(MyResultRow r) {
		super();
		setAll(r);
	}
	
	@Override
	public String toString() {
		return primaryKey;
	}

}
