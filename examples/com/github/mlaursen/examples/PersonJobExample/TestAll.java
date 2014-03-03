/**
 * 
 */
package com.github.mlaursen.examples.PersonJobExample;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.github.mlaursen.database.objects.Procedure;
import com.github.mlaursen.database.testing.TestingObjectManager;

/**
 * @author mikkel.laursen
 *
 */
public class TestAll {
	protected TestingObjectManager tom = new TestingObjectManager();
	
	@Rule
	public TestRule rule = new TestWatcher() {		
		@Override
		public void finished(Description description) {
			super.finished(description);
			if(tom != null) tom.cleanUp();
		}
	};
	@Test
	public void testJobType() {
		tom = new TestingObjectManager(JobType.class);
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
		//tom = new TestingObjectManager(Job.class);
	}
	
	@Test
	public void testAll() {
	}
}
