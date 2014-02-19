/**
 * 
 */
package test;

import static com.github.mlaursen.annotations.DatabaseAnnotationType.*;

import com.github.mlaursen.annotations.DatabaseField;
import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.MyResultRow;

/**
 * @author mikkel.laursen
 *
 */
public class FoodUnit extends DatabaseObject {
	{ setPrimaryKeyName("name"); }
	@DatabaseField(values={GET}, getPosition=0)
	private String longName;
	private int id;
	public FoodUnit() {	}

	/**
	 * @param primaryKey
	 */
	public FoodUnit(String primaryKey) {
		super(primaryKey);
	}
	
	public FoodUnit(String primaryKey, int id) {
		this(primaryKey);
		this.id = id;
	}

	/**
	 * @param r
	 */
	public FoodUnit(MyResultRow r) {
		super();
		setPrimaryKey(r.get(getPrimaryKeyName()));
		setLongName(r);
	}

	public String getLongName() {
		return longName;
	}
	
	public void setLongName(MyResultRow r) {
		longName = r.get("long_name");
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FoodUnit [" + (getPrimaryKey() != null ? "primaryKey=" + getPrimaryKey() + ", " : "")
				+ (longName != null ? "longName=" + longName + ", " : "") + "id=" + id + "]";
	}
	
	
}
