import com.github.mlaursen.annotations.DatabaseField;
import com.github.mlaursen.annotations.DatabaseFieldType;
import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.MyResultRow;
import com.github.mlaursen.database.objecttypes.Getable;
import com.github.mlaursen.database.objecttypes.Updateable;

/**
 * 
 */

/**
 * @author mikkel.laursen
 *
 */
public class Account extends DatabaseObject implements Getable, Updateable {

	@DatabaseField(values=DatabaseFieldType.UPDATE)
	private String username;
	public Account() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param primaryKey
	 */
	public Account(String primaryKey) {
		super(primaryKey);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param primaryKey
	 */
	public Account(Integer primaryKey) {
		super(primaryKey);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param r
	 */
	public Account(MyResultRow r) {
		super(r);
		// TODO Auto-generated constructor stub
	}
	
	public void setUsername(MyResultRow r) {
		this.username = r.get("username");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Account [primaryKey=" + primaryKey + ", username=" + username + "]";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Account a = new Account(0);
		System.out.println(a);
		System.out.println(a.getDatabaseManagerToString());

	}

}
