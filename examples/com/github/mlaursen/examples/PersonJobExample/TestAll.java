/**
 * 
 */
package com.github.mlaursen.examples.PersonJobExample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;

import com.github.mlaursen.database.objects.Procedure;
import com.github.mlaursen.database.testing.TestingObjectManager;

/**
 * @author mikkel.laursen
 *
 */
public class TestAll {
	protected static TestingObjectManager tom = new TestingObjectManager();
	
	@ClassRule
	public static ExternalResource resource = new ExternalResource() {
		@Override
		protected void after() {
			tom.cleanUp();
		}
	};
	
	@Test
	public void testJobType() {
		tom.addPackage(JobType.class);
		Procedure get = new Procedure("get", "primarykey");
		Procedure getAll = new Procedure("getall");
		getAll.setName("get");
		Procedure newP = new Procedure("new", "primarykey");
		newP.setHasCursor(false);
		assertEquals(get.toString(), tom.getPackage(JobType.class).getProcedure("get").toString());
		assertEquals(getAll.toString(), tom.getPackage(JobType.class).getProcedure("getall").toString());
		assertEquals(newP.toString(), tom.getPackage(JobType.class).getProcedure("new").toString());
		
		JobType it = new JobType("IT");
		JobType hr = new JobType("HR");
		JobType business = new JobType("BUSINESS");
		assertTrue(tom.create(it));
		assertTrue(tom.create(hr));
		assertTrue(tom.create(business));
		JobType itDB = tom.get("IT", JobType.class);
		assertEquals(it, itDB);
		
		List<JobType> jobTypes = Arrays.asList(it, hr, business);
		List<JobType> fromDb = tom.getAll(JobType.class);
		assertNotNull(fromDb);
		assertEquals(jobTypes.size(), fromDb.size());
		for(JobType jt : fromDb) {
			assertTrue(jobTypes.contains(jt));
		}
	}

	@Test
	public void testJob() {
		tom.addPackage(Job.class);
		//Job jDev = new Job("IT", "ENTRY LEVEL JAVA DEVELOPER", "Stuff");
		Job business = new Job("BUSINESS", "ENTRY LEVEL BUSINESS POSITION", "IDK BUSINESS");
		JobType it = tom.get("it", JobType.class);
		assertNotNull(it);
		Job jDev = new Job(it, "ENTRY LEVEL JAVA DEVELOPER", "Stuff happens");
		assertTrue(tom.create(jDev));
		//assertTrue(tom.create(jDev));
		//assertTrue(tom.create(business));
		
		
	}
	
	@Test
	public void testPerson() {
		
	}
	
	@Test
	public void testAll() {
	}
}
