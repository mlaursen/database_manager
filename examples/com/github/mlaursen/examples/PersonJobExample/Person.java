/**
 * 
 */
package com.github.mlaursen.examples.PersonJobExample;

import com.github.mlaursen.annotations.DatabaseField;
import com.github.mlaursen.annotations.DatabaseFieldType;
import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.MyResultRow;
import com.github.mlaursen.database.objects.Procedure;
import com.github.mlaursen.database.objecttypes.Createable;
import com.github.mlaursen.database.objecttypes.Deleteable;
import com.github.mlaursen.database.objecttypes.Getable;
import com.github.mlaursen.database.objecttypes.Updateable;

/**
 * This is a Database Object representation of the Database Table PERSON.
 * 
 * This class is a little bit different since we created our own custom procedure that
 * is not one of the generateable procedures. A procedure named GETBYNAME is created with 
 * the Procedure class.  Since a boolean value was not supplied for whether or not there is
 * a return cursor for this procedure, it is automatically set to true. You can either add a true value 
 * to the Initialization of a procedure or call the setHasCursor method to remove the cursor. Once the procedure
 * has been created, you need to add it to the manager.
 * 
 * The manager generated is:
 * 	Package name: PERSON_PKG
 * 	Procedures: GET(:PRIMARYKEY, :CURSOR) --> Returns a Person by id
 * 				NEW(:JOBID, :FIRSTNAME, :LASTNAME, :SALARY) --> Creates a new Person and uses the sequence to generate
 * 																the Person ID
 * 				UPDATEPERSON(:PRIMARYKEY, :JOBID, :FIRSTNAME, :LASTNAE, :SALARY) --> Updates all the fields for a person
 * 																					 for the given id
 * 				DELETE(:PRIMARYKEY) --> Deletes a person from the database based on id
 * 
 * The manager also includes our procedure:
 * 	GETBYNAME(:FIRST, :LAST, :CURSOR) --> which gets a person by first/last name or just either a first or last name
 * 
 * 
 * @author mikkel.laursen
 *
 */
public class Person extends DatabaseObject implements Createable, Deleteable, Getable, Updateable {
	{
		Procedure getByName = new Procedure("getbyname", "first", "last");
		this.manager.addCustomProcedure(getByName);
	}
	@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE})
	protected String jobId;
	
	@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE})
	protected String firstName;
	
	@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE})
	protected String lastName;
	
	@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE})
	protected double salary;
	public Person() { }
	public Person(String primaryKey) {
		super(primaryKey);
	}

	/**
	 * @param primaryKey
	 */
	public Person(Integer primaryKey) {
		super(primaryKey);
	}
	
	public Person(MyResultRow r) {
		super(r);
	}
	/**
	 * @return the jobId
	 */
	public String getJobId() {
		return jobId;
	}
	/**
	 * @param jobId the jobId to set
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return the salary
	 */
	public double getSalary() {
		return salary;
	}
	/**
	 * @param salary the salary to set
	 */
	public void setSalary(double salary) {
		this.salary = salary;
	}

	public void setJobId(MyResultRow r) {
		this.jobId = r.get("job_id");
	}
	
	public void setFirstName(MyResultRow r) {
		this.firstName = r.get("first_name");
	}
	
	public void setLastName(MyResultRow r) {
		this.lastName = r.get("last_name");
	}
	
	public void setSalary(MyResultRow r) {
		try {
			this.salary = Double.parseDouble(r.get("salary"));
		}
		catch(NullPointerException | NumberFormatException e) {
			this.salary = 0;
		}
	}
	
	public Person getByFirstName(String first) { return getByName(first, null); }
	public Person getByLastName(String last) { return getByName(null, last); }
	public Person getByName(String first, String last) {
		return manager.getFirstRowFromCursorProcedure("getbyname", first, last).construct(Person.class);
	}


	@Override
	public String toString() {
		return "Person [primaryKey=" + primaryKey + ", jobId=" + jobId + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", salary=" + salary + "]";
	}
	
	
}
