package com.github.mlaursen.database;

import com.github.mlaursen.database.objects.DatabaseCreateable;
import com.github.mlaursen.database.objects.DatabaseDeleteable;
import com.github.mlaursen.database.objects.DatabaseFilterable;
import com.github.mlaursen.database.objects.DatabaseGetable;
import com.github.mlaursen.database.objects.DatabaseListable;
import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.DatabaseUpdateable;

/**
 * A Database Manager for a database object.
 * This generates a Database Package with Database Procedures.
 * For each Interface that the Database object implements, it will create
 * a procedure within the package.
 * 
 * @see DatabasePackage
 * @see DatabaseProcedure
 * @see DatabaseGetable
 * @see DatabaseListable
 * @see DatabaseUpdateable
 * @see DatabaseDeleteable
 * @see DatabaseFilterable
 * @author mikkel.laursen
 *
 */
public class DatabaseObjectManager<T extends DatabaseObject> {

	private Manager manager;
	private T dbObject;
	private DatabasePackage pkg;
	public DatabaseObjectManager(T dbObj) {
		dbObject = dbObj;
		manager = new Manager();
		pkg = new DatabasePackage(dbObject.getClass());
		generatePackageProcedures();
	}
	
	/**
	 * Executes a database stored procedure that does not have a result set as a result.
	 * The return type will be a boolean if the stored procedure affected at least 1 row.
	 * 
	 * @param procedureName The procedure name to lookup and call from the Database Object's 
	 * 	package
	 * @param An array of object parameters to be passed to the stored procedure. 
	 * 	The parameters will be cast to their corresponding types. {@link com.github.mlaursen.database.Manager}
	 * @return A boolean of success for the stored procedure call. If at least 1 row was affected, 
	 * 	the result is true.
	 */
	public boolean executeStoredProcedure(String procedureName, Object... parameters) {
		return manager.executeStoredProcedure(pkg, procedureName, parameters);
	}
	
	/**
	 * Executes a database stored procedure and returns a list of result rows.
	 * 
	 * @param procedureName The procedure name to lookup and call from the Database Object's 
	 * 	package
	 * @param An array of object parameters to be passed to the stored procedure. 
	 * 	The parameters will be cast to their corresponding types. {@link com.github.mlaursen.database.Manager}
	 * @return a MyResultSet for the resulting rows from the database call. @see MyResultSet
	 */
	public MyResultSet executeCursorProcedure(String procedureName, Object... parameters) {
		return manager.executeCursorProcedure(pkg, procedureName, parameters);
	}
	
	/**
	 * Executes a database stored procedure and returns only the first row.
	 * Mostly used for looking up a single object from the database
	 * @see executeCursorProcedure
	 * 
	 * @param procedureName	The procedure name to lookup and call from the Database Object's 
	 * 	package
	 * @param parameters An array of object parameters to be passed to the stored procedure. 
	 * 	The parameters will be cast to their corresponding types. {@link com.github.mlaursen.database.Manager}
	 * @return a MyResultRow for the resulting row from the database call. @see MyResultRow
	 */
	public MyResultRow getFirstRowFromCursorProcedure(String procedureName, Object... parameters) {
		return executeCursorProcedure(procedureName, parameters).getRow();
	}
	
	private void generatePackageProcedures() {
		if(dbObject instanceof DatabaseGetable) {
			DatabaseGetable dbGetable = (DatabaseGetable) dbObject;
			DatabaseProcedure pGet = new DatabaseProcedure("get", dbGetable.getGetableParameters());
			pkg.addProcedure(pGet);
		}
		
		if(dbObject instanceof DatabaseListable) {
			DatabaseListable dbListable = (DatabaseListable) dbObject;
			DatabaseProcedure pGetAll = new DatabaseProcedure("get", dbListable.getListableParameters());
			pGetAll.setDisplayName("getall");
		}
		
		if(dbObject instanceof DatabaseCreateable) {
			DatabaseCreateable dbCreateable = (DatabaseCreateable) dbObject;
			DatabaseProcedure pCreate = new DatabaseProcedure("create", dbCreateable.getCreateableParameters());
			pkg.addProcedure(pCreate);
		}
		
		if(dbObject instanceof DatabaseUpdateable) {
			DatabaseUpdateable dbUpdateable = (DatabaseUpdateable) dbObject;
			DatabaseProcedure pUpdate = new DatabaseProcedure("update", dbUpdateable.getUpdateableParameters());
			pkg.addProcedure(pUpdate);
		}
		
		if(dbObject instanceof DatabaseFilterable) {
			DatabaseFilterable dbFilter = (DatabaseFilterable) dbObject;
			DatabaseProcedure pFilter = new DatabaseProcedure("filter", dbFilter.getFilterableParameters());
			pkg.addProcedure(pFilter);
		}
		
		if(dbObject instanceof DatabaseDeleteable) {
			DatabaseDeleteable dbDelete = (DatabaseDeleteable) dbObject;
			DatabaseProcedure pDelete = new DatabaseProcedure("delete", dbDelete.getDeleteableParameters());
			pkg.addProcedure(pDelete);
		}
	}
	
	
	@Override
	public String toString() {
		return "DatabaseObjectManager [" + (dbObject != null ? "dbObject=" + dbObject + ", " : "") + (pkg != null ? "pkg=" + pkg : "")
				+ "]";
	}
	
	
}
