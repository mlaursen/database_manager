/**
 * 
 */


import java.util.ArrayList;
import java.util.List;

import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.MyResultRow;
import com.github.mlaursen.database.objecttypes.GetAllable;
import com.github.mlaursen.database.objecttypes.Getable;

/**
 * @author mikkel.laursen
 *
 */
public abstract class Template extends DatabaseObject implements Getable, GetAllable {
	{ setPrimaryKeyName("name"); }
	
	protected int id;
	public Template() { }

	/**
	 * @param primaryKey
	 */
	public Template(String primaryKey) {
		super();
		this.primaryKey = primaryKey;
	}

	public Template(String name, int id) {
		this(name);
		this.id = id;
	}

	/**
	 * @param r
	 */
	public Template(MyResultRow r) {
		super();
		setAll(r);
	}

	
	@Override
	public String toString() {
		return primaryKey;
	}
}
