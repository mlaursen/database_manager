/**
 * 
 */
package testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.github.mlaursen.annotations.DatabaseField;
import com.github.mlaursen.annotations.DatabaseFieldType;
import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.MyClob;
import com.github.mlaursen.database.objects.MyResultRow;
import com.github.mlaursen.database.objects.ObjectManager;
import com.github.mlaursen.database.objects.Procedure;
import com.github.mlaursen.database.objecttypes.Createable;
import com.github.mlaursen.database.objecttypes.Deleteable;
import com.github.mlaursen.database.objecttypes.Filterable;
import com.github.mlaursen.database.objecttypes.GetAllable;
import com.github.mlaursen.database.objecttypes.Getable;
import com.github.mlaursen.database.objecttypes.Updateable;
import com.github.mlaursen.database.objects.Package;

/**
 * @author mikkel.laursen
 *
 */
public class DatabaseObjectTest {
	public static class TestTable extends DatabaseObject implements Getable, Createable, Updateable, Deleteable, Filterable, GetAllable {
		@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE, DatabaseFieldType.FILTER})
		private String name;
		
		@DatabaseField(values={DatabaseFieldType.NEW})
		private MyClob orange;

		public TestTable() {}
		public TestTable(String name, String orange) {
			super();
			this.name = name;
			this.orange = new MyClob(orange);
		}

		/**
		 * @param r
		 */
		public TestTable(MyResultRow r) {
			super(r);
			// TODO Auto-generated constructor stub
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		
		public void setName(MyResultRow r) {
			this.name = r.get("name");
		}

		/**
		 * @return the orange
		 */
		public MyClob getOrange() {
			return orange;
		}

		/**
		 * @param orange the orange to set
		 */
		public void setOrange(MyClob orange) {
			this.orange = orange;
		}
		
		public void setOrange(MyResultRow r) {
			this.orange = new MyClob(r.get("orange"));
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof TestTable) {
				TestTable t = (TestTable) o;
				return t.getName().equalsIgnoreCase(this.name) && t.getOrange().equals(this.orange);
			}
			return false;
		}
		
		@Override
		public List<Procedure> getCustomProcedures() {
			Procedure del = new Procedure("deleteall", "name");
			del.setHasCursor(false);
			return Arrays.asList(del);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "TestTable [name=" + name + ", orange=" + orange + "]";
		}
		
	}
	
	@Test
	public void testTestTable() {
		List<TestTable> ttsExpect = new ArrayList<TestTable>();
		String create = "NEW(:NAME, :ORANGE)";
		String update = "UPDATETESTTABLE(:PRIMARYKEY, :NAME)";
		String get = "GET(:PRIMARYKEY, :CURSOR)";
		String delete = "DELETE(:PRIMARYKEY)";
		String filter = "FILTER(:NAME, :CURSOR)";
		String getAll = "GET(:CURSOR)";
		String deleteAll = "DELETEALL(:NAME)";
		
		ObjectManager m = new ObjectManager(TestTable.class);
		System.out.println(m);
		/*
		Package pkg = m.getPackage(TestTable.class);
		Procedure createProc = pkg.getProcedure("new");
		Procedure updateProc = pkg.getProcedure("updatetesttable");
		Procedure getProc    = pkg.getProcedure("get");
		Procedure getAllProc = pkg.getProcedure("getall");
		Procedure filterProc = pkg.getProcedure("filter");
		Procedure deleteProc = pkg.getProcedure("delete");
		Procedure deleteAllProc = pkg.getProcedure("deleteall");
		
		assertEquals(createProc.toString(), create);
		assertEquals(updateProc.toString(), update);
		assertEquals(getProc.toString(), get);
		assertEquals(getAllProc.toString(), getAll);
		assertEquals(filterProc.toString(), filter);
		assertEquals(deleteProc.toString(), delete);
		assertEquals(deleteAllProc.toString(), deleteAll);
		
		TestTable t = new TestTable("test1", "skippidy do dah");
		ttsExpect.add(t);
		assertTrue(m.create(t));
		
		TestTable t1 = new TestTable("test1", "boop boop");
		ttsExpect.add(t1);
		assertTrue(m.create(t1));
		
		TestTable t2 = new TestTable("test1", "beep beep");
		ttsExpect.add(t2);
		assertTrue(m.create(t2));
		
		TestTable t3 = new TestTable("TEST2", "honk");
		ttsExpect.add(t3);
		assertTrue(m.create(t3));
		List<TestTable> tts = m.getAll(TestTable.class);
		assertEquals(ttsExpect.size(), tts.size());
		assertEquals(ttsExpect, tts);
		TestTable tt = m.get("test2", TestTable.class);
		assertNotNull(tt.getPrimaryKey());
		assertTrue(m.executeCustomProcedure("deleteall", TestTable.class, "test1"));
		assertTrue(m.delete(tt));
		*/
	}

}
