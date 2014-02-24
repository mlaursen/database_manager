/**
 * 
 */
package com.github.mlaursen.examples.PersonJobExample;

/**
 * @author mikkel.laursen
 *
 */
public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("====================\nJobType queries");
		JobType it = new JobType();
		System.out.println(it.getAll());
		System.out.println(it.get("it"));
		
		System.out.println("\n====================\nJob queries");
		Job j = new Job();
		System.out.println(j.get("1"));
		System.out.println(j.getAll());
		System.out.println(j.get("entry level java developer"));
		Job mechanic = new Job("mechanic");
		System.out.println(mechanic);
		
		System.out.println("\n====================\nPerson queries");
		Person p = new Person();
		Person archer = p.getByLastName("archer");
		System.out.println(archer);
		double salary = archer.getSalary();
		archer.setSalary(800000);
		System.out.println("Updating Archer's salary. Was it successfuly? " + archer.update());
		archer.setSalary(salary);
		System.out.println("Returning archer's salary to the previous amount. " + archer.update());
		Person newPerson = new Person();
		newPerson.setFirstName("test");
		newPerson.setLastName("testing");
		newPerson.setJobId("1");
		newPerson.setSalary(60000);
		System.out.println(newPerson);
		System.out.println("Was this person successfully created? " + newPerson.create());
		
		Person test = p.getByFirstName("test");
		System.out.println(test);
		System.out.println("Was this person successfully deleted? " + test.delete());
	}

}
