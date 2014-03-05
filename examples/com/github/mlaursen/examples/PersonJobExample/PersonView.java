/**
 * 
 */
package com.github.mlaursen.examples.PersonJobExample;

import java.util.Arrays;
import java.util.List;

import com.github.mlaursen.annotations.DatabaseViewClass;
import com.github.mlaursen.database.objects.DatabaseView;
import com.github.mlaursen.database.objects.MyResultRow;
import com.github.mlaursen.database.objects.Procedure;
import com.github.mlaursen.database.objecttypes.GetAllable;
import com.github.mlaursen.database.objecttypes.Getable;
import com.github.mlaursen.database.objecttypes.Updateable;

/**
 * @author mikkel.laursen
 *
 */
@DatabaseViewClass(Person.class)
public class PersonView extends DatabaseView implements Getable, GetAllable {

	protected Person person;
	protected Job job;
	protected String personName;
	public PersonView() { }

	/**
	 * @param primaryKey
	 */
	public PersonView(String primaryKey) {
		super(primaryKey);
		//this.primaryKey = primaryKey;
	}

	
	public PersonView(MyResultRow r) {
		super(r);
		//setAll(r);
	}

	public PersonView(String personName, String jobType, String jobName, String jobDescription, double personSalary) {
		setPersonName(personName);
		this.person.setSalary(personSalary);
		this.job = new Job();
		this.job.setName(jobName);
		this.job.setJobType(jobType);
		this.job.setDescription(jobDescription);
	}
	
	public void setPerson(MyResultRow r) {
		this.person = new Person();
		this.person.setFirstName(r.get("first_name"));
		this.person.setLastName(r.get("last_name"));
		this.person.setSalary(r.getDouble("person_salary"));
	}
	
	public void setJob(MyResultRow r) {
		this.job = new Job();
		this.job.setName(r.get("job_name"));
		this.job.setJobType(r.get("type"));
		this.job.setDescription(r.get("job_description"));
	}
	
	public void setPersonName(MyResultRow r) {
		this.personName = r.get("person_name");
	}
	
	
	/**
	 * @return the personName
	 */
	public String getPersonName() {
		return this.personName;
	}

	/**
	 * @param personName the personName to set
	 */
	public void setPersonName(String personName) {
		this.personName = personName;
		String[] name = personName.split(",");
		if(this.person == null)
			this.person = new Person();
		if(name.length == 2) {
			this.person.setFirstName(name[1].trim());
			this.person.setLastName(name[0].trim());
		}
		else {
			this.person.setFirstName(personName);
		}
	}
	
	public String getPersonFirstName() {
		return this.person.getFirstName();
	}
	
	public String getPersonLastName() {
		return this.person.getLastName();
	}

	/**
	 * @return the personSalary
	 */
	public double getPersonSalary() {
		return this.person.getSalary();
	}

	/**
	 * @param personSalary the personSalary to set
	 */
	public void setPersonSalary(double salary) {
		this.person.setSalary(salary);
	}

	/**
	 * @return the jobType
	 */
	public JobType getJobType() {
		return this.job.getJobType();
	}

	/**
	 * @param jobType the jobType to set
	 */
	public void setJobType(JobType jobType) {
		this.job.setJobType(jobType);
	}

	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return this.job.getName();
	}

	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.job.setName(jobName);
	}

	/**
	 * @return the jobDescription
	 */
	public String getJobDescription() {
		return this.job.getDescription();
	}

	/**
	 * @param jobDescription the jobDescription to set
	 */
	public void setJobDescription(String jobDescription) {
		this.job.setDescription(jobDescription);
	}
	
	@Override
	public List<Procedure> getCustomProcedures() {
		return Arrays.asList(new Procedure("test"));
	}
	
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof PersonView) {
			PersonView pv = (PersonView) o;
			return pv.personName.equalsIgnoreCase(personName) && pv.getJobName().equalsIgnoreCase(this.getJobName());
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PersonView [primaryKey=" + primaryKey + ", personName=" + personName + ", salary=" + person.getSalary() + 
				", jobName=" + job.getName() + ", jobType=" + job.getJobType() + ", jobDescription=" + job.getDescription() + "]";
	}
}
