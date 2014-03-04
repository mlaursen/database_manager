/**
 * 
 */
package testing;

import static com.github.mlaursen.database.ClassUtil.canParseInt;
import static com.github.mlaursen.database.ClassUtil.formatClassName;
import static com.github.mlaursen.database.ClassUtil.getClassList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import com.github.mlaursen.FileUtil;
import com.github.mlaursen.database.ClassUtil;
import com.github.mlaursen.database.PackageUtil;
import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.DatabaseView;
import com.github.mlaursen.database.objects.Procedure;

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
	static class Nesting extends DatabaseObject { }
	static class Nesting2 extends Nesting { }
	static class Nesting3 extends Nesting2 { }
	
	@Test
	public void testGetClassList() {
		Class<?>[] dbvXpct = {DatabaseObject.class, DatabaseView.class};
		List<Class<?>> dbvList = getClassList(DatabaseView.class);
		for(int i = 0; i < dbvXpct.length; i++) {
			assertEquals(dbvList.get(i), dbvXpct[i]);
		}
		
		Class<?>[] nestingExpected = {DatabaseObject.class, Nesting.class, Nesting2.class, Nesting3.class};
		List<Class<?>> nestingList = getClassList(Nesting3.class);
		for(int i = 0; i < nestingExpected.length; i++) {
			assertEquals(nestingList.get(i), nestingExpected[i]);
		}
	}
	
	@Test
	public void testPackageToTest() {
		try {
			String inputSmall = FileUtil.readFile("testing/input_small.txt");
			String expectedSmall = FileUtil.readFile("testing/expected_small.txt");
			assertEquals(expectedSmall, PackageUtil.formatPackageDeclarationForTest(inputSmall, "JOB", "_TYPE"));
			
			String inputTricky = FileUtil.readFile("testing/input_tricky.txt");
			String expectedTricky = FileUtil.readFile("testing/expected_tricky.txt");
			assertEquals(expectedTricky, PackageUtil.formatPackageDeclarationForTest(inputTricky, "JOB", "_TYPE"));
			
			String input = FileUtil.readFile("testing/input.txt");
			String expected = FileUtil.readFile("testing/expected.txt");
			assertEquals(expected, PackageUtil.formatPackageDeclarationForTest(input, "JOB_PKG", "_TYPE"));
			
			String jobPkg = FileUtil.readFile("testing/job_pkg.txt");
			String jobPkgExpected = FileUtil.readFile("testing/job_pkg_expected.txt");
			assertEquals(jobPkgExpected, PackageUtil.formatPackageDeclarationForTest(jobPkg, "job_pkg", "_TYPE"));
			assertEquals(jobPkgExpected, PackageUtil.formatPackageDeclarationForTest(jobPkg, "job_pkg", "_type"));
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
