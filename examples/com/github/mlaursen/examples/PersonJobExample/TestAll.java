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
		JobType it = tom.get("it", JobType.class);
		JobType business = tom.get("business", JobType.class);
		JobType hr = tom.get("hr", JobType.class);
		assertNotNull(it);
		assertNotNull(business);
		assertNotNull(hr);
		Job jDev = new Job(it, "ENTRY LEVEL JAVA DEVELOPER", "Stuff happens");
		assertTrue(tom.create(jDev));
		Job businessEntry = new Job(business, "ENTRY LEVEL BUSINESS POSITION", "IDK BUSINESS");
		assertTrue(tom.create(businessEntry));
		
		Job leadHR = new Job(hr, "LEAD HR REPRESENTATIVE", "Woo woo. HR Stuff");
		assertTrue(tom.create(leadHR));
		Job jDev2 = tom.get(0, Job.class);
		assertNotNull(jDev2);
		assertEquals(jDev, jDev2);
		
		Job businessEntry2 = tom.get(1, Job.class);
		assertNotNull(businessEntry2);
		assertEquals(businessEntry, businessEntry2);
		
		Job leadHR2 = tom.get(2, Job.class);
		assertNotNull(leadHR2);
		assertEquals(leadHR, leadHR2);
		
		assertTrue(tom.delete(businessEntry2));
		assertTrue(tom.create(businessEntry));
		
		List<Job> jobs = Arrays.asList(jDev, businessEntry, leadHR);
		List<Job> jobsFromDB = tom.getAll(Job.class);
		assertEquals(jobs.size(), jobsFromDB.size());
		for(Job j : jobsFromDB) {
			assertTrue(jobs.contains(j));
		}
	}
	
	@Test
	public void testPerson() {
		tom.addPackage(Person.class);
	}
	
	@Test
	public void testAll() {
	}
}
