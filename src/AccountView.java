import com.github.mlaursen.database.objects.DatabaseView;
import com.github.mlaursen.database.objects.MyResultRow;
import com.github.mlaursen.database.objecttypes.Updateable;

/**
 * 
 */

/**
 * @author mikkel.laursen
 *
 */
public class AccountView extends DatabaseView implements Updateable {

	/**
	 * 
	 */
	public AccountView() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param primaryKey
	 */
	public AccountView(String primaryKey) {
		super(primaryKey);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param primaryKey
	 */
	public AccountView(Integer primaryKey) {
		super(primaryKey);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param r
	 */
	public AccountView(MyResultRow r) {
		super(r);
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] _) {
		AccountView av = new AccountView();
		
		System.out.println(av.getDatabaseManagerToString());
	}

}
