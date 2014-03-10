/**
 * 
 */
package com.github.mlaursen.annotations;

import com.github.mlaursen.database.procedures.Createable;
import com.github.mlaursen.database.procedures.Deleteable;
import com.github.mlaursen.database.procedures.Filterable;
import com.github.mlaursen.database.procedures.GetAllable;
import com.github.mlaursen.database.procedures.Getable;
import com.github.mlaursen.database.procedures.Updateable;

/**
 * @author mlaursen
 * 
 */
public enum DatabaseFieldType {
	GET, GETALL, NEW, DELETE, UPDATE, FILTER;
	
	public String toString() {
		return this.name().toLowerCase();
	}
	
	public static DatabaseFieldType classToType(Class<?> c) {
		if(c.equals(Getable.class))
			return GET;
		else if(c.equals(GetAllable.class))
			return GETALL;
		else if(c.equals(Createable.class)) {
			return NEW;
		}
		else if(c.equals(Deleteable.class))
			return DELETE;
		else if(c.equals(Updateable.class))
			return UPDATE;
		else if(c.equals(Filterable.class))
			return FILTER;
		else {
			return null;
		}
	}
	
	public static int getPosition(DatabaseFieldType proc, DatabaseField a) {
		int pos = -1;
		if(proc.equals(DatabaseFieldType.GET))
			pos = a.getPosition();
		else if(proc.equals(DatabaseFieldType.GETALL))
			pos = a.getAllPosition();
		else if(proc.equals(DatabaseFieldType.NEW))
			pos = a.createPosition();
		else if(proc.equals(DatabaseFieldType.UPDATE))
			pos = a.updatePosition();
		else if(proc.equals(DatabaseFieldType.DELETE))
			pos = a.deletePosition();
		else if(proc.equals(DatabaseFieldType.FILTER))
			pos = a.filterPosition();
		return pos;
	}
}
