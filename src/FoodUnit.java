/**
 * 
 */


import com.github.mlaursen.database.objects.MyResultRow;

/**
 * @author mikkel.laursen
 *
 */
public class FoodUnit extends Template {

	private String longName;
	public FoodUnit() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param primaryKey
	 */
	public FoodUnit(String primaryKey) {
		super(primaryKey);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 * @param id
	 */
	public FoodUnit(String name, int id) {
		super(name, id);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param r
	 */
	public FoodUnit(MyResultRow r) {
		super(r);
		// TODO Auto-generated constructor stub
	}
	
	public void setLongName(MyResultRow r) {
		this.longName = r.get("long_name");
	}
	
	public String getLongName() {
		return this.longName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FoodUnit [" + (primaryKey != null ? "primaryKey=" + primaryKey + ", " : "")
				+ (longName != null ? "longName=" + longName : "") + "id=" + id + ", " + "]";
	}
	
	

}
