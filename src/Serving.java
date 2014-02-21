

import com.github.mlaursen.database.Util;
import com.github.mlaursen.database.objects.MyResultRow;

public class Serving {

	private double size;
	private FoodUnit foodUnit;
	public Serving(double s, FoodUnit u) {
		size = s;
		foodUnit = u;
	}
	
	public Serving(MyResultRow r) {
		String n = Util.formatClassName(this.getClass());
		String s = (n + "_size").toLowerCase();
		String u = (n + "_unit").toLowerCase();
		size = StringNumberFormat.attemptParseDouble(r.get(s));
		foodUnit = new FoodUnit(r.get(u));
		
	}
	
	public Serving(String s, String u) {
		size = StringNumberFormat.attemptParseDouble(s);
		foodUnit = new FoodUnit(u);
	}
	
	public void setSize(double s) {
		size = s;
	}
	
	public void setUnit(FoodUnit u) {
		foodUnit = u;
	}
	
	public double getSize() {
		return size;
	}
	
	public FoodUnit getUnit() {
		return foodUnit;
	}
	
	public String getUnitName() {
		return foodUnit.getPrimaryKey();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return StringNumberFormat.formatFractionString(size) + " (" + getUnitName() + ")";
	}

	
}
