/**
 * 
 */
package testing;

import java.util.Arrays;

import com.github.mlaursen.database.objects.ObjectManager;
import com.github.mlaursen.database.objects.Package;
import com.github.mlaursen.examples.PersonJobExample.Job;
import com.github.mlaursen.examples.PersonJobExample.JobType;
import com.github.mlaursen.examples.PersonJobExample.Person;

/**
 * @author mikkel.laursen
 *
 */
public class Test {

	/**
	 * 
	 */
	public Test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Package pkg = new Package(Person.class);
		//System.out.println(pkg);
		
		ObjectManager mom = new ObjectManager(Person.class, Job.class, JobType.class);
		System.out.println(mom);
		System.out.println(mom.get("1", Person.class));
		System.out.println(mom.getAll(Job.class));
		System.out.println(mom.getAll(JobType.class));
		Person p = new Person();
		p.setFirstName("BOB");
		p.setLastName("BURGERS");
		p.setJobId("1");
		p.setSalary(20000);
		System.out.println(p);
		System.out.println(mom.create(p));
		//System.out.println(mom.getAll(Job.class));
		
		//System.out.println(mom.create(Person.class))
	}

}
