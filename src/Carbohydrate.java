

import com.github.mlaursen.database.objects.MyResultRow;

public class Carbohydrate extends Macro {

	public Carbohydrate(double a) {
		super(a);
	}
	
	public Carbohydrate(MyResultRow r) {
		super(StringNumberFormat.attemptParseDouble(r.get("carbs")));
	}
}
