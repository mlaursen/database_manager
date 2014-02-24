/**
 * 
 */
package com.github.mlaursen.examples.PersonJobExample;

import com.github.mlaursen.annotations.DatabaseField;
import com.github.mlaursen.annotations.DatabaseFieldType;
import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.MyResultRow;
import com.github.mlaursen.database.objecttypes.Createable;
import com.github.mlaursen.database.objecttypes.Deleteable;
import com.github.mlaursen.database.objecttypes.GetAllable;
import com.github.mlaursen.database.objecttypes.Getable;
import com.github.mlaursen.database.objecttypes.Updateable;

/**
 * This is a Database Object representation for the Table named JOB.
 * The order of the fields matter unless you had the value xxPosition for each of the values supplied.
 * Since this DatabaseObject has additional fields other than a primary key, they must be Annotated with the
 * NEW and UPDATE DatabaseFieldType enums.  In addition, getters must be implemented for getting each of the database field
 * values. Setters must be created as well for each DatabaseField that takes only a MyResultRow as the parameter. All of the
 * setters with a MyResultRow parameter will be called when the super method setAll() is called.
 * 
 * This class will generate a manager as follows:
 * 	Package name: JOB_PKG
 * 	Procedures: GET(:PRIMARYKEY, :CURSOR) --> Returns a single Job by searching for the primary key (which is an integer id)
 * 			    GET(:CURSOR) --> Returns a List of ALL Jobs
 * 				NEW(:JOBTYPE, NAME, DESCRIPTION) --> Creates a new Job in the database with the ID being the next value in the sequence
 * 				UPDATEJOB(:PRIMARYKEY, :JOBTYPE, :NAME, :DESCRIPTION) --> Updates all the values in the database for a job based on the job id
 * 				DELETE(:PRIMARYKEY) --> Deletes a job in the database by the primary key id
 * 
 * @author mikkel.laursen
 *
 */
public class Job extends DatabaseObject implements Getable, GetAllable, Updateable, Createable, Deleteable {

	@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE})
	protected String jobType;
	
	@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE})
	protected String name;
	
	@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE})
	protected String description;
	public Job() { }
	public Job(String primaryKey) {
		super(primaryKey);
	}

	/**
	 * @param primaryKey
	 */
	public Job(Integer primaryKey) {
		super(primaryKey);
	}

	/**
	 * @param r
	 */
	public Job(MyResultRow r) {
		super(r);
	}

	public void setJobType(MyResultRow r) {
		this.jobType = r.get("job_type");
	}
	
	public void setName(MyResultRow r) {
		this.name = r.get("name");
	}
	
	public void setDescription(MyResultRow r) {
		this.description = r.get("description");
	}
	/**
	 * @return the jobType
	 */
	public String getJobType() {
		return jobType;
	}
	/**
	 * @param jobType the jobType to set
	 */
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	
	@Override
	public String toString() {
		return "Job [primaryKey=" + primaryKey + ", jobType=" + jobType + ", name=" + name + ", description=" + description + "]";
	}
	
	
}
