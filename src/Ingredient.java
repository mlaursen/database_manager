

import com.github.mlaursen.annotations.DatabaseField;
import com.github.mlaursen.annotations.DatabaseFieldType;
import com.github.mlaursen.annotations.MultipleDatabaseField;
import com.github.mlaursen.database.objects.DatabaseObject;
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
public class Ingredient extends DatabaseObject implements Getable, GetAllable, Createable, Updateable, Deleteable, Filterable {
	{
		Procedure pFilter = new Procedure("filter", "category", "brand");
		this.manager.addCustomProcedure(pFilter);
	}
	@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE})
	private String name;
	
	@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE, DatabaseFieldType.FILTER}, filterPosition=1)
	private Brand brand;
	
	@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE, DatabaseFieldType.FILTER}, filterPosition=0)
	private Category category;
	
	@MultipleDatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE}, names = { "servingSize", "servingUnit" })
	private Serving defaultServing;
	
	@MultipleDatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE}, names = { "altservingSize", "altservingUnit" })
	private Serving alternateServing;
	
	@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE})
	private Calorie calories;
	
	@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE})
	private Carbohydrate carbs;
	
	@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE})
	private Fat fat;
	
	@DatabaseField(values={DatabaseFieldType.NEW, DatabaseFieldType.UPDATE})
	private Protein protein;
	public Ingredient() { }
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
	 * @return the brand
	 */
	public Brand getBrand() {
		return brand;
	}
	/**
	 * @param brand the brand to set
	 */
	public void setBrand(Brand brand) {
		this.brand = brand;
	}
	
	public void setBrand(MyResultRow r) {
		this.brand = new Brand(r.get("brand"));
	}
	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public void setCategory(MyResultRow r) {
		this.category = new Category(r.get("category"));
	}
	/**
	 * @return the calories
	 */
	public Calorie getCalories() {
		return calories;
	}
	/**
	 * @param calories the calories to set
	 */
	public void setCalories(Calorie calories) {
		this.calories = calories;
	}
	
	public void setCalories(MyResultRow r) {
		this.calories = new Calorie(Double.parseDouble(r.get("calories")));
	}
	/**
	 * @return the carbs
	 */
	public Carbohydrate getCarbs() {
		return carbs;
	}
	/**
	 * @param carbs the carbs to set
	 */
	public void setCarbs(Carbohydrate carbs) {
		this.carbs = carbs;
	}
	
	public void setCarbs(MyResultRow r) {
		this.carbs = new Carbohydrate(Double.parseDouble(r.get("carbs")));
	}
	/**
	 * @return the fat
	 */
	public Fat getFat() {
		return fat;
	}
	/**
	 * @param fat the fat to set
	 */
	public void setFat(Fat fat) {
		this.fat = fat;
	}
	
	public void setFat(MyResultRow r) {
		this.fat = new Fat(Double.parseDouble(r.get("fat")));
	}
	/**
	 * @return the protein
	 */
	public Protein getProtein() {
		return protein;
	}
	/**
	 * @param protein the protein to set
	 */
	public void setProtein(Protein protein) {
		this.protein = protein;
	}

	public void setProtein(MyResultRow r) {
		this.protein = new Protein(Double.parseDouble(r.get("protein")));
	}
	
	public Serving getDefaultServing() {
		return defaultServing;
	}
	/**
	 * @param defaultServing the defaultServing to set
	 */
	public void setDefaultServing(Serving defaultServing) {
		this.defaultServing = defaultServing;
	}
	
	public void setDefaultServing(MyResultRow r) {
		this.defaultServing = new Serving(r);
	}
	
	public Serving getAlternateServing() {
		return this.alternateServing;
	}
	/**
	 * @param alternateServing the alternateServing to set
	 */
	public void setAlternateServing(Serving alternateServing) {
		this.alternateServing = alternateServing;
	}
	
	public void setAlternateServing(MyResultRow r) {
		this.alternateServing = new AltServing(r);
	}
	
	public double getServingSize() {
		return this.defaultServing.getSize();
	}
	
	public String getServingUnit() {
		return this.defaultServing.getUnitName();
	}
	
	public double getAltServingSize() {
		return this.alternateServing.getSize();
	}
	
	public String getAltServingUnit() {
		return this.alternateServing.getUnitName();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Ingredient [" + (primaryKey != null ? "primaryKey=" + primaryKey + ", " : "") + (name != null ? "name=" + name + ", " : "")
				+ (brand != null ? "brand=" + brand + ", " : "") + (category != null ? "category=" + category + ", " : "")
				+ (defaultServing != null ? "defaultServing=" + defaultServing + ", " : "")
				+ (alternateServing != null ? "alternateServing=" + alternateServing + ", " : "")
				+ (calories != null ? "calories=" + calories + ", " : "") + (carbs != null ? "carbs=" + carbs + ", " : "")
				+ (fat != null ? "fat=" + fat + ", " : "") + (protein != null ? "protein=" + protein : "") + "]";
	}
	
	public static void main(String[] _) {
		Ingredient i = new Ingredient();
		System.out.println(i.getDatabaseManagerToString());
		i.setName("0% Greek Yogurt");
		i.setBrand(new Brand("Chobani"));
		i.setCategory(new Category("Dairy"));
		i.setDefaultServing(new Serving(227, new FoodUnit("g")));
		i.setAlternateServing(new AltServing(1, new FoodUnit("c")));
		i.setCalories(new Calorie(140));
		i.setFat(new Fat(0));
		i.setCarbs(new Carbohydrate(9));
		i.setProtein(new Protein(23));
		System.out.println(i);
		System.out.println(i.create());
	}
	
}
