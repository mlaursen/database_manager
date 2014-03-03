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

import java.util.List;

import org.junit.Test;

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
		String body = "PACKAGE BODY JOB_PKG AS\n";
		body += "  PROCEDURE GET(PID IN INTEGER, PCURSOR OUT SYS_REFCURSOR)\n";
		body += "  IS\n";
		body += "  BEGIN\n";
		body += "    OPEN PCURSOR FOR\n";
		body += "      SELECT *\n";
		body += "      FROM JOB_VIEW\n";
		body += "      WHERE ID=PID\n";
		body += "  END GET;\n";
		body += "\n";
		body += "  PROCEDURE GET(PNAME IN JOB.NAME%TYPE, PCURSOR OUT SYS_REFCURSOR\n";
		body += "  IS\n";
		body += "  BEGIN\n";
		body += "   OPEN PCURSOR FOR\n";
		body += "     SELECT *\n";
		body += "     FROM JOB_VIEW\n";
		body += "     WHERE NAME=UPPER(NAME)\n";
		body += "  END GET;\n";
		body += "  \n";
		body += "  PROCEDURE NEW(PJOB_TYPE IN JOB.JOB_TYPE%TYPE, PNAME IN JOB.NAME%TYPE, PDESC IN JOB.DESCRIPTION%TYPE, PID IN JOB.ID%TYPE DEFAULT SEQ_JOB_ID.NEXTVAL)\n";
		body += "  IS\n";
		body += "  BEGIN\n";
		body += "    INSERT INTO JOB(ID, JOB_TYPE, NAME, DESCRIPTION)\n";
		body += "    VALUES(PID,UPPER(PJOB_TYPE),UPPER(PNAME),PDESC\n";
		body += "    COMMIT;\n";
		body += "\n";
		body += "    EXCEPTION\n";
		body += "      WHEN OTHERS THEN\n";
		body += "        ROLLBACK;\n";
		body += "        RAISE;\n";
		body += "  END NEW;\n";
		body += "END JOB_PKG;\n";
		
		//System.out.println(PackageUtil.packageToTest(new StringBuilder(body), "Job_Pkg"));
		
		String line = "  PROCEDURE NEW(PJOB_TYPE IN JOB.JOB_TYPE%TYPE, PNAME IN JOB.NAME%TYPE, PDESC IN JOB.DESCRIPTION%TYPE, PID IN JOB.ID%TYPE DEFAULT SEQ_JOB_ID.NEXTVAL)\n";
		String expected = "  PROCEDURE NEW(PTEST_JOB_TYPE IN TEST_JOB.JOB_TYPE%TYPE, PNAME IN TEST_JOB.NAME%TYPE, PDESC IN TEST_JOB.DESCRIPTION%TYPE, PID IN TEST_JOB.ID%TYPE DEFAULT SEQ_TEST_JOB_ID.NEXTVAL)\n";
		assertEquals(expected, PackageUtil.formatProcedureLine(line, "JOB"));
	}
}
