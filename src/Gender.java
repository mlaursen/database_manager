import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.MyResultRow;
import com.github.mlaursen.database.objects.MyResultSet;
import com.github.mlaursen.database.objecttypes.GetAllable;

/**
 * 
 */

/**
 * @author mikkel.laursen
 *
 */
public class Gender extends DatabaseObject implements GetAllable {

	{
		setPrimaryKeyName("name");
	}
	public Gender() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param primaryKey
	 */
	public Gender(String primaryKey) {
		super();
		this.primaryKey = primaryKey;
	}

	/**
	 * @param r
	 */
	public Gender(MyResultRow r) {
		super();
		setAll(r);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Gender [primaryKey=" + primaryKey + "]";
	}
	
	public static void main(String[] _) {
		Gender g = new Gender();
		System.out.println(g.getAll());
	}
}
