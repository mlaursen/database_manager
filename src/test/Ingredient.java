package test;

import static com.github.mlaursen.annotations.DatabaseAnnotationType.CREATE;
import static com.github.mlaursen.annotations.DatabaseAnnotationType.FILTER;

import com.github.mlaursen.annotations.DatabaseField;
import com.github.mlaursen.annotations.MultipleDatabaseField;
import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.DatabasePackage;
import com.github.mlaursen.database.objects.MyResultRow;
import com.github.mlaursen.database.objecttypes.Createable;
import com.github.mlaursen.database.objecttypes.Filterable;
import com.github.mlaursen.database.objecttypes.GetAllable;
import com.github.mlaursen.database.objecttypes.Getable;

/**
 * 
 */

/**
 * 
 * private Serving defaultServing, alternateServing;
	private Calorie calories;
	private Carbohydrate carbs;
	private Fat fat;
	private Protein protein;
 * @author mikkel.laursen
 *
 */
public class Ingredient extends DatabaseObject implements Getable, GetAllable, Filterable , Createable {
	
	@DatabaseField(values={CREATE})
	private String name;
	
	@DatabaseField(values={CREATE, FILTER}, filterPosition=1)
	private String brand;
	
	@DatabaseField(values={CREATE, FILTER}, filterPosition=0)
	private String category;
	
	@MultipleDatabaseField(values={CREATE}, names={"servingSize", "servingUnit"})
	private Serving defaultServing;
	
	@MultipleDatabaseField(values={CREATE}, names={"altServingSize", "altServingUnit"})
	private Serving alternateServing;
	/*
	@DatabaseField(values={CREATE})
	private String servingUnit;
	
	@DatabaseField(values={CREATE})
	private double servingSize;
	
	@DatabaseField(values={CREATE})
	private double altServingSIze;
	
	@DatabaseField(values={CREATE})
	private String altServingUnit;
	*/
	/*
	@DatabaseFieldClass(value=Serving.class, startVal=3)
	private Serving defaultServing;
	
	@DatabaseFieldClass(value=Serving.class, startVal=5)
	private Serving alternateServing;
	*/
	@DatabaseField(values={CREATE})
	private double calories;
	
	@DatabaseField(values={CREATE})
	private double fat;
	
	@DatabaseField(values={CREATE})
	private double carbs;
	
	@DatabaseField(values={CREATE})
	private double protein;
	public Ingredient() {}
	public Ingredient(MyResultRow r) {
		super(r);
		name = r.get("name");
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Ingredient [primaryKey=" + primaryKey + ", primaryKeyName=" + primaryKeyName + ", name=" + name + "]";
	}
	
	public DatabasePackage getPackage() {
		return this.manager.getPackage();
	}

	/*
	public static final String[] GETABLE_PARAMETERS    = new String[] { "primaryKey" };
	public static final String[] CREATEABLE_PARAMETERS = new String[] {"new", "brand", "category"
																	  , "servingsize", "servingunit"
																	  , "altservingsize", "altservingunit"
																	  , "calories", "fat"
																	  , "carbs", "protein"};
	public static final String[] GETALLABLE_PARAMETERS = new String[] { };
	public static final String[] FILTERABLE_PARAMETERS = new String[] {"cateogry", "brand"};
	*/
	public static void main(String[] _) {
		Ingredient t = new Ingredient();
		System.out.println(t.getDatabaseManagerToString());
		//System.out.println(t.getDatabaseManagerToString());
	//	System.out.println(t.getPackage().getProcedure("getall"));
	//	System.out.println(t.getAll());
	}
}
