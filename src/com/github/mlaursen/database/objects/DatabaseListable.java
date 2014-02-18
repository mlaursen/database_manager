package com.github.mlaursen.database.objects;

import java.util.List;

public interface DatabaseListable {
	String[] getListableParameters();
	<T extends DatabaseListable> List<T> getAll(Class<T> type);
}
