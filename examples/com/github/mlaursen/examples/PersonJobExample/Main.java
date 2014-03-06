/**
 * 
 */
package com.github.mlaursen.examples.PersonJobExample;

import com.github.mlaursen.database.managers.ObjectManager;

/**
 * Here is an example using the data supplied by the sql scripts. There are examples of getting objects, modifying, deleting, and
 * filterting.
 * 
 * @author mlaursen
 * 
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ObjectManager manager = new ObjectManager(JobType.class, Job.class);
		manager.addPackageWithView(Person.class, PersonView.class);
		System.out.println(manager);
		
		System.out.println("\n====================\nJobType queries");
		System.out.println(manager.getAll(JobType.class));
		System.out.println(manager.get("it", JobType.class));
		
		System.out.println("\n====================\nJob queries");
		System.out.println(manager.getAll(Job.class));
		System.out.println(manager.get(3, Job.class));
		System.out.println(manager.get("entry level java developer", Job.class));
		Job mechanic = manager.get("mechanic", Job.class);
		System.out.println(mechanic);
		
		System.out.println("\n====================\nPerson queries");
		Person archer = manager.executeCustomGetProcedure("getbyname", Person.class, null, "archer");
		System.out.println(archer);
		double salary = archer.getSalary();
		archer.setSalary(8);
		System.out.println("Salary update for Archer was " + (manager.update(archer) ? "" : "un") + "successful.");
		archer.setSalary(salary);
		System.out.println("Salary update for Archer was " + (manager.update(archer) ? "" : "un") + "successful.");
		
		PersonView archerV = manager.get(archer.getPrimaryKey(), PersonView.class);
		System.out.println(archerV);
		
		// The next two should be false since PersonView is NOT Updateable
		archerV.setPersonSalary(8);
		System.out.println("Salary update for Archer was " + (manager.update(archerV) ? "" : "un") + "successful.");
		archerV.setPersonSalary(salary);
		System.out.println("Salary update for Archer was " + (manager.update(archerV) ? "" : "un") + "successful.");
		
		Person test = new Person("test", "testing", "1", 60000);
		System.out.println(test);
		System.out.println("Person '" + test.firstName + "' was created " + (manager.create(test) ? "" : "un")
				+ "successfully in the database");
		test = manager.executeCustomGetProcedure("getbyname", Person.class, "test", null);
		System.out.println(test);
		System.out.println("Delete was " + (manager.delete(test) ? "" : "un") + "successful.");
		
	}
	
}
