package test;

import com.github.mlaursen.annotations.DatabaseField;
import static com.github.mlaursen.annotations.DatabaseAnnotationType.*;

public class Serving {
	
	@DatabaseField(values={CREATE}, createPosition=0)
	private double size;
	
	@DatabaseField(values={CREATE}, createPosition=1)
	private FoodUnit foodUnit;
	public Serving(double s, FoodUnit u) {
		size = s;
		foodUnit = u;
	}
	public Serving(String s, String u) {
		try {
			size = Double.parseDouble(s);
		}
		catch(NumberFormatException e) {
			size = 0;
		}
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
		return size + " (" + getUnitName() + ")";
	}

	
}
