package com.github.mlaursen.database.objecttypes;

import java.util.List;

import com.github.mlaursen.database.objects.DatabaseObject;

public interface GetAllable {
	List<DatabaseObject> getAll();
}
