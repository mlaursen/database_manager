/**
 * 
 */


import com.github.mlaursen.database.objects.MyResultRow;
import com.github.mlaursen.database.objects.Procedure;
import com.github.mlaursen.database.objecttypes.Createable;

/**
 * @author mikkel.laursen
 *
 */
public class Brand extends Template implements Createable {
	{
		Procedure pNew = manager.getPackage().getProcedure("new");
		pNew.addParams("name");
		
	}
	
	public Brand() { }
	public Brand(String primaryKey) {
		super(primaryKey);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 * @param id
	 */
	public Brand(String name, int id) {
		super(name, id);
	}

	/**
	 * @param r
	 */
	public Brand(MyResultRow r) {
		super(r);
	}

}
