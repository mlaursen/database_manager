/**
 * 
 */
package com.github.mlaursen.examples.PersonJobExample;

import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.MyResultRow;
import com.github.mlaursen.database.procedures.Createable;
import com.github.mlaursen.database.procedures.GetAllable;
import com.github.mlaursen.database.procedures.Getable;

/**
 * This is a DatabaseObject representation for the database table named JOB_TYPE.
 * 
 * There are no fields requried in this class because the JOB_TYPE table only has one column that is the primary key. However, since the
 * primary key name is not 'id', you must set the primary key name to 'name'. In addition, the constructor for a JobType with a MyResultRow
 * must be changed to call super() with no parameters and then the method setAll() should be called. If the call to super is passed the
 * MyResultRow, the manager will try to use 'id' as the primaryKeyName instead of 'name'.
 * 
 * The manager created for this class will be as follows: Package name: JOB_TYPE_PKG Procedures: GET(:PRIMARYKEY, :CURSOR) --> The get only
 * one procedure GET(:CURSOR) --> Get All job types NEW(:PRIMARYKEY) - > Creates a new Job Type
 * 
 * @author mlaursen
 * 
 */
public class JobType extends DatabaseObject implements Getable, GetAllable, Createable {
	
	private static final long serialVersionUID = -8177747561681328228L;

	/**
	 * Default constructor. This is always needed for generation purposes.
	 */
	public JobType() {}
	
	/**
	 * Creates a new JobType with the name given. It also sets the primaryKeyName to "name"
	 * 
	 * @param name
	 *            The JobTypeName
	 */
	public JobType(String name) {
		super(name, "name");
	}
	
	/**
	 * Creates a JobType with a MyResultRow. It sets the primaryKeyName to "name" before executing everything
	 * 
	 * @param r
	 *            The MyResultRow
	 */
	public JobType(MyResultRow r) {
		super("name", r);
	}
	
	/**
	 * The toString is override to just return the primary key. This is used when being inserted into the database in JobType
	 */
	@Override
	public String toString() {
		return primaryKey;
	}
	
}
