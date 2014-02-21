

import com.github.mlaursen.database.objects.MyResultRow;


public class Calorie {

	private double amt;
	public Calorie(double amt) {
		this.amt = amt;
	}
	
	public Calorie(MyResultRow r) {
		this.amt = StringNumberFormat.attemptParseDouble(r.get("calories"));
	}
	
	public void setAmount(double a) {
		amt = a;
	}
	
	public double getAmount() {
		return amt;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%.2f", amt);
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Calorie && amt == ((Calorie) o).amt;
	}
}
