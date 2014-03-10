/**
 * 
 */
package testing;

import static com.github.mlaursen.database.utils.ClassUtil.canParseInt;
import static com.github.mlaursen.database.utils.ClassUtil.formatClassName;
import static com.github.mlaursen.database.utils.ClassUtil.getClassList;
import static com.github.mlaursen.database.utils.ClassUtil.isClassCallable;
import static com.github.mlaursen.database.utils.DateUtil.sameDate;
import static com.github.mlaursen.database.utils.DateUtil.stringToDate;
import static com.github.mlaursen.database.utils.DateUtil.sysdateToDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import org.junit.Test;

import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.DatabaseView;
import com.github.mlaursen.database.objects.Procedure;
import com.github.mlaursen.database.procedures.Deleteable;
import com.github.mlaursen.database.procedures.GetAllable;
import com.github.mlaursen.database.procedures.Getable;
import com.github.mlaursen.database.procedures.Updateable;
import com.github.mlaursen.database.utils.ClassUtil;
import com.github.mlaursen.database.utils.FileUtil;
import com.github.mlaursen.database.utils.SqlFormatUtil;
import com.github.mlaursen.examples.PersonJobExample.Job;
import com.github.mlaursen.examples.PersonJobExample.Person;
import com.github.mlaursen.examples.PersonJobExample.PersonView;

/**
 * @author mikkel.laursen
 * 
 */
public class UtilTest {
	
	@Test
	public void testFormatClassName() {
		assertEquals(formatClassName(ClassUtil.class), "Class_Util");
		assertEquals(formatClassName(Procedure.class), "Procedure");
		assertEquals(formatClassName(DatabaseObject.class, null, "ello"), "DatabaseelloObject");
		assertEquals(formatClassName(DatabaseObject.class, "", ""), "DatabaseObject");
		assertEquals(formatClassName(DatabaseObject.class, "_", ""), "DatabaseObject");
		assertEquals(formatClassName(DatabaseObject.class, "_", "_"), "DatabaseObject");
	}
	
	@Test
	public void testCanParseInt() {
		assertTrue(canParseInt("1"));
		assertFalse(canParseInt("boop"));
		assertTrue(canParseInt("0"));
		assertTrue(canParseInt("-1"));
		assertTrue(canParseInt("-100"));
		assertFalse(canParseInt(""));
	}
	
	static class Nesting extends DatabaseObject {}
	
	static class Nesting2 extends Nesting {}
	
	static class Nesting3 extends Nesting2 {}
	
	@Test
	public void testGetClassList() {
		Class<?>[] dbvXpct = { DatabaseObject.class, DatabaseView.class };
		List<Class<?>> dbvList = getClassList(DatabaseView.class);
		for(int i = 0; i < dbvXpct.length; i++) {
			assertEquals(dbvList.get(i), dbvXpct[i]);
		}
		
		Class<?>[] nestingExpected = { DatabaseObject.class, Nesting.class, Nesting2.class, Nesting3.class };
		List<Class<?>> nestingList = getClassList(Nesting3.class);
		for(int i = 0; i < nestingExpected.length; i++) {
			assertEquals(nestingList.get(i), nestingExpected[i]);
		}
	}
	
	public void test(String in, String out, String[] classes) throws IOException {
		String tLoc = "testing/test_files/";
		String input = FileUtil.readFile(tLoc + in);
		String expected = FileUtil.readFile(tLoc + out);
		String actual = SqlFormatUtil.formatSqlForTesting(input, classes);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testformatSqlForTesting() {
		try {
			String[] params = { "job", "person", "temp_account", "account", "account_setting", "ingredient", "category", "brand" };
			for(int i = 1; i <= 9; i++) {
				test("t" + i + ".txt", "e" + i + ".txt", params);
			}
		}
		catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testIsClassCallable() {
		assertTrue(isClassCallable(Job.class, GetAllable.class));
		assertTrue(isClassCallable(Person.class, Getable.class));
		assertTrue(isClassCallable(Person.class, Updateable.class));
		assertTrue(isClassCallable(Person.class, Deleteable.class));
		assertFalse(isClassCallable(Person.class, GetAllable.class));
		assertTrue(isClassCallable(PersonView.class, Getable.class));
		assertFalse(isClassCallable(PersonView.class, Updateable.class));
		assertFalse(isClassCallable(PersonView.class, Deleteable.class));
	}
	
	@Test
	public void testSameDay() {
		assertTrue(sameDate(Date.valueOf("1990-01-01"), Date.valueOf("1990-01-01")));
		assertFalse(sameDate(Date.valueOf("1991-01-01"), Date.valueOf("1990-01-01")));
	}
	
	
	@Test
	public void testStringToDate() {
		Date d = Date.valueOf("1990-02-21");
		assertTrue(sameDate(stringToDate("21-FEB-1990", "dd-MMM-yyyy"), d));
		assertTrue(sameDate(stringToDate("02-21-1990", "MM-dd-yyyy"), d));
		assertTrue(sameDate(stringToDate("21-02-1990", "dd-MM-yyyy"), d));
		assertTrue(sameDate(stringToDate("1990-02-21"), d));
		assertTrue(sameDate(stringToDate("02/21/1990"), d));
	}
	
	@Test
	public void testSysdateToString() {
		assertTrue(sameDate(sysdateToDate("1990-02-21 8:22:13"), sysdateToDate("1990-02-21 8:22:13")));
		assertTrue(sameDate(sysdateToDate("1990-02-21 8:22:13"), Date.valueOf("1990-02-21")));
	}
}
