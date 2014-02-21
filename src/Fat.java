

import com.github.mlaursen.database.objects.MyResultRow;

public class Fat extends Macro {

	public Fat(double a) {
		super(a);
		setToCals(9);
	}
	
	public Fat(MyResultRow r) {
		super(r);
		setToCals(9);
	}
}
