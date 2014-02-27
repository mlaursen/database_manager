/**
 * 
 */
package testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.mlaursen.annotations.DatabaseField;
import com.github.mlaursen.annotations.DatabaseFieldType;
import com.github.mlaursen.database.ObjectManager;
import com.github.mlaursen.database.objects.Package;
import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.MyClob;
import com.github.mlaursen.database.objects.MyResultRow;
import com.github.mlaursen.database.objects.Procedure;
import com.github.mlaursen.database.objecttypes.Createable;
import com.github.mlaursen.database.objecttypes.Deleteable;
import com.github.mlaursen.database.objecttypes.Filterable;
import com.github.mlaursen.database.objecttypes.GetAllable;
import com.github.mlaursen.database.objecttypes.Getable;
import com.github.mlaursen.database.objecttypes.Updateable;

/**
 * @author mikkel.laursen
 *
 */
public class DatabaseObjectTest {
	public static class TestTable extends DatabaseObject implements Getable, Createable, Updateable, Deleteable, Filterable, GetAllable {
		{
			Procedure delAll = new Procedure("deleteall", "name");
			delAll.setHasCursor(false);
			manager.addCustomProcedure(delAll);
		}
		@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE, DatabaseFieldType.FILTER})
		private String name;
		
		@DatabaseField(values={DatabaseFieldType.NEW})
		private MyClob orange;

		/**
		 * 
		 */
		public TestTable() {
			super();
			// TODO Auto-generated constructor stub
		}

		/**
		 * @param primaryKey
		 */
		public TestTable(Integer primaryKey) {
			super(primaryKey);
			// TODO Auto-generated constructor stub
		}

		/**
		 * @param r
		 */
		public TestTable(MyResultRow r) {
			super(r);
			// TODO Auto-generated constructor stub
		}

		/**
		 * @param primaryKey
		 */
		public TestTable(String primaryKey) {
			super(primaryKey);
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
		
		public boolean deleteAll(String name) {
			return manager.executeStoredProcedure("deleteall", name);
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof TestTable) {
				TestTable t = (TestTable) o;
				return t.getName().equalsIgnoreCase(this.name) && t.getOrange().equals(this.orange);
			}
			return false;
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
		TestTable t = new TestTable();
		String create = "NEW(:NAME, :ORANGE)";
		String update = "UPDATETESTTABLE(:PRIMARYKEY, :NAME)";
		String get = "GET(:PRIMARYKEY, :CURSOR)";
		String delete = "DELETE(:PRIMARYKEY)";
		String filter = "FILTER(:NAME, :CURSOR)";
		String getAll = "GET(:CURSOR)";
		String deleteAll = "DELETEALL(:NAME)";
		
		ObjectManager m = t.getObjectManager();
		Package pkg = m.getPackage();
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
		
		t.setName("test1");
		t.setOrange(new MyClob("skippity do dah"));
		ttsExpect.add(t);
		assertTrue(t.create());
		
		TestTable t1 = new TestTable();
		t1.setName("test1");
		t1.setOrange(new MyClob("boop boop"));
		ttsExpect.add(t1);
		assertTrue(t1.create());
		
		TestTable t2 = new TestTable();
		t2.setName("test1");
		t2.setOrange(new MyClob("beep beep"));
		ttsExpect.add(t2);
		assertTrue(t2.create());
		
		TestTable t3 = new TestTable();
		t3.setName("TEST2");
		t3.setOrange(new MyClob("honk"));
		ttsExpect.add(t3);
		assertTrue(t3.create());
		List<TestTable> tts = t.getAll(TestTable.class);
		assertEquals(ttsExpect.size(), tts.size());
		assertEquals(ttsExpect, tts);
		TestTable tt = new TestTable("test2");
		assertNotNull(tt.getPrimaryKey());
		assertTrue(tt.deleteAll("test1"));
		assertTrue(tt.delete());
	}

}
