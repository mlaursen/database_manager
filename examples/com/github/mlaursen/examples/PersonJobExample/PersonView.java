/**
 * 
 */
package com.github.mlaursen.examples.PersonJobExample;

import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.DatabaseView;
import com.github.mlaursen.database.objects.MyResultRow;
import com.github.mlaursen.database.objecttypes.GetAllable;
import com.github.mlaursen.database.objecttypes.Getable;

/**
 * @author mikkel.laursen
 *
 */
public class PersonView extends DatabaseView implements Getable, GetAllable {

	protected String personName;
	protected double personSalary;
	protected JobType jobType;
	protected String jobName, jobDescription;
	public PersonView() {
		super(Person.class);
	}

	/**
	 * @param primaryKey
	 */
	public PersonView(String primaryKey) {
		this();
		this.primaryKey = primaryKey;
	}

	
	public PersonView(MyResultRow r) {
		this();
		setAll(r);
	}

	/**
	 * @return the personName
	 */
	public String getPersonName() {
		return personName;
	}

	/**
	 * @param personName the personName to set
	 */
	public void setPersonName(String personName) {
		this.personName = personName;
	}

	/**
	 * @return the personSalary
	 */
	public double getPersonSalary() {
		return personSalary;
	}

	/**
	 * @param personSalary the personSalary to set
	 */
	public void setPersonSalary(double salary) {
		this.personSalary = salary;
	}

	/**
	 * @return the jobType
	 */
	public JobType getJobType() {
		return jobType;
	}

	/**
	 * @param jobType the jobType to set
	 */
	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}

	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * @return the jobDescription
	 */
	public String getJobDescription() {
		return jobDescription;
	}

	/**
	 * @param jobDescription the jobDescription to set
	 */
	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	
	public void setPersonName(MyResultRow r) {
		this.personName = r.get("person_name");
	}
	
	public void setPersonSalary(MyResultRow r) {
		this.personSalary = Double.parseDouble(r.get("personSalary"));
	}
	
	public void setJobType(MyResultRow r) {
		this.jobType = new JobType(r);
	}
	
	public void setJobName(MyResultRow r) {
		this.jobName = r.get("job_name");
	}
	
	public void setJobDescription(MyResultRow r) {
		this.jobDescription = r.get("job_description");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PersonView [primaryKey=" + primaryKey + ", personName=" + personName + ", personSalary=" + personSalary + ", jobType="
				+ jobType + ", jobName=" + jobName + ", jobDescription=" + jobDescription + "]";
	}
	
	
}
