package com.github.mlaursen.database.objects;

public interface DatabaseCreateable {
	boolean create();
	String[] getCreateableParameters();
}
