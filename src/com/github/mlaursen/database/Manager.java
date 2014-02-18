/**
 * 
 */
package com.github.mlaursen.database;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import oracle.jdbc.OracleTypes;

import com.github.mlaursen.database.objects.DatabaseObject;
import com.github.mlaursen.database.objects.MyClob;

/**
 * Utility class for interacting with databases.
 * A dbconfig.properties file must be located in {projectHomeDirectory}/config
 * 
 * 
 * @author mikkel.laursen
 *
 */
public class Manager {
	private String databaseName, databaseUser, databasePswd, classForName;
	public Manager() {
		try {
			Properties localProperties = new LocalSettings().getLocalSettings();
			databaseName = localProperties.getProperty(LocalSettings.DATABASE_NAME);
			databaseUser = localProperties.getProperty(LocalSettings.USERNAME);
			databasePswd = localProperties.getProperty(LocalSettings.PASSWORD);
			classForName = localProperties.getProperty(LocalSettings.CLASS_FOR_NAME);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a database connection from the localProperties file.
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName(classForName);
		return DriverManager.getConnection(databaseName, databaseUser, databasePswd);
	}
	
	/**
	 * Takes in a package and a procedure name to call with an array of parameters
	 * 
	 * @param pkg	DatabasePackage that holds a procedure to call
	 * @param procedureName	The procedure name to call from the package
	 * @param parameters	Array of parameters to be passed to the stored procedure
	 * @return	True or false depending on if the stored procedure executed successfully without
	 * 			errors
	 */
	public boolean executeStoredProcedure(DatabasePackage pkg, String procedureName, Object... parameters) {
		return executeStoredProcedure(pkg.call(procedureName), parameters);
	}
	
	
	/**
	 * Takes in a procedure and an arrya of parameters to be passed to the stored procedure
	 * @param p	The procedure to call
	 * @param parameters An array of objects to be passed to the stored procedure
	 * @return	True or false depending on if the stored procedure executed successfully without
	 * 			errors
	 */
	public boolean executeStoredProcedure(DatabaseProcedure p, Object... parameters) {
		return executeStoredProcedure(p.toString(), parameters);
	}
	
	private boolean executeStoredProcedure(String procedureName, Object... parameters) {
		boolean success = false;
		Connection conn = null;
		CallableStatement cs = null;
		try {
			conn = getConnection();
			cs = conn.prepareCall("{call " + procedureName + "}");
			for(int i = 1; i < parameters.length; i++) {
				Object param = parameters[i-1];
				bindWithDatatype(param, i, conn, cs);
			}
			success = cs.executeUpdate() > 0;
		}
		catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeCallableStatement(cs);
			closeConnection(conn);
		}
		return success;
	}
	
	/**
	 * 
	 * @param pkg
	 * @param procedureName
	 * @param parameters
	 * @return
	 */
	public MyResultSet executeCursorProcedure(DatabasePackage pkg, String procedureName, Object... parameters) {
		return executeCursorProcedure(pkg.call(procedureName), parameters);
	}
	
	/**
	 * 
	 * @param databaseProcedure
	 * @param parameters
	 * @return
	 */
	public MyResultSet executeCursorProcedure(DatabaseProcedure databaseProcedure, Object... parameters) {
		return executeCursorProcedure(databaseProcedure.toString(), parameters);
	}
	
	private MyResultSet executeCursorProcedure(String procedureName, Object... parameters) {
		int cursorPos = parameters.length+1;
		Connection conn = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		MyResultSet results = null;
		System.out.println(procedureName);
		try {
			conn = getConnection();
			cs = conn.prepareCall("{call " + procedureName + "}");
			for(int i = 1; i <= parameters.length; i++) {
				Object p = parameters[i-1];
				bindWithDatatype(p, i, conn, cs);
			}
			cs.registerOutParameter(cursorPos, OracleTypes.CURSOR);
			cs.execute();
			rs = (ResultSet) cs.getObject(cursorPos);
			results = MyResultSet.toMyResultSet(rs);
		}
		catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			closeResultSet(rs);
			closeCallableStatement(cs);
			closeConnection(conn);
		}
		return results;
	}
	
	private void closeConnection(Connection conn) {
		if(conn != null) {
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void closeCallableStatement(CallableStatement cs) {
		if(cs != null) {
			try {
				cs.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void closeResultSet(ResultSet rs) {
		if(rs != null) {
			try {
				rs.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private static void bindWithDatatype(Object p, int i, Connection conn, CallableStatement cs) throws SQLException {
		if(p instanceof Date) {
			cs.setDate(i, (Date) p);
		}
		else if(p instanceof Integer || canParseInt(p)) {
			cs.setInt(i, Integer.parseInt((String) p));
		}
		else if(p instanceof Double) {
			cs.setDouble(i, (Double) p);
		}
		else if(p instanceof MyClob) {
			Clob c = conn.createClob();
			c.setString(1, ((MyClob) p).getValue());
			cs.setClob(i, c);
		}
		else if(p instanceof DatabaseObject) {
			cs.setString(i, ((DatabaseObject) p).getPrimaryKey());
		}
		else {
			cs.setString(i, p.toString());
		}
	}
	
	private static boolean canParseInt(Object i) {
		try {
			Integer.parseInt((String)i);
			return true;
		}
		catch(NumberFormatException | ClassCastException e) {
			return false;
		}
	}
}
