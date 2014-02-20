import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.MyResultRow;
import com.github.mlaursen.database.objecttypes.Getable;

/**
 * 
 */

/**
 * @author mikkel.laursen
 *
 */
public class Ingredient extends DatabaseObject implements Getable {

	private String name, brand, category;
	public Ingredient() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param primaryKey
	 */
	public Ingredient(String primaryKey) {
		super(primaryKey);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param primaryKey
	 */
	public Ingredient(Integer primaryKey) {
		super(primaryKey);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param r
	 */
	public Ingredient(MyResultRow r) {
		super(r);
		// TODO Auto-generated constructor stub
	}
	
	public void setName(MyResultRow r) {
		name = r.get("name");
	}
	
	public void setBrand(MyResultRow r) {
		brand = r.get("brand");
	}
	
	public void setCategory(MyResultRow r) {
		category = r.get("category");
	}
	
	
	
	@Override
	public String toString() {
		return "Ingredient [primaryKey=" + primaryKey + ", name=" + name + ", brand=" + brand + ", category=" + category + "]";
	}

	public static void main(String[] _) {
		Ingredient i = new Ingredient(8);
		System.out.println(i);
	}

}
