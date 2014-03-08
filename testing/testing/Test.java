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
		
		String s = "INSERT INTO JOB(ID, JOB_TYPE, DESCRIPTION)";
		String s2 = "CREATE OR REPLACE PACKAGE BODY JOB_PKG AS";
		String regex = "JOB";
		String[] ss = s.split("\\s(?=\\b" + regex + "\\b)((?!" + regex + "\\_)|(?=" + regex + "\\_PKG)|(?=" + regex + "\\_VIEW))");
		String[] aa = s.split("\\s(?=" + regex + ")((?!" + regex + "\\_)|(?=" + regex + "_PKG)|(?=" + regex + "_VIEW))");
		
		String string = "WHERE ACCOUNTID=PACCTID";
		String r = "\\s(?=\\bJOB\\b)(?=\\bJOB\\b((\\_VIEW)|(\\_PKG))|(?!\bJOB\\_))";
		System.out.println(r);
		//ss = s2.split(r);
		regex = "ACCOUNT";
		System.out.println(Arrays.toString(string.split("\\s(?=\\b"+regex+"(\\_VIEW|\\_PKG|[^_])?\\b)")));
		for(String str : ss) {
			System.out.println(str);
		}
		
		regex = "(INGREDIENT|BRAND|CATEGORY)";
		String myregex = "\\s(?=\\b"+regex+"((\\_VIEW|\\_PKG|[^_])?(?!,))\\b)";
		String test = "INSERT INTO INGREDIENT(ID, NAME, BRAND, CATEGORY, SERVING_UNIT, SERVING_SIZE, ALT_SERVING_UNIT, ALT_SERVING_SIZE, CALORIES, FAT, CARBS, PROTEIN)";
		String[] splits = test.split(myregex);
		for(String str : splits) {
			System.out.println(str);
		}
		
		// System.out.println(Arrays.toString(s.split("\\s"+"JOB"+"((^\\_)|(?=\\_PKG)|(?=\\_VIEW))")));
		// System.out.println(Arrays.toString(s2.split("\\s"+"JOB"+"((^\\_)|_PKG|\\s)")));
		/*
		 * //Package normalPkg = new Package(Person.class); //System.out.println(normalPkg);
		 * System.out.println(Package.formatClassName(PersonView.class)); ObjectManager om = new ObjectManager(Person.class,
		 * PersonView.class); System.out.println(om); System.out.println(om.get(0, PersonView.class)); //System.out.println(om.get(1,
		 * PersonView.class)); /* //Package pkg = new Package(Person.class); //System.out.println(pkg);
		 * 
		 * ObjectManager mom = new ObjectManager(Person.class, Job.class, JobType.class); System.out.println(mom);
		 * System.out.println(mom.get("1", Person.class)); System.out.println(mom.getAll(Job.class));
		 * System.out.println(mom.getAll(JobType.class)); Person p = new Person(); p.setFirstName("BOB"); p.setLastName("BURGERS");
		 * p.setJobId("1"); p.setSalary(20000); System.out.println(p); System.out.println(mom.create(p));
		 * //System.out.println(mom.getAll(Job.class));
		 * 
		 * //System.out.println(mom.create(Person.class))
		 */
	}
	
}
