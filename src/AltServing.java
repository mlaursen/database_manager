/**
 * 
 */


import com.github.mlaursen.database.objects.MyResultRow;


/**
 * @author mikkel.laursen
 *
 */
public class AltServing extends Serving {

	/**
	 * @param s
	 * @param u
	 */
	public AltServing(double s, FoodUnit u) {
		super(s, u);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param r
	 */
	public AltServing(MyResultRow r) {
		super(r);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param s
	 * @param u
	 */
	public AltServing(String s, String u) {
		super(s, u);
		// TODO Auto-generated constructor stub
	}

}
