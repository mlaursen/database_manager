/**
 * 
 */
package testing;

import java.util.Arrays;

import com.github.mlaursen.database.managers.ObjectManager;
import com.github.mlaursen.database.objects.Package;
import com.github.mlaursen.examples.PersonJobExample.Job;
import com.github.mlaursen.examples.PersonJobExample.JobType;
import com.github.mlaursen.examples.PersonJobExample.Person;
import com.github.mlaursen.examples.PersonJobExample.PersonView;

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
		//Package normalPkg = new Package(Person.class);
		//System.out.println(normalPkg);
		System.out.println(Package.formatClassName(PersonView.class));
		ObjectManager om = new ObjectManager(Person.class, PersonView.class);
		System.out.println(om);
		System.out.println(om.get(0, PersonView.class));
		//System.out.println(om.get(1, PersonView.class));
		/*
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
		*/
	}

}
