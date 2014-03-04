/**
 * 
 */
package com.github.mlaursen.examples.PersonJobExample;

import com.github.mlaursen.database.testing.TestingConnectionManager;
import com.github.mlaursen.database.testing.TestingObjectManager;

/**
 * @author mikkel.laursen
 *
 */
public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("====================\nInitilizin TestingObjectManager and creating testing environment...");
		TestingObjectManager manager = new TestingObjectManager(JobType.class, Job.class, Person.class);
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
		archer.setSalary(800000);
		System.out.println("Updating Archer's personSalary. Was it successful? " + manager.update(archer));
		archer.setSalary(salary);
		System.out.println("Returning archer's personSalary to the previous amount. " + manager.update(archer));
		Person test = new Person("test", "testing", "1", 60000);
		System.out.println(test);
		System.out.println(manager.create(test));
		test = manager.executeCustomGetProcedure("getbyname", Person.class, "test", null);
		System.out.println(test);
		System.out.println(manager.delete(test));
		
		manager.cleanUp();
	}

}
